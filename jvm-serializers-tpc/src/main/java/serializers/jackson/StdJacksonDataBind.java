package serializers.jackson;

import java.io.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

public final class StdJacksonDataBind<T> extends BaseJacksonDataBind<T>
{
    public StdJacksonDataBind(String name, Class<T> clazz, ObjectMapper mapper) {
        super(name, clazz, mapper);
    }

    public StdJacksonDataBind(String name, JavaType type,
            ObjectMapper mapper, ObjectReader reader, ObjectWriter writer)
    {
        super(name, type, mapper, reader, writer);
    }
    
    @Override
    public byte[] serialize(T data) throws IOException
    {
        return writer.writeValueAsBytes(data);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(byte[] array) throws IOException
    {
        return (T) reader.readValue(array, 0, array.length);
    }

    // // Future extensions for testing performance for item sequences
    
    @Override
    public void serializeItems(T[] items, OutputStream out) throws IOException
    {
        JsonGenerator generator = constructGenerator(out);
        // JSON allows simple sequences, so:
        for (int i = 0, len = items.length; i < len; ++i) {
            writer.writeValue(generator, items[i]);
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
            result[i] = (T) reader.readValue(parser, type);
        }
        parser.close();
        return result;
    }
}
