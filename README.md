# HashAlert
An email alert application for EthOS distro.
There really wasn't an effective way to recieve alerts if a miner went down via EthOS. So I wrote this app to solve my issue, as well as for others. Feel free to use, re-distribute, or make changes.

This java application is intended to be ran within a Linux kernel or Windows cmd.

#### Linux and Windows Instructions
Navigate to the directory of the .jar and use:
```
java -jar HashAlert.jar
```

If running on Windows, you may use a .bat for containment:
```
@echo off
java -jar HashAlert.jar
pause
```
#### Requirements
Java 7 or higher