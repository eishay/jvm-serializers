#! /usr/bin/env bash

# added this, because the other runscripts did not work for me with recent cygwin installation

mem=-Xmx256m
clz=serializers.BenchmarkRunner

cpgen=$(cat build/gen-cp)
cplib=$(cat build/lib-cp)
sep=';'
cp=./build/bytecode/main$sep$cpgen$sep$cplib

java -version &> result.txt

#echo ""
#echo "CPGEN:"
#echo $cpgen
#echo ""
#echo "CPLIB:"
#echo $cplib
#echo ""
#echo "CP:"
#echo $cp
#echo ""


# SIMPLE/GENERIC
echo "" >> result.txt
echo "<b>SIMPLE/GENERIC:</b> <br> Serializes any POJO tree without class specific optimization. Serialized classes are known in advance. No cycle detection/shared object detection is done." >> result.txt 
echo "" >> result.txt
java $mem -cp $cp $clz -trials=500 -chart -include=java-built-in,hessian,kryo,fast-serialization,jboss-serialization,jboss-marshalling-river,protostuff,msgpack-databind,json/jackson/databind,json/jackson/db-afterburner,json/google-gson/databind,json/svenson-databind,json/flexjson/databind,json/fastjson/databind,smile/jackson/databind,smile/jackson/db-afterburner,smile/protostuff-runtime,bson/jackson/databind,xml/xstream+c,xml/jackson/databind-aalto,json/protostuff-runtime data/media.1.cks >> result.txt

# DEFAULT
echo "" >> result.txt
echo "<b>DEFAULT:</b> <br> Serializes arbitrary object graphs, cycle detection enabled. Nothing is known in advance about the classes to serialize. Only serializers supporting full object graph serialization are included." >> result.txt 
echo "" >> result.txt
java $mem -cp $cp $clz -trials=500 -chart -include=java-built-in-serializer,hessian,kryo-serializer,fast-serialization-shared,jboss-serialization data/media.1.cks >> result.txt

# SIMPLE/SPECIFC: Serializes only specific classes using code generation or other special knowledge about the class.
echo "" >> result.txt
echo "<b>SIMPLE/SPECIFC:</b> Serializes only specific classes using code generation or other special knowledge about the class." >> result.txt 
echo "" >> result.txt
java $mem -cp $cp $clz -trials=500 -chart -include=wobly,wobly-compact,kryo-opt,protobuf,protostuff,protobuf/protostuff,thrift,thrift-compact,avro,json/json-lib-databind,json/jsonij-jpath,json/jsonpath/json.simple data/media.1.cks >> result.txt

# MANUAL: Serializes only specific classes using hand written serialization code.
echo "" >> result.txt
echo "<b>MANUAL:</b> <br> Serializes only specific classes using hand written serialization code." >> result.txt 
echo "" >> result.txt
java $mem -cp $cp $clz -hidden -trials=500 -chart -include=java-manual,kryo-manual,protostuff-manual,json/jackson/manual,json/jackson/tree,json/protostuff-manual,json/google-gson/manual,json/google-gson/manual/tree,json/json.simple/manual,json/json.simple/manual/tree,json/json-smart/manual/tree,json/org.json/manual/tree,json/jsonij-manual/tree,json/argo-manual/tree,smile/jackson/manual,smile/protostuff-manual,bson/mongodb,xml/woodstox-manual,xml/aalto-manual,xml/fastinfo-manual,xml/xstream+c-woodstox,xml/xstream+c-aalto,xml/xstream+c-fastinfo,xml/javolution,avro-generic data/media.1.cks >> result.txt

