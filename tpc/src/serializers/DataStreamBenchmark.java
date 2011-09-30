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
    public static void main(String[] args)
    {
        new BenchmarkRunner().runBenchmark(args);
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
}
