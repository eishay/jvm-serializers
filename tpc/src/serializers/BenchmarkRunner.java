package serializers;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.DeflaterOutputStream;

import serializers.BenchmarkBase.Params;
import serializers.cks.CksBinary;
import serializers.cks.CksText;
import serializers.jackson.*;
import serializers.json.JsonGsonDatabind;
import serializers.json.JsonArgoTree;
import serializers.json.FastJSONDatabind;
import serializers.json.FlexjsonDatabind;
import serializers.json.JsonGsonManual;
import serializers.json.JsonGsonTree;
import serializers.json.JsonDotOrgManualTree;
import serializers.json.JsonLibJsonDatabind;
import serializers.json.JsonPathDeserializerOnly;
import serializers.json.JsonSimpleManualTree;
import serializers.json.JsonSimpleWithContentHandler;
import serializers.json.JsonSmartManualTree;
import serializers.json.JsonTwoLattes;
import serializers.json.JsonijJpath;
import serializers.json.JsonijManualTree;
import serializers.json.JsonSvensonDatabind;
import serializers.protobuf.ActiveMQProtobuf;
import serializers.protobuf.Protobuf;
import serializers.protostuff.Protostuff;
import serializers.protostuff.ProtostuffJson;
import serializers.protostuff.ProtostuffSmile;
import serializers.xml.XmlJavolution;
import serializers.xml.XmlStax;
import serializers.xml.XmlXStream;

public class BenchmarkRunner extends BenchmarkBase
{
    public static void main(String[] args) {
        new BenchmarkRunner().runBenchmark(args);
    }
    
    private void runBenchmark(String[] args) {
        runBenchmark(args,
                Create,
                Serialize, SerializeSameObject,
                Deserialize, DeserializeAndCheck, DeserializeAndCheckShallow);
    }

    @Override
    protected void addTests(TestGroups groups)
    {
        // Binary Formats; language-specific ones
        JavaBuiltIn.register(groups);
        JavaManual.register(groups);
        Scala.register(groups);
        // hessian, kryo and wobly are Java object serializations
        Hessian.register(groups);
        Kryo.register(groups);
        Wobly.register(groups);
        
        // Binary formats, generic: protobuf, thrift, avro, CKS, msgpack
        Protobuf.register(groups);
        ActiveMQProtobuf.register(groups);
        Protostuff.register(groups);
        Thrift.register(groups);
        AvroSpecific.register(groups);
        AvroGeneric.register(groups);
        CksBinary.register(groups);
        MsgPack.register(groups);

        // JSON
        JacksonJsonManual.register(groups);
        JacksonJsonTree.register(groups);
        JacksonJsonTreeWithStrings.register(groups);
        JacksonJsonDatabind.register(groups);
        JacksonJsonDatabindWithStrings.register(groups);
        JsonTwoLattes.register(groups);
        ProtostuffJson.register(groups);
// too slow, why bother:
//        ProtobufJson.register(groups);
        JsonGsonManual.register(groups);
        JsonGsonTree.register(groups);
        JsonGsonDatabind.register(groups);
        JsonSvensonDatabind.register(groups);
        FlexjsonDatabind.register(groups);
        JsonLibJsonDatabind.register(groups);
        FastJSONDatabind.register(groups);
        JsonSimpleWithContentHandler.register(groups);
        JsonSimpleManualTree.register(groups);
        JsonSmartManualTree.register(groups);
        JsonDotOrgManualTree.register(groups);
        JsonijJpath.register(groups);
        JsonijManualTree.register(groups);
        JsonArgoTree.register(groups);
        JsonPathDeserializerOnly.register(groups);

        // Then JSON-like
        // CKS text is textual JSON-like format
        CksText.register(groups);
        // then binary variants
        // BSON is binary JSON-like format
        JacksonBsonManual.register(groups);
        JacksonBsonDatabind.register(groups);
        MongoDB.register(groups);
        JacksonSmileManual.register(groups);
        JacksonSmileDatabind.register(groups);
        ProtostuffSmile.register(groups);

        // XML-based formats.
        XmlStax.register(groups);
        XmlXStream.register(groups);
        JacksonXmlDatabind.register(groups);
        XmlJavolution.register(groups);
    }

    @Override
    protected Object convertTestData(TestGroup.Entry<?,Object> loader, Params params, byte[] data)
            throws Exception
    {
        Object deserialized = loader.serializer.deserialize(data);
        return loader.transformer.reverse(deserialized);
    }

    @Override
    protected <J> byte[] serializeForSize(Transformer<J,Object> transformer, Serializer<Object> serializer, J value)
        throws Exception
    {
        return serializer.serialize(transformer.forward(value));
    }
    
    @Override
    protected <J> void checkCorrectness(PrintWriter errors, Transformer<J,Object> transformer,
            Serializer<Object> serializer, J value)
        throws Exception
    {
        checkSingleItem(errors, transformer, serializer, value);
    }
    
    // ------------------------------------------------------------------------------------
    // Test case objects
    // ------------------------------------------------------------------------------------
    
    protected final TestCase Create = new TestCase()
    {
            public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
            {
                    long start = System.nanoTime();
                    for (int i = 0; i < iterations; i++)
                    {
                            transformer.forward(value);
                    }
                    return iterationTime(System.nanoTime() - start, iterations);
            }
    };

    protected final TestCase Serialize = new TestCase()
    {
            public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
            {
                    long start = System.nanoTime();
                    for (int i = 0; i < iterations; i++)
                    {
                            Object obj = transformer.forward(value);
                            serializer.serialize(obj);
                    }
                    return iterationTime(System.nanoTime() - start, iterations);
            }
    };

    protected final TestCase SerializeSameObject = new TestCase()
    {
            public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
            {
                    // let's reuse same instance to reduce overhead
                    Object obj = transformer.forward(value);
                    long start = System.nanoTime();
                    for (int i = 0; i < iterations; i++)
                    {
                            serializer.serialize(obj);
                    }
                    return iterationTime(System.nanoTime() - start, iterations);
            }
    };

    protected final TestCase Deserialize = new TestCase()
    {
            public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
            {
                    byte[] array = serializer.serialize(transformer.forward(value));
                    long start = System.nanoTime();
                    for (int i = 0; i < iterations; i++)
                    {
                            serializer.deserialize(array);
                    }
                    return iterationTime(System.nanoTime() - start, iterations);
            }
    };

    protected final TestCase DeserializeAndCheck = new TestCase()
    {
            public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
            {
                    byte[] array = serializer.serialize(transformer.forward(value));
                    long start = System.nanoTime();
                    for (int i = 0; i < iterations; i++)
                    {
                            Object obj = serializer.deserialize(array);
                            transformer.reverse(obj);
                    }
                    return iterationTime(System.nanoTime() - start, iterations);
            }
    };

    protected final TestCase DeserializeAndCheckShallow = new TestCase()
    {
            public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
            {
                    byte[] array = serializer.serialize(transformer.forward(value));
                    long start = System.nanoTime();
                    for (int i = 0; i < iterations; i++)
                    {
                            Object obj = serializer.deserialize(array);
                            transformer.shallowReverse(obj);
                    }
                    return iterationTime(System.nanoTime() - start, iterations);
            }
    };
}
