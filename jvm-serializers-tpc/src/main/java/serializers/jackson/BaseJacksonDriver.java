package serializers.jackson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import serializers.Serializer;

abstract class BaseJacksonDriver<T> extends Serializer<T>
{
    protected final String name;

    protected BaseJacksonDriver(String n)
    {
        name = n;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public abstract byte[] serialize(T data) throws IOException;

    @Override
    public abstract T deserialize(byte[] array) throws IOException;

    // // Future extensions for testing performance for item sequences
    
    @Override
    public abstract void serializeItems(T[] items, OutputStream out) throws Exception;

    @Override
    public abstract T[] deserializeItems(InputStream in, int numberOfItems) throws IOException;
}
