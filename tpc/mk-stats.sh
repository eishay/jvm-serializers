#! /usr/bin/env bash

echo "" > stats.txt

FILES=./results/tmp/*-result.txt
for f in $FILES
do
   awk '/./{line=$0} END{print line}' $f >> stats.txt
done

cpgen=$(cat build/gen-cp)
cplib=$(cat build/lib-cp)
sep=':' 
# cygwin
case "`uname`" in
	CYGWIN*) sep=';' ;;
esac

cp=./build/bytecode/main$sep$cpgen$sep$cplib

java -cp $cp serializers.StatsCruncher
