#!/usr/bin/env bash

set -euo pipefail

echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin

export container_registry="registry.hub.docker.com"
export container_image="alex4108/library-backend:${TRAVIS_COMMIT}"
export container_uri="${container_registry}/${container_image}"

bash build.sh
bash test.sh

docker push ${container_image}

cd terraform && bash ci.sh
