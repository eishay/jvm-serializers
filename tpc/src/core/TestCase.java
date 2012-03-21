package core;

import core.serializers.Serializer;

public abstract class TestCase
{
    public abstract <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception;
}
