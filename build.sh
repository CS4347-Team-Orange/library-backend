#!/usr/bin/env bash

set -eo pipefail 

if [[ "${container_image}" == "" && "${TRAVIS_COMMIT}" == "" ]]; then
    container_image="alex4108/library-backend"
fi

docker build -t ${container_image} .

