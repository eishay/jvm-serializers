package serializers;

import com.dyuproject.protostuff.JsonIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import serializers.protostuff.media.MediaContent;
import serializers.protostuff.media.Media;
import serializers.protostuff.media.Image;

/**
 * @author David Yu
 * @created Oct 26, 2009
 */

public final class ProtostuffJson
{

    public static void register(TestGroups groups)
    {
            groups.media.add(Protostuff.MediaTransformer, JsonMediaSerializer);
            groups.media.add(JavaBuiltIn.MediaTransformer, RuntimeJsonMediaSerializer);
    }

    public static final Serializer<MediaContent> JsonMediaSerializer = 
        new Serializer<MediaContent>()
    {

        public MediaContent deserialize(byte[] array) throws Exception
        {
            MediaContent mc = new MediaContent();
            JsonIOUtil.mergeFrom(array, mc, true);
            return mc;
        }

        public byte[] serialize(MediaContent content) throws Exception
        {
            return JsonIOUtil.toByteArray(content, true);
        }
        
        public String getName()
        {
            return "json/protostuff-core";
        }
        
    };

    public static final Serializer<data.media.MediaContent> RuntimeJsonMediaSerializer = 
        new Serializer<data.media.MediaContent>()
    {

	final Schema<data.media.MediaContent> schema = RuntimeSchema.getSchema(data.media.MediaContent.class);

        public data.media.MediaContent deserialize(byte[] array) throws Exception
        {
            data.media.MediaContent mc = new data.media.MediaContent();
            JsonIOUtil.mergeFrom(array, mc, schema, true);
            return mc;
        }

        public byte[] serialize(data.media.MediaContent content) throws Exception
        {
            return JsonIOUtil.toByteArray(content, schema, true);
        }
        
        public String getName()
        {
            return "json/protostuff-runtime";
        }
        
    };
}
