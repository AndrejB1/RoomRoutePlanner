Logic test application for eGym.de

Room Route Planner

By using a combination of several loops it is possible to make an adventure style application which automates the search of certain
objects in a map.

The purpose of the program is as follows:

- An XML "map" is read by the program upon startup. The map file contains a list of rooms with unique names and IDs. Additionally,
  each room contains information on what rooms surround it (in N, S, E, W directions), and each room can hold a list of "items".

- After making a model of the map, the program also reads a "config" file to determine which items it needs to pick up, and which room
  the program will start searching from.
  
- The program is completed when it finds all of the items it needs, and will output the path it took to an XML file.

The program operates as follows:

- The program accepts 3 command line parameters: the map filepath, config filepath, and output filepath.
- The program will never visit the same room twice, unless that room is the only possible exit out of the current one.
- The program will mark each room with a required object as a 'Destination Room'.
- The program determines, within a distance of two rooms, whether or not there is a Destination Room nearby. If it detects one, it
  will make a beeline towards it.
- If there is no Destination Room nearby, it will simply pick the first possible direction to move in. Since the same room is
  never visited twice, a Destination Room is likely to be found quickly with this method.
- On the off chance that the map being used is large with many interconnecting rooms, the program may find itself in a multiple-exit 
  room, surrounded by previously visited rooms. In this case the program will use a random number to determine a direction to move in,
  and will keep doing so until it detects a Destination Room or finds an untravelled path.
