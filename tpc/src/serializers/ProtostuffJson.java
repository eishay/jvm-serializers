package serializers;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.JsonIOUtil;
import com.dyuproject.protostuff.JsonXIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import serializers.protostuff.media.MediaContent;

/**
 * @author David Yu
 * @created Oct 26, 2009
 */

public final class ProtostuffJson
{

    public static void register(TestGroups groups)
    {
        groups.media.add(Protostuff.MediaTransformer, JsonMediaSerializer);
        groups.media.add(Protostuff.MediaTransformer, JsonMediaSerializerNumeric);
        // protostuff has too many entries
        //groups.media.add(JavaBuiltIn.MediaTransformer, RuntimeJsonMediaSerializer);
        //groups.media.add(JavaBuiltIn.MediaTransformer, RuntimeJsonMediaSerializerNumeric);
    }

    public static final Serializer<MediaContent> JsonMediaSerializer = 
        new Serializer<MediaContent>()
    {

        public MediaContent deserialize(byte[] array) throws Exception
        {
            MediaContent mc = new MediaContent();
            JsonIOUtil.mergeFrom(array, mc, mc.cachedSchema(), false);
            return mc;
        }

        public byte[] serialize(MediaContent content) throws Exception
        {
            return JsonIOUtil.toByteArray(content, content.cachedSchema(), false);
        }
        
        public String getName()
        {
            return "json/protostuff";
        }
        
    };

    public static final Serializer<MediaContent> JsonMediaSerializerNumeric = 
        new Serializer<MediaContent>()
    {

        final LinkedBuffer buffer = LinkedBuffer.allocate(512);

        public MediaContent deserialize(byte[] array) throws Exception
        {
            MediaContent mc = new MediaContent();
            JsonIOUtil.mergeFrom(array, mc, mc.cachedSchema(), true);
            return mc;
        }

        public byte[] serialize(MediaContent content) throws Exception
        {
            try
            {
                return JsonXIOUtil.toByteArray(content, content.cachedSchema(), true, buffer);
            }
            finally
            {
                buffer.clear();
            }
        }
        
        public String getName()
        {
            return "json/protostuff+numeric";
        }
        
    };

    public static final Serializer<data.media.MediaContent> RuntimeJsonMediaSerializer = 
        new Serializer<data.media.MediaContent>()
    {

	final Schema<data.media.MediaContent> schema = RuntimeSchema.getSchema(data.media.MediaContent.class);

        public data.media.MediaContent deserialize(byte[] array) throws Exception
        {
            data.media.MediaContent mc = new data.media.MediaContent();
            JsonIOUtil.mergeFrom(array, mc, schema, false);
            return mc;
        }

        public byte[] serialize(data.media.MediaContent content) throws Exception
        {
            return JsonIOUtil.toByteArray(content, schema, false);
        }
        
        public String getName()
        {
            return "json/protostuff-runtime";
        }
        
    };

    public static final Serializer<data.media.MediaContent> RuntimeJsonMediaSerializerNumeric = 
        new Serializer<data.media.MediaContent>()
    {

        final LinkedBuffer buffer = LinkedBuffer.allocate(512);

	    final Schema<data.media.MediaContent> schema = RuntimeSchema.getSchema(data.media.MediaContent.class);

        public data.media.MediaContent deserialize(byte[] array) throws Exception
        {
            data.media.MediaContent mc = new data.media.MediaContent();
            JsonIOUtil.mergeFrom(array, mc, schema, true);
            return mc;
        }

        public byte[] serialize(data.media.MediaContent content) throws Exception
        {
            try
            {
                return JsonXIOUtil.toByteArray(content, schema, true, buffer);
            }
            finally
            {
                buffer.clear();
            }
        }
        
        public String getName()
        {
            return "json/protostuff-runtime+numeric";
        }
        
    };
}
