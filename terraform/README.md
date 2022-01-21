# Terraform

These Terraform modules are configured for use in [Terraform Cloud "TFC"](https://app.terraform.io)

You should set the `TF_ORG` appropriately if you're running in your own TFC organization.

# terraform init

The Terraform projects in this system use [Partial Configuration](https://www.terraform.io/language/settings/backends/remote#using-cli-input) of the Terraform Cloud backend.

```
cp config.remote.tfbackend.tpl config.remote.tfbackend
sed -i "s#TF_ORG#alex4108#g" config.remote.tfbackend
sed -i "s#TF_WORKSPACE#library-backend-develop#g" config.remote.tfbackend
sed -i "s#TF_HOST#app.terraform.io#g" config.remote.tfbackend
terraform init -backend-config="config.remote.tfbackend"
```

# terraform plan

```
echo "stage = \"${USER}\"" > ./${USER}.auto.tfvars
echo "container_uri = \"registry.hub.docker.com/alex4108/library-backend:latest\"" >> ./${USER}.auto.tfvars
echo "tf_account_workspace = \"library-${USER}\"" >> ./${USER}.auto.tfvars
echo "tf_db_workspace = \"library-db-${USER}\"" >> ./${USER}.auto.tfvars
echo "tf_org = \"alex4108\"" >> ./${USER}.auto.tfvars
terraform plan
```

