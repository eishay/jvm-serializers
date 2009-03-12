#!/bin/sh

# note: need JDK 1.6 that support wildcard for classpath

java -Xmx64m -cp build:lib/\* serializers.BenchmarkRunner
