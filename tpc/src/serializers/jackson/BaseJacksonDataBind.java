package serializers.jackson;

import java.io.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

public abstract class BaseJacksonDataBind<T> extends BaseJacksonDriver<T>
{
    protected final JavaType type;
    protected final ObjectMapper mapper;
    protected final ObjectReader reader;
    protected final ObjectWriter writer;

    protected BaseJacksonDataBind(String name, Class<T> clazz, ObjectMapper mapper)
    {
        super(name);
        type = mapper.getTypeFactory().constructType(clazz);
        this.mapper = mapper;
        reader = mapper.reader(type);
        writer = mapper.writerWithType(type);
    }
    
    protected final JsonParser constructParser(byte[] data) throws IOException {
        return mapper.getJsonFactory().createParser(data, 0, data.length);
    }

    protected final JsonParser constructParser(InputStream in) throws IOException {
        return mapper.getJsonFactory().createParser(in);
    }
    
    protected final JsonGenerator constructGenerator(OutputStream out) throws IOException {
        return mapper.getJsonFactory().createGenerator(out, JsonEncoding.UTF8);
    }
}
