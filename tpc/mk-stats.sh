#! /usr/bin/env bash

echo "" > stats.txt

FILES=./results/tmp/*-result.txt
for f in $FILES
do
   awk '/./{line=$0} END{print line}' $f >> stats.txt
done

cat stats.txt
