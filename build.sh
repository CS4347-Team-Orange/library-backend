#!/usr/bin/env bash

set -e # Abort on any error 

PROJECT_NAME="backend" # db, backend, or frontend

# Do what you need to do to prepare for the container's build here.

# Build the container
docker build -t alex4108/library-${PROJECT_NAME} .
