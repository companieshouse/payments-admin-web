resource "aws_lb" "dev-specs-alb" {
  name            = "dev-specs-${var.environment}-lb"
  security_groups = [aws_security_group.dev-site-sg.id]
  subnets         = var.subnet_ids
  internal        = var.internal_albs
}

resource "aws_lb_listener" "dev-specs-alb-listener" {
  load_balancer_arn = aws_lb.dev-specs-alb.arn
  port              = "443"
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-2016-08"
  certificate_arn   = var.ssl_certificate_id
  default_action {
    type = "fixed-response"
    fixed_response {
      content_type = "text/plain"
      message_body = "OK"
      status_code  = "200"
    }
  }
}

resource "aws_route53_record" "dev-specs-r53-record" {
  count   = "${var.zone_id == "" ? 0 : 1}" # zone_id defaults to empty string giving count = 0 i.e. not route 53 record
  zone_id = var.zone_id
  name    = "developer-specs${var.external_top_level_domain}"
  type    = "A"
  alias {
    name                   = aws_lb.dev-specs-alb.dns_name
    zone_id                = aws_lb.dev-specs-alb.zone_id
    evaluate_target_health = false
  }
}
