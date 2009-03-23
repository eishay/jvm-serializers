#!/bin/sh

java -Xmx64m -server \
-cp build:$(find lib -name "*.jar" | tr '\n' ':') serializers.BenchmarkRunner
