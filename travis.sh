#!/usr/bin/env bash

set -e
set -o pipefail

bash build.sh
bash test.sh
if [[ "${TRAVIS_PULL_REQUEST}" == "false" && "${TRAVIS_BRANCH}" == "master" ]]; then
    docker push alex4108/library-backend
fi
