package com.andrej;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class MainTest {

    // Initialize the roomMap.
    @BeforeClass
    public static void before(){
        Main.roomMap = XMLParser.readFromXMLFile("map.xml");
    }

    // Check if a config file can be read properly.
    @Test
    public void getConfig1StartingRoom(){
        int testStartingRoom = ConfigFileReader.getStartingRoom("config1.txt");
        assertEquals(testStartingRoom, 1);
    }

    // Check if all of a room's connections are assembled properly.
    @Test
    public void checkRoomConnections(){
        HashMap<String, Integer> expectedExits = Main.roomMap.get(2).getExits();
        HashMap<String, Integer> actualExits = new HashMap<>();
        actualExits.put("north", 4);
        actualExits.put("east", 3);
        actualExits.put("south", 7);
        actualExits.put("west", 1);

        assertEquals(expectedExits, actualExits);
    }

    // Check if all the correct objects are collected.
    @Test
    public void allObjectsCollectedConfig1Test(){

        HashMap<Integer, String> objects = new HashMap<>();
        objects.put(2, "Scarf");
        objects.put(6, "Soap");

        Main.findRoute(1, ConfigFileReader.getRequiredObjects("config1.txt"));

        assertEquals(Main.roomObject, objects);
    }
}