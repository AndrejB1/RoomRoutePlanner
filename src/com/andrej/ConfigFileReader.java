package com.andrej;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for reading the config.txt files, with methods for returning different parts of such files.
 */
class ConfigFileReader {

    private static ArrayList<String> objectiveList;

    private static void readFile(String filename){

        objectiveList = new ArrayList<>();
        BufferedReader bufferedReader = null;

        try {
            FileReader fileReader = new FileReader(filename);
            bufferedReader = new BufferedReader(fileReader);

            String line;
            while((line = bufferedReader.readLine()) != null) {
                objectiveList.add(line);
            }

            bufferedReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: The file " + filename + " cannot be found.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
     * Method for returning only the first value of 'objectiveList'.
     * This integer will be the starting room of the 'findRoute' operation in Main.
     */
    static int getStartingRoom(String filename){
        readFile(filename);
        return Integer.parseInt(objectiveList.get(0));
    }

    /*
     * Method for returning the objects that we want to collect during our 'findRoute' operation in Main.
     */
    static List<String> getRequiredObjects(String filename){
        readFile(filename);
        return objectiveList.subList(1, objectiveList.size());
    }
}
