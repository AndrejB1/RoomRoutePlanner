package com.andrej;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static java.util.logging.Level.WARNING;

/*
 * This class handles all XML related input and output.
 */
class XMLParser {

    // Logging setup for keeping track of exceptions
    private static LogManager lm = LogManager.getLogManager();
    private static Logger logger = lm.getLogger(Logger.GLOBAL_LOGGER_NAME);

    // Method for reading and parsing an XML file.
    static HashMap<Integer, Room> readFromXMLFile(String filename){

        HashMap<Integer, Room> roomMap = new HashMap<>();

        File inputFile = new File(filename);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();



        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);

            NodeList roomNodes = doc.getElementsByTagName("room");
            for(int i=0; i<roomNodes.getLength(); i++){
                Node roomNode = roomNodes.item(i);

                // Initialize placeholders for each of the directions.
                int north = 0;
                int east = 0;
                int south = 0;
                int west = 0;

                // An ArrayList to hold the objects contained within the rooms (child elements of 'room')
                ArrayList<String> objectList = new ArrayList<>();

                if(roomNode.getNodeType()==Node.ELEMENT_NODE){

                    Element e = (Element) roomNode;

                    // Get the room's id number.
                    int id = Integer.parseInt(e.getAttribute("id"));

                    // Acquire the room id numbers corresponding to each of the 4 cardinal directions.
                    String name = e.getAttribute("name");
                    if(!e.getAttribute("north").equals(""))
                        north = Integer.parseInt(e.getAttribute("north"));
                    if(!e.getAttribute("east").equals(""))
                        east = Integer.parseInt(e.getAttribute("east"));
                    if(!e.getAttribute("south").equals(""))
                        south = Integer.parseInt(e.getAttribute("south"));
                    if(!e.getAttribute("west").equals(""))
                        west = Integer.parseInt(e.getAttribute("west"));

                    // Obtain a NodeList of all the objects in the room, and add each of them to objectList.
                    NodeList childNodes = e.getChildNodes();

                    for(int j=0; j<childNodes.getLength(); j++){
                        Node n = childNodes.item(j);
                        if(n.getNodeType()==Node.ELEMENT_NODE){

                            Element o = (Element) n;

                            String object = o.getAttribute("name");
                            objectList.add(object);
                        }
                    }

                    // Make sure that none of the 4 cardinal directions are 0 (meaning they don't lead anywhere)
                    // before adding them to the possibleRoomExits Map.
                    HashMap<String, Integer> possibleRoomExits = new HashMap<>();
                    if(north!=0)
                        possibleRoomExits.put("north", north);
                    if(east!=0)
                        possibleRoomExits.put("east", east);
                    if(south!=0)
                        possibleRoomExits.put("south", south);
                    if(west!=0)
                        possibleRoomExits.put("west", west);

                    // Combine the elements into one Room class object and add it to roomMap.
                    Room room = new Room(name, possibleRoomExits, objectList);
                    roomMap.put(id, room);
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            logger.log(WARNING, "There was an error while parsing the file", e);
        } catch (IOException e) {
            logger.log(WARNING, "There was an error while reading from the file", e);
        }
        return roomMap;
    }

    // Method for writing the results of the findRoute method from Main, to a new XML file.
    static void writeToXMLFile(String filename){
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // Root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("route");
            doc.appendChild(rootElement);

            // Loop through the roomRoute Map from Main, and make each room an element.
            for(int key : Main.roomRoute.keySet()){
                Element route = doc.createElement("room");
                rootElement.appendChild(route);

                // Write the names and ids of the rooms as XML elements.
                route.setAttribute("id", String.valueOf(key));
                route.setAttribute("name", Main.roomRoute.get(key));

                // If the roomObject Map from Main contains an object corresponding to this key, list it
                // as a child element.
                if(Main.roomObject.containsKey(key)){
                    Element object = doc.createElement("object");
                    object.setAttribute("name", Main.roomObject.get(key));
                    route.appendChild(object);
                }
            }

            // Write the content into XML file, with UTF-8 encoding and proper indentation.
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filename));

            transformer.transform(source, result);

            System.out.println("File saved!");
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            logger.log(WARNING, "There was an error while transforming the data into an XML format.", e);
        } catch (ParserConfigurationException e) {
            logger.log(WARNING, "Parser configuration error", e);
        }
    }
}
