# Sumobots
[![Build Status](https://travis-ci.org/InspireRobotics/sumobots.svg?branch=master)](https://travis-ci.org/InspireRobotics/sumobots)
[![Build status](https://ci.appveyor.com/api/projects/status/1t9x73sn30a554pb/branch/master?svg=true)](https://ci.appveyor.com/project/DevOrc/sumobots/branch/master)

This repository has all of the code for the Inspire Robotics Sumobots competition. This includes the FMS code, driver station code, and robot code. 

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

# Docs
Currently the only documentation we have is the [FMS Reference Guide](https://docs.google.com/document/d/1qiZn4luMMmQuMcA1el_nNRmyWFzrsatD_j4kDK5Ss3E/edit?usp=sharing) which is still being written. The goal is have a decent amount of documentation for the 0.3 release (July 4th, 2018). To keep up to date on current documentation visit the [tracking issue](https://github.com/InspireRobotics/sumobots/issues/50).

# Tests
Please note the tests for the repository. Every time a commit is made to the master branch of the repository Travis CI will run a series of tests to verify the code. If a single one of these test fails the Travis the build will fail! These tests include

- Testing if the code will compile
- Tests written FXMLFileLoader certain areas of the code
- Tests verifying the GUI will load 
- Verifying the format off all the java code.



In addition to any commits, Travis CI will also run the same checks on every pull request. GitHub will not allow a merge to be made unless all checks pass!

### How Do I make sure my code will pass the tests?
Open the root directory of the repository then run the verify commit script:

```cmd
$ verifyc.bat
```

# Questions?
If you have any questions you can email [inspirerobotics4283@gmail.com](mailto:inspirerobotics4283@gmail.com) or file a GitHub issue with the 'Question' label.
