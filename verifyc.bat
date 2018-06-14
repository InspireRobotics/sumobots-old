cd sumobots
cls

:: Print info screen
@echo.
@echo Verifying Commit

:: Test the code, if it fails exit the program
call mvn test || cd.. && exit /b

:: Now that tests have passed, we should format the code
call mvn spotless:apply

:: Move back to root directory
cd ..

:: Show the end of verification screen
cls
@echo.
@echo.
@echo Commit verified
