@echo off
cd /d "%~dp0"
if not exist out mkdir out
javac -encoding UTF-8 -source 8 -target 8 -d out *.java
if errorlevel 1 exit /b 1
java -Djava.awt.headless=true -cp out Tests
if errorlevel 1 exit /b 1
java -Djava.awt.headless=true -cp out RubricTests
if errorlevel 1 exit /b 1
jar cfe MiniOS.jar MiniOS -C out .
if errorlevel 1 exit /b 1
