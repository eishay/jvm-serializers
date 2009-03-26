#!/bin/sh

# Small documents, should probably use fairly small heap to capture effects
# of memory allocation

java -Xmx16m -server \
-cp build:$(find lib -name "*.jar" | tr '\n' ':') serializers.BenchmarkRunner
