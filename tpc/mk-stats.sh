#! /usr/bin/env bash

raw_result_dir="results/raw"
combined_file="results/stats.txt"
output_file="results/report.texfile"

mkdir -p "$(dirname "$combined_input")"
echo "" > "$combined_file"
for f in "$raw_result_dir/"*"-result.txt"; do
   echo "Processing: $f"
   awk '/./{line=$0} END{print line}' "$f" >> "$combined_file"
done

cpgen=$(cat build/gen-cp)
cplib=$(cat build/lib-cp)
sep=':' 
# cygwin
case "`uname`" in
	CYGWIN*) sep=';' ;;
esac

cp=./build/bytecode/main$sep$cpgen$sep$cplib

echo "Writing results to: $output_file"
exec java -cp $cp serializers.StatsCruncher "$combined_file" "$output_file"
