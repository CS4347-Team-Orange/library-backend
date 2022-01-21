variable "stage" { 
    description = "Stage of deployment, eg dev|qa|live"
    default     = "local"
    type        = string
}

variable "container_uri" { 
    description = "The full URI of the container, eg registry.hub.docker.com/alex4108/library-db:latest"
    default     = "registry.hub.docker.com/alex4108/library-backend:latest"
    type        = string
}

variable "tf_account_workspace" { 
    description = "The Terraform Cloud workspace where the account module is deployed"
    default     = ""
    type        = string
}

variable "tf_db_workspace" { 
    description = "The Terraform Cloud workspace where the db module is deployed"
    default     = ""
    type        = string
}

variable "tf_org" { 
    description = "The Terraform Cloud organization"
    default     = "alex4108"
    type        = string
}

# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task-cpu-memory-error.html

variable "service_cpu" { 
    description = "The amount of CPU to allocate to the service (See AWS ECS Fargate Task Definition)"
    default     = 512
    type        = number
}

variable "service_ram" { 
    description = "The amount of RAM to allocate to the service (See AWS ECS Fargate Task Definition)"
    default     = 1024
    type        = number
}

variable "service_instances" { 
    description = "The number of instances for this deployment."
    default     = 2
    type        = number
}


locals { 
    tf_account_workspace = (var.tf_account_workspace == "") ? "library-${var.stage}" : var.tf_account_workspace
    tf_db_workspace = (var.tf_db_workspace == "") ? "library-db-${var.stage}" : var.tf_db_workspace
    app_name = "library-backend-${var.stage}"
    
    service_cpu       = var.service_cpu
    service_ram       = var.service_ram
    service_instances = var.service_instances

    service_discovery_root = "${var.stage}.library.${data.aws_caller_identity.current.account_id}.aws"

    db_username = "library"
    db_name     = "library"
}