#!/bin/bash
sudo kill -9 $(sudo lsof -i:8085 | grep java | awk '{print $2}')