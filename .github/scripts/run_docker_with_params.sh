#!/usr/bin/env bash

docker_arg="$1"
echo "Docker arg = $docker_arg"
docker build -t testingtool .
com='docker run testingtool:latest $docker_arg'
eval "$com"