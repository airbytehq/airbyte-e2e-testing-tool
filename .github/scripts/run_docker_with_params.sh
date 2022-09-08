#!/usr/bin/env bash

docker_arg="$1"
echo "Docker arg = $docker_arg"
docker run -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=123 -e POSTGRES_DB=test_db -e POSTGRES_SCHEMA=public \
    -p 5432:5432 -d -t -i --name postgres postgres:latest