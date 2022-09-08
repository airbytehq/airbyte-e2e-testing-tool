#!/usr/bin/env bash

docker_arg="$1"
echo "Docker arg = $docker_arg"
arg_1=${docker_arg//\=/\= }
arg_2=${arg_1// /\" }
arg_3="${arg_2}\" "
echo "AAAAAAA = $arg_3"
#arg_4=
#docker build -t testingtool .
#docker run -it  testingtool:latest /help-full name="$docker_arg"