resource "aws_lb" "dev-site-alb" {
  name            = "dev-site-${var.environment}-lb"
  security_groups = [aws_security_group.dev-site-sg.id]
  subnets         = var.subnet_ids
  internal        = var.internal_albs
}

resource "aws_lb_listener" "dev-site-alb-listener" {
  load_balancer_arn = aws_lb.dev-site-alb.arn
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

resource "aws_route53_record" "dev-site-r53-record" {
  count   = "${var.zone_id == "" ? 0 : 1}" # zone_id defaults to empty string giving count = 0 i.e. not route 53 record
  zone_id = var.zone_id
  name    = "developer${var.external_top_level_domain}"
  type    = "A"
  alias {
    name                   = aws_lb.dev-site-alb.dns_name
    zone_id                = aws_lb.dev-site-alb.zone_id
    evaluate_target_health = false
  }
}
