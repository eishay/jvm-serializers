package serializers;

import java.io.PrintWriter;

import serializers.jackson.*;
import serializers.xml.XmlStax;

/**
 * Alternative benchmark which uses a sequence of data items for testing,
 * instead of a single item that main test uses.
 */
public class DataStreamBenchmark extends BenchmarkBase
{
    public static void main(String[] args) {
        new DataStreamBenchmark().runBenchmark(args);
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
        // JSON
        JacksonJsonManual.register(groups);
        JacksonJsonTree.register(groups);
        JacksonJsonDatabind.register(groups);

        // JSON-like
        JacksonSmileManual.register(groups);
        JacksonSmileDatabind.register(groups);

        // XML
        XmlStax.register(groups);

    }

    @Override
    protected <J> void checkCorrectness(PrintWriter errors, Transformer<J,Object> transformer,
            Serializer<Object> serializer, J value)
        throws Exception
    {
        // !!! TODO
    }

    // ------------------------------------------------------------------------------------
    // Test case objects
    // ------------------------------------------------------------------------------------
    
    // !!! TODO: implement properly (for now, cut'n pasted from original)
    
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
