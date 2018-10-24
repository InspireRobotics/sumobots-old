# Sumobots
[![Build Status](https://travis-ci.org/InspireRobotics/sumobots.svg?branch=master)](https://travis-ci.org/InspireRobotics/sumobots)
[![Build status](https://ci.appveyor.com/api/projects/status/1t9x73sn30a554pb/branch/master?svg=true)](https://ci.appveyor.com/project/DevOrc/sumobots/branch/master)

This repository has all of the code for the Inspire Robotics Sumobots competition. This includes the FMS code, driver station code, and robot code. The FMS and Driver station are only tested on Windows and the robot code is only designed for the Raspberry Pi 3b

### State of the Project
This project is generally used during the spring and summer because that is when INSPIRE Robotics does there sumobot activites. The project has not been abonded but paused. Development will probably began to pick up in January or so

# Commands
There are two batch scripts used to build and run the java programs.
To build executable jars run the following command:
``` cmd
$ build.bat
```
This will build the executable jars, delete the unexecutable librarys, and rename the librarys to 'field.jar', 'display.jar' and 'driverstation.jar' respectively.

Once you have done that you can run each module by running the following command
``` cmd
$ run field
$ run driverstation
$ run display
```
__Note:__ These commands only work on windows.
# Questions?
If you have any questions you can email [inspirerobotics4283@gmail.com](mailto:inspirerobotics4283@gmail.com) or file a GitHub issue with the 'Question' label.
