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

#### Allowing 3rd Party App Access in Google Mail
By default, gmail will turn off access to "less secure apps".
Go here while logged in and select "turn on": https://www.google.com/settings/security/lesssecureapps

This will allow email alerts to continue without error within HashAlert. It's recommended to preform an email test by forcing an error. An easy way to do this is to change the "errorMinimum" value to something high (ex. 999), then running the application.
