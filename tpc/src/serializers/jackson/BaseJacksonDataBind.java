package serializers.jackson;

import java.io.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class BaseJacksonDataBind<T> extends BaseJacksonDriver<T>
{
    protected final JavaType type;
    protected final ObjectMapper mapper;

    protected BaseJacksonDataBind(String name, Class<T> clazz, ObjectMapper mapper)
    {
        super(name);
        type = mapper.getTypeFactory().constructType(clazz);
        this.mapper = mapper;
    }
    
    protected final JsonParser constructParser(byte[] data) throws IOException {
        return mapper.getJsonFactory().createJsonParser(data, 0, data.length);
    }

    protected final JsonParser constructParser(InputStream in) throws IOException {
        return mapper.getJsonFactory().createJsonParser(in);
    }
    
    protected final JsonGenerator constructGenerator(OutputStream out) throws IOException {
        return mapper.getJsonFactory().createJsonGenerator(out, JsonEncoding.UTF8);
    }
}
