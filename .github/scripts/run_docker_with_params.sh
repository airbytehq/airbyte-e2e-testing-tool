#!/usr/bin/env bash

docker_arg="$1"
echo "Docker arg = $docker_arg"
docker build -t testingtool .
docker run -it  testingtool:latest "$docker_arg"