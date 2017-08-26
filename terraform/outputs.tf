output "coveo_challenge_url" {
  value = "http://${module.coveo_challenge_elb.elb_dns_name}"
}