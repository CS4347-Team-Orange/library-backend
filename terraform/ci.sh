#!/usr/bin/env bash

set -eou pipefail

if [[ "${TRAVIS_PULL_REQUEST}" == "false" && "${TRAVIS_BRANCH}" == "develop" ]]; then
    stage="develop"
elif [[ "${TRAVIS_PULL_REQUEST}" == "false" && "${TRAVIS_BRANCH}" == "master" ]]; then
    stage="live"
else
    echo "Deployment not supported for this branch"
    exit 0
fi


TF_WORKSPACE="library-backend-${stage}"

mkdir -p ~/.terraform.d/
echo "{\"credentials\": {\"app.terraform.io\": {\"token\": \"$TF_API_TOKEN\"}}}" > ~/.terraform.d/credentials.tfrc.json

echo "stage = \"${stage}\"" > ./ci.auto.tfvars
echo "container_uri = \"${container_uri}\"" >> ./ci.auto.tfvars
echo "tf_account_workspace = \"library-${stage}\"" >> ./ci.auto.tfvars
echo "tf_db_workspace = \"library-db-${stage}\"" >> ./ci.auto.tfvars
echo "tf_org = \"${TF_ORG}\"" >> ./ci.auto.tfvars

cp config.remote.tfbackend.tpl config.remote.tfbackend
sed -i "s#TF_ORG#${TF_ORG}#g" config.remote.tfbackend
sed -i "s#TF_WORKSPACE#${TF_WORKSPACE}#g" config.remote.tfbackend
sed -i "s#TF_HOST#${TF_HOST}#g" config.remote.tfbackend 

terraform init -backend-config="config.remote.tfbackend"
terraform plan
terraform apply -auto-approve