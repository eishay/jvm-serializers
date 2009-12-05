package serializers;

import java.io.IOException;

import serializers.java.MediaContent;
import serializers.java.Media;

import org.codehaus.jackson.*;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * This serializer uses Jackson in full automated data binding mode, which
 * can handle typical Java POJOs (esp. beans; otherwise may need to annotate
 * to configure)
 */
public class JsonDataBindingSerializer
    extends JsonSerializer
{
    final ObjectMapper _mapper = new ObjectMapper();

    public JsonDataBindingSerializer() { super("json/jackson-databind"); }

    protected void writeMediaContent(JsonGenerator generator, MediaContent content) throws IOException
    {
        _mapper.writeValue(generator, content);
    }

    protected MediaContent readMediaContent(JsonParser parser) throws IOException
    {
        return _mapper.readValue(parser, MediaContent.class);
    }
}

