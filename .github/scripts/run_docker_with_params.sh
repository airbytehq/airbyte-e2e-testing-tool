#!/usr/bin/env bash

docker_arg="$1"
version="$2"
echo "Docker testing tool args = $docker_arg"
echo "Version = $version"
com="docker run -v $(pwd)/secrets:/secrets -v $(pwd)/result:/result $version $docker_arg"
eval "$com"