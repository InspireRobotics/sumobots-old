@echo Running module %1

cd sumobots/%1/target
call java -jar %1.jar
@cd ../../../
