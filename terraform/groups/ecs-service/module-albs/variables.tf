# ECS Service
variable "stack_name" {
  type        = string
  description = "The name of the Stack / ECS Cluster."
}
variable "name_prefix" {
  type        = string
  description = "The name prefix to be used for stack / environment name spacing."
}

# Environment
variable "environment" {
  type        = string
  description = "The environment name, defined in envrionments vars."
}

# Networking
variable "subnet_ids" {
  type        = list(string)
  description = "Subnet IDs of application subnets from aws-mm-networks remote state."
}
variable "web_access_cidrs" {
  type        = list(string)
  description = "Subnet CIDRs for web ingress rules in the security group."
}
variable "vpc_id" {
  type        = string
  description = "The ID of the VPC for the target group and security group."
}
variable "internal_albs" {
  type        = bool
  description = "Whether the ALBs should be internal or public facing"
}

# DNS
variable "zone_id" {
  type        = string
  description = "The ID of the hosted zone to contain the Route 53 record."
}
variable "external_top_level_domain" {
  type        = string
  description = "The type levelel of the DNS domain for external access."
}

# Certificates
variable "ssl_certificate_id" {
  type        = string
  description = "The ARN of the certificate for https access through the ALB."
}
