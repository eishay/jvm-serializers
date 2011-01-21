package serializers;

import static serializers.Protostuff.MEDIA_CONTENT_SCHEMA;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.JsonIOUtil;
import com.dyuproject.protostuff.JsonXIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import java.io.ByteArrayOutputStream;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;

import org.codehaus.jackson.smile.SmileFactory;
import org.codehaus.jackson.smile.SmileGenerator;

import serializers.protostuff.media.MediaContent;

/**
 * @author David Yu
 * @created Jan 18, 2011
 */

public final class ProtostuffSmile
{

    public static void register(TestGroups groups)
    {
        // manual (hand-coded schema, no autoboxing)
        groups.media.add(JavaBuiltIn.MediaTransformer, SmileManualMediaSerializer);
        // runtime (reflection)
        groups.media.add(JavaBuiltIn.MediaTransformer, SmileRuntimeMediaSerializer);

        /* protostuff has too many entries

        // generated code
        groups.media.add(Protostuff.MediaTransformer, SmileMediaSerializer);*/
    }

    static final SmileFactory factory = new SmileFactory();
    static
    {
	    factory.configure(SmileGenerator.Feature.CHECK_SHARED_NAMES, true);
        //	factory.configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, true);
	    factory.configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, false);
    }

    public static final Serializer<MediaContent> SmileMediaSerializer = 
        new Serializer<MediaContent>()
    {

        public MediaContent deserialize(byte[] array) throws Exception
        {
            final MediaContent mc = new MediaContent();
            final JsonParser parser = factory.createJsonParser(array, 0, array.length);
            try
            {
                JsonIOUtil.mergeFrom(parser, mc, mc.cachedSchema(), false);
            }
            finally
            {
                parser.close();
            }
            return mc;
        }

        public byte[] serialize(MediaContent content) throws Exception
        {
            final ByteArrayOutputStream baos = outputStream(content);
            final JsonGenerator generator = factory.createJsonGenerator(baos, JsonEncoding.UTF8); 
            try
            {
                JsonIOUtil.writeTo(generator, content, content.cachedSchema(), false);
            }
            finally
            {
                generator.close();
            }
            return baos.toByteArray();
        }
        
        public String getName()
        {
            return "smile/protostuff";
        }
        
    };

    public static final Serializer<data.media.MediaContent> SmileRuntimeMediaSerializer = 
        new Serializer<data.media.MediaContent>()
    {

	final Schema<data.media.MediaContent> schema = RuntimeSchema.getSchema(data.media.MediaContent.class);

        public data.media.MediaContent deserialize(byte[] array) throws Exception
        {
            final data.media.MediaContent mc = new data.media.MediaContent();
            final JsonParser parser = factory.createJsonParser(array, 0, array.length);
            try
            {
                JsonIOUtil.mergeFrom(parser, mc, schema, false);
            }
            finally
            {
                parser.close();
            }
            return mc;
        }

        public byte[] serialize(data.media.MediaContent content) throws Exception
        {
            final ByteArrayOutputStream baos = outputStream(content);
            final JsonGenerator generator = factory.createJsonGenerator(baos, JsonEncoding.UTF8); 
            try
            {
                JsonIOUtil.writeTo(generator, content, schema, false);
            }
            finally
            {
                generator.close();
            }
            return baos.toByteArray();
        }
        
        public String getName()
        {
            return "smile/protostuff-runtime";
        }
        
    };

    public static final Serializer<data.media.MediaContent> SmileManualMediaSerializer = 
        new Serializer<data.media.MediaContent>()
    {

        public data.media.MediaContent deserialize(byte[] array) throws Exception
        {
            final data.media.MediaContent mc = new data.media.MediaContent();
            final JsonParser parser = factory.createJsonParser(array, 0, array.length);
            try
            {
                JsonIOUtil.mergeFrom(parser, mc, MEDIA_CONTENT_SCHEMA, false);
            }
            finally
            {
                parser.close();
            }
            return mc;
        }

        public byte[] serialize(data.media.MediaContent content) throws Exception
        {
            final ByteArrayOutputStream baos = outputStream(content);
            final JsonGenerator generator = factory.createJsonGenerator(baos, JsonEncoding.UTF8); 
            try
            {
                JsonIOUtil.writeTo(generator, content, MEDIA_CONTENT_SCHEMA, false);
            }
            finally
            {
                generator.close();
            }
            return baos.toByteArray();
        }
        
        public String getName()
        {
            return "smile/protostuff-manual";
        }
        
    };
}
