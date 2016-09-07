package com.andrej;

import java.util.*;

public class Main {

    // HashMap to contain all the rooms collected from the XML file, using ids as map keys.
    static HashMap<Integer, Room> roomMap;

    // The Map which will collect the name and id of each room the program goes through.
    static LinkedHashMap<Integer, String> roomRoute = new LinkedHashMap<>();

    // The Map which will collect the objects found by the program as well as their corresponding room id numbers.
    static LinkedHashMap<Integer, String> roomObject = new LinkedHashMap<>();

    public static void main(String[] args) {

        if(args.length < 3){

            System.out.println("Please provide a map file name, a task (config) file, and an output file name.");

        }else{

            // Retrieve data for each room in the map.xml file.
            roomMap = XMLParser.readFromXMLFile(args[0]);

            // Read the user's chosen config.txt file and run the method to collect all the objects.
            findRoute(ConfigFileReader.getStartingRoom(args[1]), ConfigFileReader.getRequiredObjects(args[1]));

            // Finally, write the resulting route to an XML file.
            XMLParser.writeToXMLFile(args[2]);
        }

    }

    /*
     * This method finds a route for picking up the objects specified in the config.txt file.
     * The parameters are a starting room, and a list of objects to look for.
     *
     * While it runs, this method will update the 'roomRoute' and 'roomObject' Maps whenever
     * a new room is entered, or an object found, respectively. This data will later be
     * written to an XML file.
     *
     * The route determined by this program will never enter the same room twice, unless
     * the room in question is the only exit out of another room.
     */
    public static void findRoute(int startingRoom, List<String> requiredObjects){

        int currentRoom = startingRoom;

        // Initialize two lists. One containing all the rooms which we need to visit to complete the program,
        // and one to mark all the rooms that have been visited already
        ArrayList<Integer> destinationRooms = new ArrayList<>();
        ArrayList<Integer> visitedRooms = new ArrayList<>();

        // Find out which rooms we need to go to, to acquire the objects. They will be listed inside 'destinationRooms'
        //--------------------------------------------------------
        for(int id : roomMap.keySet()){
            Room room = roomMap.get(id);
            for(String object : room.getObjectList()){
                if(requiredObjects.contains(object)){
                    destinationRooms.add(id);
                }
            }
        }
        //--------------------------------------------------------

        /*
         * Start a logic loop to last until all the objects have been collected.
         * Every time we move to a different room, the loop is restarted.
         *
         * The loop will only stop once all the objects have been found.
         */
        parentloop:
        while(true){

            // Initialize a Room class using the currentRoom integer.
            Room room = roomMap.get(currentRoom);

            // Add the current room into roomRoute.
            roomRoute.put(currentRoom, roomMap.get(currentRoom).getName());

            // Check if the current room has any of the items we need.
            // If the current room is a 'Destination Room' then it definitely contains an object that we need.
            // If it does, then remove the object from 'requiredObjects' and notify the user.
            //----------------------------------------------------------------

            if(destinationRooms.contains(currentRoom)){

                // Iterate through each of the objects in this room.
                for(String object : room.getObjectList()){

                    // If one of the objects is a 'requiredObject' remove it from the
                    // 'requiredObjects' List to simulate having picked it up.
                    if(requiredObjects.contains(object)){

                        requiredObjects.remove(object);
                        roomObject.put(currentRoom, object);

                        System.out.println("Found the " + object + "!");
                        // Un-designate this room as a 'Destination Room', so as not to interfere
                        // with the program's pathfinding.
                        destinationRooms.remove(destinationRooms.indexOf(currentRoom));

                        // If this is the final object that had to be collected, end the method.
                        if(requiredObjects.isEmpty()){
                            System.out.println("Search Finished!");
                            break parentloop;
                        }
                    }
                }
            }
            //----------------------------------------------------------------

            // Check if the current room is empty and if it has only one exit.
            // If this is the case, immediately move to that exit.
            // Update currentRoom accordingly.
            //----------------------------------------------------------------
            if(room.getObjectList().isEmpty() && room.getExits().size()==1){
                visitedRooms.add(currentRoom);
                currentRoom  = room.getExits().entrySet().iterator().next().getValue();
                System.out.println("Moved to room " + currentRoom);
                continue;
            }
            //----------------------------------------------------------------

            // Check if any of the connecting rooms is a 'Destination Room' ie: It contains an item we need.
            // If a neighboring room qualifies, move to it.
            //----------------------------------------------------------------
            for(int roomNumber : room.getExits().values()){

                if(destinationRooms.contains(roomNumber)){
                    visitedRooms.add(currentRoom);
                    currentRoom  = roomNumber;
                    System.out.println("Moved to room " + roomNumber);
                    continue parentloop;
                }
            }
            //----------------------------------------------------------------

            // Do an operation to check if any of the surrounding rooms are connected to a 'Destination Room'
            //----------------------------------------------------------------
            for(int roomNumber : room.getExits().values()){
                Room nextRoom = roomMap.get(roomNumber);

                for(int secondRoomNumber : nextRoom.getExits().values()){

                    if(destinationRooms.contains(secondRoomNumber)){
                        visitedRooms.add(currentRoom);
                        currentRoom  = roomNumber;
                        System.out.println("Moved to room " + roomNumber);
                        continue parentloop;
                    }
                }
            }
            //----------------------------------------------------------------
            /*
             *  The above code will work perfectly, provided your starting location is at most
             *  2 rooms away from the required object. But what if the object we need is further
             *  away than that (for example, you start in the living room (7), and need to get
             *  to the bathroom (6))?
             *
             *  In this case, the program should randomly search through connecting rooms until
             *  it gets to the correct room. But in order to avoid getting caught in an infinite
             *  loop of leaving and re-entering the same room, we need to use a list of
             *  'Visited Rooms' which the program should stay away from.
             *
             */
            //----------------------------------------------------------------
            for(int roomNumber : room.getExits().values()){

                // Avoid entering an already visited room.
                if(!visitedRooms.contains(roomNumber)){
                    visitedRooms.add(currentRoom);
                    currentRoom  = roomNumber;
                    System.out.println("Moved to room " + roomNumber);
                    continue parentloop;
                }
            }
            //----------------------------------------------------------------

            /*
             * NOTE:
             *
             * The following is a potential fail safe operation in the case that we are
             * using a larger room map, and somehow the program gets stuck in a room with
             * more than one exit, and all exits have been 'visited' before.
             *
             * This will never happen with the current map, so I have left it commented out
             * to improve performance. However, if a larger, custom map was used instead
             * of the current one, this operation could possibly be necessary.
             * -----------------------------------------------------------------------------
             *
             *   if(visitedRooms.containsAll(room.getExits().values())){
             *
             *       visitedRooms.add(currentRoom);
             *
             *       // Use a random number to determine which exit to take
             *       Random random = new Random();
             *       int randomExit = random.nextInt(room.getExits().size()-1);
             *
             *       // Get a list of possible room exits.
             *       List<Integer> exits = (List<Integer>) room.getExits().values();
             *
             *       // Randomly pick an exit from the list.
             *       currentRoom  = exits.get(randomExit);
             *       System.out.println("Moved to room " + currentRoom);
             *
             *       continue parentloop;
             *   }
             *
             * -----------------------------------------------------------------------------
             */

        }

    }

}
