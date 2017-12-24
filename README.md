# Sumobots
Repository for all of the Sumo bot code. This includes the FMS code, driver station code, and robot code. 

# Directories
There are four main directories for code in this repository. They are Driver Station, Robot, Library, and Field.

## Field
The field folder holds all of the code for the FMS. This will start a server for the Driver Stations to connect to.

## Driver Station
This folder contains the code for the driver station. Currently all it does is connect to the FMS Server. 

## Library
This folder contains all of the code shared by the Robot, Driver Station, and Field. Currently it contains Networking utilities and Concurrent channels

## Robot
This folder will contain all of the code for the robot. Currently this is empty.