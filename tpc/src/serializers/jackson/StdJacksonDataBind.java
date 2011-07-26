package serializers.jackson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
    
    // @Override
    public byte[] serializeItems(T[] items) throws IOException
    {
        ByteArrayOutputStream baos = outputStream(items[0]);
        JsonGenerator generator = constructGenerator(baos);
        // JSON allows simple sequences, so:
        for (int i = 0, len = items.length; i < len; ++i) {
            mapper.writeValue(generator, items[i]);
        }
        generator.close();
        return baos.toByteArray();
    }

    // @Override
    @SuppressWarnings("unchecked")
    public T[] deserializeItems(byte[] input, int numberOfItems) throws IOException 
    {
        T[] result = (T[]) new Object[numberOfItems];
        JsonParser parser = constructParser(input);
        for (int i = 0; i < numberOfItems; ++i) {
            result[i] = mapper.readValue(parser, type);
        }
        parser.close();
        return result;
    }
}
