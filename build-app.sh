#!/bin/bash
export $(grep -v '^#' .env | xargs)
sh -c "mvn clean package -DskipTests"