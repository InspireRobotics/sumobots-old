# Sumobots
[![Build Status](https://travis-ci.org/InspireRobotics/sumobots.svg?branch=master)](https://travis-ci.org/InspireRobotics/sumobots)

This repository has all of the code for the Inspire Robotics Sumobots competition. This includes the FMS code, driver station code, and robot code. 

# Docs
The documentation for the field is a WIP. The current documentation is located in the [docs folder.](https://github.com/InspireRobotics/sumobots/tree/master/Docs/)

# Tests
Please note the tests for the repository. Every time a commit is made to the master branch of the repository Travis CI will run a series of tests to verify the code. If a single one of these test fails the Travis the build will fail! These tests include

- Testing if the code will compile
- Tests written FXMLFileLoader certain areas of the code
- Tests verifying the GUI will load 
- Verifying the format off all the java code.



In addition to any commits, Travis CI will also run the same checks on every pull request. GitHub will not allow a merge to be made unless all checks pass!

### How Do I make sure my code will pass the test?
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
