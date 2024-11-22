variable "region" {
  description = "AWS region for deployment"
  default     = "us-east-1"
}

variable "instance_type" {
  description = "Instance type for EC2 instances"
  default     = "t2.micro"
}

variable "ami" {
  description = "Amazon Machine Image (AMI) to use for EC2 instances"
  default     = "ami-011899242bb902164"
}

variable "db_name" {
  description = "Name of the RDS MySQL database"
  default     = "ecomdb"
}

variable "db_username" {
  description = "Username for the RDS MySQL database"
  default     = "admin"
}

variable "db_password" {
  description = "Password for the RDS MySQL database"
  type        = string
  sensitive   = true
}

variable "bucket_prefix" {
  description = "Prefix for the S3 bucket"
  default     = "my-simple-bucket"
}

variable "vnpay_secret" {
  description = "VNPay Secret"
  type        = string
  sensitive   = true
}

variable "ghn_token" {
  description = "GHN API Token"
  type        = string
  sensitive   = true
}

variable "google_client_secret" {
  description = "Google Client Secret"
  type        = string
  sensitive   = true
}
