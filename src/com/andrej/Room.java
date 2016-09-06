package com.andrej;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Model class for each room in the map.
 */
public class Room {


    private String name;
    // HashMap listing all possible exits from the room, with the corresponding direction.
    private HashMap<String, Integer> exits = new HashMap<>();
    // ArrayList containing all the objects in the room.
    private ArrayList<String> objectList;

    public Room(String name, HashMap<String, Integer> possibleExits, ArrayList<String> objectList){

        this.name = name;
        for(String direction : possibleExits.keySet()){
            exits.put(direction, possibleExits.get(direction));
        }
        this.objectList = objectList;
    }


    String getName() {
        return name;
    }

    HashMap<String, Integer> getExits() { return exits; }

    ArrayList<String> getObjectList() {
        return objectList;
    }
}
