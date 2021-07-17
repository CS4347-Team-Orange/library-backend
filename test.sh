#!/usr/bin/env bash

set -e # Bomb on any error

PROJECT_NAME="backend" # The project's name

# Do any testing here
./gradlew clean build
