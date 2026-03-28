#!/bin/bash

for arg in "$@"; do
    echo "Program: $arg"
    java -jar jars/simulator.jar src/configuration/config.xml stats test_cases/$arg.out &>/dev/null

    #cat test_cases/$program.expected

    cat stats
done

