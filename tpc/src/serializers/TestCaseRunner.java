package serializers;

public final class TestCaseRunner<J>
{
    private final Transformer<J,Object> transformer;
    private final Serializer<Object> serializer;
    private final J value;

    public TestCaseRunner(Transformer<J,Object> transformer, Serializer<Object> serializer, J value)
    {
            this.transformer = transformer;
            this.serializer = serializer;
            this.value = value;
    }

    public double run(TestCase tc, int iterations) throws Exception
    {
            return tc.run(transformer, serializer, value, iterations);
    }

    public double runTakeMin(int trials, TestCase tc, int iterations) throws Exception
    {
            double minTime = Double.MAX_VALUE;
            for (int i = 0; i < trials; i++) {
                    double time = tc.run(transformer, serializer, value, iterations);
                    minTime = Math.min(minTime, time);
            }
            return minTime;
    }
}
