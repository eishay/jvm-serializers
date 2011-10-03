package serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import serializers.cks.CksText;
import serializers.jackson.*;
import serializers.protobuf.Protobuf;
import serializers.xml.XmlStax;

/**
 * Alternative benchmark which uses a sequence of data items for testing,
 * instead of a single item that main test uses.
 */
public class MediaStreamBenchmark extends BenchmarkBase
{
    public static void main(String[] args) {
        new MediaStreamBenchmark().runBenchmark(args);
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
        JavaManual.register(groups);
        Hessian.register(groups);

        // Binary formats, generic: protobuf, thrift, avro, CKS, msgpack
        Protobuf.register(groups);
        Thrift.register(groups);
        AvroSpecific.register(groups);
 
        // JSON
        JacksonJsonManual.register(groups);
//        JacksonJsonTree.register(groups);
        JacksonJsonDatabind.register(groups);

        // JSON-like
        // share both names & values for data streams:
        JacksonSmileManual.register(groups, true, true);
        JacksonSmileDatabind.register(groups, true, true);

        // this one needed to read in test data, too:
        CksText.register(groups);
        
        // XML (only fastest codecs)
        XmlStax.register(groups, false, true, true); // skip woodstox, include aalto and fast-infoset
    }

    @Override
    protected Object convertTestData(TestGroup.Entry<?,Object> loader, Params params, byte[] data)
        throws Exception
    {
        String extra = params.dataExtra;
        int count = 0;
        try {
            count = Integer.parseInt(extra);
        } catch (Exception e) {
            throw new IllegalArgumentException("Non-integer extra part ('"+extra+"') of data file: must be count");
        }
        Object[] deserialized = loader.serializer.deserializeItems(new ByteArrayInputStream(data), count);
        return loader.transformer.reverseAll(deserialized);
    }

    @Override
    protected <J> byte[] serializeForSize(Transformer<J,Object> transformer, Serializer<Object> serializer, J value)
        throws Exception
    {
        @SuppressWarnings("unchecked")
        Object[] result = transformer.forwardAll((J[]) value);
        return serializer.serializeAsBytes(result);
    }
    
    @Override
    protected <J> void checkCorrectness(PrintWriter errors, Transformer<J,Object> transformer,
            Serializer<Object> serializer, J input)
        throws Exception
    {
        // nasty cast, but works (and has to be used) here:
        @SuppressWarnings("unchecked")
        J[] items = (J[]) input;
        for (J item : items) {
            checkSingleItem(errors, transformer, serializer, item);
        }
    }

    // ------------------------------------------------------------------------------------
    // Test case objects
    // ------------------------------------------------------------------------------------
    
    protected final TestCase Create = new TestCase()
    {
        public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
        {
            @SuppressWarnings("unchecked")
            J[] src = (J[]) value;
            Object[] result = new Object[src.length];
            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                transformer.forward(src, result);
            }
            return iterationTime(System.nanoTime() - start, iterations);
        }
    };

    protected final TestCase Serialize = new TestCase()
    {
        public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
        {
            @SuppressWarnings("unchecked")
            J[] src = (J[]) value;
            long start = System.nanoTime();
            ByteArrayOutputStream out = serializer.outputStreamForList(src);
            for (int i = 0; i < iterations; i++) {
                Object[] items = transformer.forwardAll(src);
                serializer.serializeItems(items, out);
                out.reset();
            }
            return iterationTime(System.nanoTime() - start, iterations);
        }
    };

    protected final TestCase SerializeSameObject = new TestCase()
    {
        public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
        {
            long start = System.nanoTime();
            @SuppressWarnings("unchecked")
            Object[] items = transformer.forwardAll((J[]) value);
            ByteArrayOutputStream out = serializer.outputStreamForList(items);
            for (int i = 0; i < iterations; i++) {
                serializer.serializeItems(items, out);
                out.reset();
            }
            return iterationTime(System.nanoTime() - start, iterations);
        }
    };

    protected final TestCase Deserialize = new TestCase()
    {
        public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
        {
            @SuppressWarnings("unchecked")
            J[] src = (J[]) value;
            byte[] bytes = serializer.serializeAsBytes(transformer.forwardAll(src));
            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                serializer.deserializeItems(new ByteArrayInputStream(bytes), src.length);
            }
            return iterationTime(System.nanoTime() - start, iterations);
        }
    };

    protected final TestCase DeserializeAndCheck = new TestCase()
    {
        public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
        {
            @SuppressWarnings("unchecked")
            J[] src = (J[]) value;
            byte[] bytes = serializer.serializeAsBytes(transformer.forwardAll(src));
            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                Object[] items = serializer.deserializeItems(new ByteArrayInputStream(bytes), src.length);
                for (Object item : items) {
                    transformer.reverse(item);
                }
            }
            return iterationTime(System.nanoTime() - start, iterations);
        }
    };

    protected final TestCase DeserializeAndCheckShallow = new TestCase()
    {
        public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
        {
            @SuppressWarnings("unchecked")
            J[] src = (J[]) value;
            byte[] bytes = serializer.serializeAsBytes(transformer.forwardAll(src));
            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                Object[] items = serializer.deserializeItems(new ByteArrayInputStream(bytes), src.length);
                for (Object item : items) {
                    transformer.shallowReverse(item);
                }
            }
            return iterationTime(System.nanoTime() - start, iterations);
        }
    };
}
