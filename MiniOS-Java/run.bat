@echo off
cd /d "%~dp0"
java -jar MiniOS.jar
if errorlevel 1 pause
