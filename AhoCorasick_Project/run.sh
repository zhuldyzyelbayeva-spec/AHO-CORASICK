#!/usr/bin/env bash
set -e
mkdir -p out results
javac -d out src/*.java
java -cp out Tests
java -cp out Benchmark
