#!/usr/bin/env bash

docker_arg="$1"
echo "Docker testing tool args = $docker_arg"
docker build -t testingtool .
com="docker run -v $(pwd)/secrets:/app/lib/secrets testingtool:latest $docker_arg"
eval "$com"