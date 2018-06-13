cls
@echo Building jar files... 

cd sumobots

:: Clean out old files
call mvn clean

:: Let's not package everything
call mvn package
cls

:: Cleaning up broken jar files   
@echo Cleaning up jar files
cd driverstation/target
del driverstation-0.2.1.jar
ren driverstation-0.2.1-jar-with-dependencies.jar driverstation.jar
cd ../../field/target
del field-0.2.1.jar
ren field-0.2.1-jar-with-dependencies.jar field.jar
cd ../../display/target
del display-0.2.1.jar
ren display-0.2.1-jar-with-dependencies.jar display.jar

:: Go back to original folder and clear console
cd ../../..
cls
@echo Finished creating executables...

:: Prevents cmd from closing...
@PAUSE
cls