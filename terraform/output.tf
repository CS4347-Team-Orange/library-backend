output "lb_security_group_id" { 
    value = aws_security_group.lb.id
}

output "lb_id" { 
    value = aws_lb.this.id
}

output "lb_arn" { 
    value = aws_lb.this.arn
}

output "lb_dns" { 
    value = aws_lb.this.dns_name
}