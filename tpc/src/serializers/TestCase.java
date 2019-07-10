package serializers;

public abstract class TestCase
{
    /**
     * @return avg time cost every iteration, measure in nanosecond unit
     */
    public abstract <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception;
}
