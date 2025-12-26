#!/bin/bash
# This script runs the application with the necessary environment setup.
export $(grep -v '^#' .env | xargs)
echo "Shell: $SHELL"
echo "User: $(whoami)"
echo "PATH: $PATH"
which java
java -version
ls -l target/pixelmate-0.0.2.jar
APP_VERSION=$(echo $APP_VERSION | tr -d ' \n\r')
echo $APP_VERSION
java @jvm-options.txt -Dspring.profiles.active=dev -jar target/pixelmate-$APP_VERSION.jar