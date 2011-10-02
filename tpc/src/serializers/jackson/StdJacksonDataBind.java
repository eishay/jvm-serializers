package serializers.jackson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public final class StdJacksonDataBind<T> extends BaseJacksonDataBind<T>
{
    public StdJacksonDataBind(String name, Class<T> clazz, ObjectMapper mapper) {
        super(name, clazz, mapper);
    }

    @Override
    public byte[] serialize(T data) throws IOException
    {
        return mapper.writeValueAsBytes(data);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(byte[] array) throws IOException
    {
        return (T) mapper.readValue(array, 0, array.length, type);
    }

    // // Future extensions for testing performance for item sequences
    
    @Override
    public void serializeItems(T[] items, OutputStream out) throws IOException
    {
        JsonGenerator generator = constructGenerator(out);
        // JSON allows simple sequences, so:
        for (int i = 0, len = items.length; i < len; ++i) {
            mapper.writeValue(generator, items[i]);
        }
        generator.close();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T[] deserializeItems(InputStream in, int numberOfItems) throws IOException 
    {
        T[] result = (T[]) new Object[numberOfItems];
        JsonParser parser = constructParser(in);
        for (int i = 0; i < numberOfItems; ++i) {
            result[i] = (T) mapper.readValue(parser, type);
        }
        parser.close();
        return result;
    }
}
