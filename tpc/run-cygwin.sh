#! /usr/bin/env bash

# added this, because the other runscripts did not work for me with recent cygwin installation

mem=-Xmx256m
clz=serializers.BenchmarkRunner

cpgen=$(cat build/gen-cp)
cplib=$(cat build/lib-cp)
sep=':' 
# cygwin
case "`uname`" in
	CYGWIN*) sep=';' ;;
esac

cp=./build/bytecode/main$sep$cpgen$sep$cplib
testTime=10000
warmupTime=10000
iter=100

mkdir ./results/tmp &> /dev/null

if [ -n "$1" ]; then
    sentence=$1
else
    rm ./results/tmp/*.txt
    sentence=$(java -cp $cp serializers.BenchMarkExporter) # just grab all serializers

    sentence=${sentence//,/$'\n'}  # change the colons to white space
    for word in $sentence
    do
        echo "running $word .."
        file=$word-result.txt
        file=./results/tmp/${file//\//-}  # change '/' to '-'
        echo $word > $file
        java $mem -cp $cp $clz -iterations=$iter -warmup-time=$warmupTime -testRunMillis=$testTime -include=$word data/media.1.cks >> $file    
    done
fi

# find files with no numbers => errors
echo ""
echo "====================================================================================="
echo "errors:"
find ./results/tmp/. -print -type f -name "*.txt" -exec tail -1 {} \; | grep -B 1 create
echo "====================================================================================="
echo ""

exec ./mk-stats.sh