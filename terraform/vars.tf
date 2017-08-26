# ---------------------------------------------------------------------------------------------------------------------
# ENVIRONMENT VARIABLES
# Define these secrets as environment variables
# ---------------------------------------------------------------------------------------------------------------------

# AWS_ACCESS_KEY_ID
# AWS_SECRET_ACCESS_KEY

# ---------------------------------------------------------------------------------------------------------------------
# OPTIONAL MODULE PARAMETERS
# These variables have defaults, but may be overridden by the operator.
# ---------------------------------------------------------------------------------------------------------------------

variable "region" {
  description = "The region where to deploy this code (e.g. us-east-1)."
  default = "us-east-1"
}

variable "key_pair_name" {
  description = "The name of the Key Pair that can be used to SSH to each EC2 instance in the ECS cluster. Leave blank to not include a Key Pair."
  default = ""
}

variable "coveo_challenge_image" {
  description = "The name of the Docker image to deploy for the app"
  default = "kushanagi/coveo"
}

variable "coveo_challenge_version" {
  description = "The version (i.e. tag) of the Docker container to deploy for the app"
  default = "latest"
}

variable "coveo_challenge_port" {
  description = "The port the service Docker container listens on for HTTP requests (e.g. 9000)"
  default = 9000
}
