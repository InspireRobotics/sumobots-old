# Sumobots
[![Build Status](https://travis-ci.org/InspireRobotics/sumobots.svg?branch=master)](https://travis-ci.org/InspireRobotics/sumobots)

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
# Versions
There are multiple versions of the Sumobot code. __The current version, 0.2, is still being developed and is planned to be released by June 1st, 2018__. Following the 0.2 release will be the 0.3 release which is aimed for July 4th, 2018. To keep updated on the current version/future versions check the GitHub issue list. All of the green labels are version related! 

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
Open the root directory of the repository then run:

```cmd
$ cd sumobots
$ mvn test
$ mvn spotless:check
```

If __spotless:check__ fails you can run the following command to format all code:
```cmd
$ mvn spotless:apply
```

# Questions?
If you have any questions you can email [inspirerobotics4283@gmail.com](mailto:inspirerobotics4283@gmail.com) or file a GitHub issue with the 'Question' label.
