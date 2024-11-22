output "public_site_ip" {
  value = aws_instance.public_site.public_ip
}

output "admin_site_ip" {
  value = aws_instance.admin_site.public_ip
}

output "rds_endpoint" {
  value = aws_db_instance.mysql_rds.endpoint
}

output "s3_bucket_name" {
  value = aws_s3_bucket.admin_bucket.bucket
}