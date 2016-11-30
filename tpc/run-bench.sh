#! /usr/bin/env bash
set -e

raw_result_dir="results/raw"

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

# for low run-to-run jitter  (anyway expect ~3% run-to-run jitter)
# testTime=60000
# warmupTime=60000
# turn off turbo boost and any other kind of dynamic clock scaling

testTime=10000
warmupTime=15000
iter=2000

if [ $# -eq 0 ]; then
    if [ -e "$raw_result_dir" ]; then
        echo "removing old raw directory"
        rm -r "$raw_result_dir"
    fi
    sentence=$(java -cp $cp serializers.BenchmarkExporter) # just grab all serializers
    echo "sentence is: "
    echo $sentence
elif [ $# -eq 1 ]; then
	if [ -e "$raw_result_dir" ]; then
        echo "in elif: removing old raw directory"
        rm -r "$raw_result_dir"
    fi
    sentence=$1
else
    echo "Expecting zero or one argument, got $#." 1>&2
    exit 1
fi

echo "outside of if fi block, sentence is: "
echo $sentence
#echo "exit now..."
#exit 1

mkdir -p "$raw_result_dir"

sentence=${sentence//,/$'\n'}  # change the colons to white space
for word in $sentence
do
    echo "running $word .."
    file=$word-result.txt
    file="$raw_result_dir"/${file//\//-}  # change '/' to '-'
    echo $word > $file
    java $mem -cp $cp $clz -iterations=$iter -warmup-time=$warmupTime -testRunMillis=$testTime -include=$word data/media.hermes.cks >> $file
done

echo "about to run mk-stats.sh now."
./mk-stats.sh
echo "finished running mk-stats.sh."

# find files with no numbers => errors
echo ""
echo "====================================================================================="
echo "errors:"
find ./results/raw/. -print -type f -name "*.txt" -exec tail -1 {} \; | grep -B 1 create
echo "====================================================================================="
echo ""

