terraform {
  backend "remote" {}
}

provider "aws" {
    region = "us-east-2"
}