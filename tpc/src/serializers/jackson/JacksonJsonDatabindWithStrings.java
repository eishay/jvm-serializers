package serializers.jackson;

import java.io.*;

import data.media.MediaContent;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.*;

import serializers.JavaBuiltIn;
import serializers.TestGroups;

/**
 * This serializer uses Jackson in full automated data binding mode, which
 * can handle typical Java POJOs (esp. beans; otherwise may need to annotate
 * to configure)
 */
public class JacksonJsonDatabindWithStrings<T> extends BaseJacksonDataBind<T>
{
    public static void register(TestGroups groups)
    {
        groups.media.add(JavaBuiltIn.MediaTransformer,
                new JacksonJsonDatabindWithStrings<MediaContent>(MediaContent.class));
    }

    public JacksonJsonDatabindWithStrings(Class<T> clz) {
        super("json/jackson/databind-strings", clz, new ObjectMapper());
    }

    public byte[] serialize(T data) throws IOException
    {
        return mapper.writeValueAsString(data).getBytes(("UTF-8"));
    }

    @SuppressWarnings("unchecked")
    public T deserialize(byte[] array) throws IOException
    {
        // return (T) mapper.readValue(array, 0, array.length, type);
        String input = new String(array, "UTF-8");
        return (T) mapper.readValue(input, type);
    }
    
    // // Future extensions for testing performance for item sequences
    
    @Override
    public void serializeItems(T[] items, OutputStream out) throws Exception
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
