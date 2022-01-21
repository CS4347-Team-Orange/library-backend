
// Canary Common

resource "aws_s3_bucket" "canaries" { 
    bucket = "${local.app_name}-canaries"
    acl    = "private"

    force_destroy = true
    
    versioning { 
        enabled = true
    }

    lifecycle_rule {
    enabled = true

    noncurrent_version_expiration {
      days = 60
    }
  }
}



resource "aws_s3_bucket_policy" "canaries_bucket_policy" {
  bucket = aws_s3_bucket.canaries.id
  policy = jsonencode({
    Version = "2012-10-17"
    Id = "CanaryBucketPolicy"
    Statement = [
      {
        Sid = "Permissions"
        Effect = "Allow"
        Principal = {
          AWS = data.aws_caller_identity.current.account_id
        }
        Action = ["s3:*"]
        Resource = ["${aws_s3_bucket.canaries.arn}/*"]
      }
    ]
  })
}

data "aws_iam_policy_document" "canary_assume_role_policy" {
  statement {
    actions = ["sts:AssumeRole"]
    effect = "Allow"

    principals {
      identifiers = ["lambda.amazonaws.com"]
      type = "Service"
    }
  }
}


data "aws_iam_policy_document" "canary_policy" {
  statement {
    sid = "Canary"
    effect = "Allow"
    actions = [
      "s3:PutObject",
      "s3:GetBucketLocation",
      "s3:ListAllMyBuckets",
      "cloudwatch:PutMetricData",
      "logs:CreateLogGroup",
      "logs:CreateLogStream",
      "logs:PutLogEvents",
      "events:*"
    ]
    resources = ["*"]
  }
}


resource "aws_iam_policy" "canary_policy" {
  name = "${local.app_name}-canary-policy"
  path = "/"
  policy = data.aws_iam_policy_document.canary_policy.json
  description = "IAM role for AWS Synthetic Monitoring Canaries"
}

resource "aws_iam_role" "canary_role" {
  name = "${local.app_name}-canary-role"
  path = "/"
  assume_role_policy = data.aws_iam_policy_document.canary_assume_role_policy.json
  description = "IAM role for AWS Synthetic Monitoring Canaries"
}

resource "aws_iam_role_policy_attachment" "canary-policy-attachment" {
  role = aws_iam_role.canary_role.name
  policy_arn = aws_iam_policy.canary_policy.arn
}

// http://LB:8080/api/books in 300 seconds or less

resource "local_file" "canary_api_inline" {
    content  = templatefile("${path.module}/canary_node.tpl", {
      protocol = "http",
      hostname = aws_lb.this.dns_name,
      port = "8080",
      path = "api/books/"
    })
    filename = "${path.module}/pageLoadBlueprint-api.js"
}

data "local_file" "canary_api_inline" { 
    depends_on = [ local_file.canary_api_inline ]
    filename = "${path.module}/pageLoadBlueprint-api.js"
}

data "archive_file" "canary_api_inline" {
  type        = "zip"
  output_path = "/tmp/canary_api_inline.zip"
  
  source {
    content = "${data.local_file.canary_api_inline.content}"
    filename = "nodejs/node_modules/pageLoadBlueprint.js"
  }
}


resource "aws_synthetics_canary" "api" {
  depends_on           = [ data.archive_file.canary_api_inline ]
  name                 = "library-${var.stage}-api"
  artifact_s3_location = "s3://${aws_s3_bucket.canaries.id}/canary/apibook"
  execution_role_arn   = aws_iam_role.canary_role.arn
  handler              = "pageLoadBlueprint.handler"
  zip_file             = "/tmp/canary_api_inline.zip"
  runtime_version      = "syn-nodejs-puppeteer-3.3"
  start_canary         = true

  success_retention_period = 2
  failure_retention_period = 14

  schedule {
    expression = "rate(5 minutes)"
  }
    
  run_config {
    timeout_in_seconds = 300
    active_tracing = false
  }
}

resource "aws_cloudwatch_event_rule" "canary_event_rule_api_book" {
  name = "library-${var.stage}-api"
  event_pattern = jsonencode({
    source = ["aws.synthetics"]
    detail = {
      "canary-name": [aws_synthetics_canary.api.name],
      "test-run-status": ["FAILED"]
    }
  })
}

resource "aws_cloudwatch_event_target" "canary_event_target_api_book" {
  target_id = "${local.app_name}-api-book-target"
  arn = nonsensitive(data.tfe_outputs.account.values.sns_alerts_arn)
  rule = aws_cloudwatch_event_rule.canary_event_rule_api_book.name
}

// Canary: Actuator Healthcheck

resource "local_file" "canary_healthcheck_inline" {
    content  = templatefile("${path.module}/canary_node.tpl", {
      protocol = "http",
      hostname = aws_lb.this.dns_name,
      port = "8080",
      path = "actuator/health"
    })
    filename = "${path.module}/pageLoadBlueprint-hc.js"
}

data "local_file" "canary_healthcheck_inline" { 
    depends_on = [ local_file.canary_healthcheck_inline ]
    filename = "${path.module}/pageLoadBlueprint-hc.js"
}

data "archive_file" "canary_healthcheck_inline" {
  type        = "zip"
  output_path = "/tmp/canary_healthcheck_inline.zip"
  
  source {
    content = "${data.local_file.canary_healthcheck_inline.content}"
    filename = "nodejs/node_modules/pageLoadBlueprint.js"
  }
}


// http://LB:8080/actuator/health in 60 seconds or less
resource "aws_synthetics_canary" "health" {
  depends_on           = [ data.archive_file.canary_healthcheck_inline ]
  name                 = "library-${var.stage}-hc"
  artifact_s3_location = "s3://${aws_s3_bucket.canaries.id}/canary/health"
  execution_role_arn   = aws_iam_role.canary_role.arn
  handler              = "pageLoadBlueprint.handler"
  zip_file             = "/tmp/canary_healthcheck_inline.zip"
  runtime_version      = "syn-nodejs-puppeteer-3.3"
  start_canary         = true

  success_retention_period = 2
  failure_retention_period = 14

  schedule {
    expression = "rate(5 minutes)"
  }
    
  run_config {
    timeout_in_seconds = 60
    active_tracing = false
  }
}

resource "aws_cloudwatch_event_rule" "canary_event_rule_health" {
  name = "${local.app_name}-health"
  event_pattern = jsonencode({
    source = ["aws.synthetics"]
    detail = {
      "canary-name": [aws_synthetics_canary.health.name],
      "test-run-status": ["FAILED"]
    }
  })
}

resource "aws_cloudwatch_event_target" "canary_event_target_health" {
  target_id = "${local.app_name}-health-target"
  arn = nonsensitive(data.tfe_outputs.account.values.sns_alerts_arn)
  rule = aws_cloudwatch_event_rule.canary_event_rule_health.name
}

// Cloudwatch Alarms

// Cloudwatch Dashboard 
resource "aws_cloudwatch_dashboard" "dashboard" {
  dashboard_name = local.app_name

  dashboard_body = templatefile("cloudwatch_dash.tpl", {
      "cluster_name" = nonsensitive(data.tfe_outputs.account.values.ecs_cluster_name),
      "service_name" = local.app_name,
      "stage"        = var.stage,
      "region"       = data.aws_region.current.name,
      "ram_high"     = floor(local.service_ram * 0.9),
      "cpu_high"     = floor(local.service_cpu * 0.9),
      "target_group" = split(":", aws_lb_target_group.this.arn)[5],
      "load_balancer" = join("/", tolist(slice(tolist(split("/", aws_lb.this.arn)), 1, 4)))
  })
}