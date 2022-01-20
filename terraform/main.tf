// IAM 

resource "aws_iam_role" "ecs_task_execution_role" {
  name = "${local.app_name}-ecsTaskExecutionRole"
 
  assume_role_policy = <<EOF
{
 "Version": "2012-10-17",
 "Statement": [
   {
     "Action": "sts:AssumeRole",
     "Principal": {
       "Service": "ecs-tasks.amazonaws.com"
     },
     "Effect": "Allow",
     "Sid": ""
   }
 ]
}
EOF
}
 
resource "aws_iam_role_policy_attachment" "ecs-task-execution-role-policy-attachment" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role" "ecs_task_role" {
  name = "${local.app_name}-ecsTaskRole"
 
  assume_role_policy = <<EOF
{
 "Version": "2012-10-17",
 "Statement": [
   {
     "Action": "sts:AssumeRole",
     "Principal": {
       "Service": "ecs-tasks.amazonaws.com"
     },
     "Effect": "Allow",
     "Sid": ""
   }
 ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "ecs_task_policy_attach" {
  role       = aws_iam_role.ecs_task_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

// ELB

resource "aws_lb" "this" { 
  name               = "library-${var.stage}"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [ aws_security_group.lb.id ]
  subnets            = nonsensitive(data.tfe_outputs.account.values.vpc_public_subnets)
}

resource "aws_lb_listener" "this" { 
  load_balancer_arn = aws_lb.this.arn
  protocol          = "HTTP"
  port              = 8080

  default_action { 
    type             = "forward"
    target_group_arn = aws_lb_target_group.this.arn
  }
}

resource "aws_lb_target_group" "this" { 
  name      = local.app_name
  port      = 8080
  protocol  = "HTTP"
  vpc_id    = nonsensitive(data.tfe_outputs.account.values.vpc_id)
  target_type = "ip"

  health_check { 
    enabled             = true
    healthy_threshold   = 3
    interval            = 10
    matcher             = "200-299"
    path                = "/actuator/health"
    protocol            = "HTTP"
    unhealthy_threshold = 2
  }
}

// ECS

resource "aws_ecs_task_definition" "this" {
  network_mode             = "awsvpc"
  family                   = local.app_name
  requires_compatibilities = ["FARGATE"]
  cpu                      = local.service_cpu
  memory                   = local.service_ram
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn

  container_definitions = templatefile(
  "${path.module}/task-definition.json", {
    "name" = local.app_name,
    "image" = var.container_uri,
    "ram" = local.service_ram,
    "cpu" = local.service_cpu,
    "region" = data.aws_region.current.name,
    "app_name" = local.app_name,
    "db_host" = nonsensitive(data.tfe_outputs.db.values.db_host),
    "db_name" = nonsensitive(data.tfe_outputs.db.values.db_name),
    "db_username" = nonsensitive(data.tfe_outputs.db.values.db_username),
    "db_password" = nonsensitive(data.tfe_outputs.db.values.db_password)
  })
}

resource "aws_ecs_service" "main" {
  name                               = "${local.app_name}"
  cluster                            = nonsensitive(data.tfe_outputs.account.values.ecs_cluster_arn)
  task_definition                    = aws_ecs_task_definition.this.arn
  desired_count                      = local.service_instances
  deployment_minimum_healthy_percent = 50
  deployment_maximum_percent         = 100
  launch_type                        = "FARGATE"
  scheduling_strategy                = "REPLICA"

  network_configuration {
    security_groups  = [nonsensitive(data.tfe_outputs.account.values.cluster_security_group_id),aws_security_group.this.id]
    subnets          = nonsensitive(data.tfe_outputs.account.values.vpc_private_subnets)
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.this.arn
    container_name   = local.app_name
    container_port   = 8080
  }
}

resource "aws_cloudwatch_log_group" "this" {
  name = local.app_name
}

// Security Group

resource "aws_security_group" "this" { 
    name                 = "${local.app_name}"
    description          = "Tier security group for ${local.app_name}"
    vpc_id               = nonsensitive(data.tfe_outputs.account.values.vpc_id)
    
    ingress {
        protocol        = "tcp"
        from_port       = 8080
        to_port         = 8080
        security_groups = [ nonsensitive(data.tfe_outputs.account.values.cluster_security_group_id),aws_security_group.lb.id ]
    }

    ingress {
        protocol        = "tcp"
        from_port       = 8080
        to_port         = 8080
        self            = true
    }

    egress {
        from_port        = 0
        to_port          = 0
        protocol         = "-1"
        cidr_blocks      = ["0.0.0.0/0"]
    }
}


resource "aws_security_group" "lb" { 
    name                 = "${local.app_name}-elb"
    description          = "ELB Security group for ${local.app_name}"
    vpc_id               = nonsensitive(data.tfe_outputs.account.values.vpc_id)
    
    ingress {
        protocol        = "tcp"
        from_port       = 8080
        to_port         = 8080
        cidr_blocks      = ["0.0.0.0/0"]
    }

    ingress {
        protocol        = "tcp"
        from_port       = 80
        to_port         = 80
        cidr_blocks      = ["0.0.0.0/0"]
    }

    ingress {
        protocol        = "tcp"
        from_port       = 443
        to_port         = 443
        cidr_blocks      = ["0.0.0.0/0"]
    }

    egress {
        from_port        = 0
        to_port          = 0
        protocol         = "-1"
        cidr_blocks      = ["0.0.0.0/0"]
    }
}
