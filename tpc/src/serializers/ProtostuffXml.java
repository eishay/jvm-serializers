package serializers;

import com.dyuproject.protostuff.XmlIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import serializers.protostuff.media.MediaContent;
import serializers.protostuff.media.Media;
import serializers.protostuff.media.Image;

public final class ProtostuffXml
{

    public static void register(TestGroups groups)
    {
        groups.media.add(Protostuff.MediaTransformer, XmlMediaSerializer);
        // protostuff has too many entries
        //groups.media.add(JavaBuiltIn.MediaTransformer, RuntimeXmlMediaSerializer);
    }

    public static final Serializer<MediaContent> XmlMediaSerializer = 
        new Serializer<MediaContent>()
    {

        public MediaContent deserialize(byte[] array) throws Exception
        {
            MediaContent mc = new MediaContent();
            XmlIOUtil.mergeFrom(array, mc, mc.cachedSchema());
            return mc;
        }

        public byte[] serialize(MediaContent content) throws Exception
        {
            return XmlIOUtil.toByteArray(content, content.cachedSchema());
        }
        
        public String getName()
        {
            return "xml/protostuff";
        }
        
    };

    public static final Serializer<data.media.MediaContent> RuntimeXmlMediaSerializer = 
        new Serializer<data.media.MediaContent>()
    {

	final Schema<data.media.MediaContent> schema = RuntimeSchema.getSchema(data.media.MediaContent.class);

        public data.media.MediaContent deserialize(byte[] array) throws Exception
        {
            data.media.MediaContent mc = new data.media.MediaContent();
            XmlIOUtil.mergeFrom(array, mc, schema);
            return mc;
        }

        public byte[] serialize(data.media.MediaContent content) throws Exception
        {
            return XmlIOUtil.toByteArray(content, schema);
        }
        
        public String getName()
        {
            return "xml/protostuff-runtime";
        }
        
    };
}
