# Coveo code challenge
[![Build Status](https://travis-ci.org/alheureux88/coveo-challenge.svg?branch=master)](https://travis-ci.org/alheureux88/coveo-challenge)

## Required application
- SBT
- Terraform
- Docker

## SBT commands
- `dependencyUpdates` will print out a list of out-of-date dependency that needs updating.
- `run` will launch a local instance of the service accessible on `localhost:9000`
- `release` in project web will ask you for a version, tag git, compile, test and generate docker and publish it to the repo.
- `test` will execute the test.
- `it:test` will execute the integration tests.
- `docker:publishLocal` will generate the runscripts and docker images.
- `docker:publish` will generate the runscripts and docker images and push it.
- `makeBashScripts` or `makeBatScripts` will generate the runscripts.

## Technology used
|Technology|Reason it was used|
|---|---|
|Scala|My most comfortable language to develop something fast.|
|Scalatest|Used it often.|
|Scalacheck|Used it a couple of times.|
|Play|Already knew how to do the filters. Normally I would have used akka http or a smaller HTTP library because most of the useful feature of Play are not helpful for this project. I just wanted to finish it faster than using a new technology.|
|CodaHale Metrics|Library I used in the past. Easy to use and helpful for metric gathering.|
|Swagger|First time used.|
|AWS|First time I run anything on the cloud.|
|Terraform|First time I ever used it.|
|Docker|A tiny amount of testing in the past.|
|Travis|First time.|