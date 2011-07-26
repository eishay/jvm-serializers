package serializers.jackson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import serializers.Serializer;

public abstract class BaseJacksonDataBind<T>  extends Serializer<T>
{
    protected final String name;
    protected final JavaType type;
    protected final ObjectMapper mapper;

    protected BaseJacksonDataBind(String name, Class<T> clazz, ObjectMapper mapper)
    {
        this.name = name;
        type = mapper.getTypeFactory().constructType(clazz);
        this.mapper = mapper;
    }

    public String getName() { return name; }

    @Override
    public abstract byte[] serialize(T data) throws IOException;

    @Override
    public abstract T deserialize(byte[] array) throws IOException;

    // // Future extensions for testing performance for item sequences
    
    // @Override
    public abstract byte[] serializeItems(T[] items) throws IOException;

    // @Override
    public abstract T[] deserializeItems(byte[] input, int numberOfItems) throws IOException;
    
    protected final JsonParser constructParser(byte[] data) throws IOException
    {
            return mapper.getJsonFactory().createJsonParser(data, 0, data.length);
    }

    protected final JsonGenerator constructGenerator(ByteArrayOutputStream baos) throws IOException
    {
            return mapper.getJsonFactory().createJsonGenerator(baos, JsonEncoding.UTF8);
    }
}
