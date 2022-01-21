data "aws_region" "current" {}

data "aws_caller_identity" "current" {}

data "tfe_outputs" "account" {
  organization = var.tf_org
  workspace    = local.tf_account_workspace
}
data "tfe_outputs" "db" {
  organization = var.tf_org
  workspace    = local.tf_db_workspace
}