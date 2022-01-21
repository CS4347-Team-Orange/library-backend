terraform {
  backend "remote" {}
}

provider "aws" {
  region = "us-east-2"
  default_tags {
    tags = {
      App         = "library"
      Stage       = var.stage
      Owner       = "alex"
    }
  }
}