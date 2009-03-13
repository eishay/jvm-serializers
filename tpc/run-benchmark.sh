#!/bin/sh

java -Xmx64m -cp build:$(find lib -name "*.jar" | tr '\n' ':') serializers.BenchmarkRunner
