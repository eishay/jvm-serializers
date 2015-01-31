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
        writer = mapper.writerFor(type);
    }

    protected BaseJacksonDataBind(String name, JavaType type,
            ObjectMapper mapper, ObjectReader reader, ObjectWriter writer)
    {
        super(name);
        this.type = type;
        this.mapper = mapper;
        this.reader = reader;
        this.writer = writer;
    }
    
    protected final JsonParser constructParser(byte[] data) throws IOException {
        return mapper.getFactory().createParser(data, 0, data.length);
    }

    protected final JsonParser constructParser(InputStream in) throws IOException {
        return mapper.getFactory().createParser(in);
    }
    
    protected final JsonGenerator constructGenerator(OutputStream out) throws IOException {
        return mapper.getFactory().createGenerator(out, JsonEncoding.UTF8);
    }
}
