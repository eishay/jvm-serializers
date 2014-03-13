package serializers.protostuff;

import static serializers.protostuff.Protostuff.MEDIA_CONTENT_SCHEMA;

import com.dyuproject.protostuff.XmlIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import serializers.*;
import serializers.protostuff.media.MediaContent;

public final class ProtostuffXml
{

    public static void register(TestGroups groups)
    {
        // manual (hand-coded schema, no autoboxing)
        groups.media.add(JavaBuiltIn.mediaTransformer, XmlManualMediaSerializer,
                new SerFeatures(
                        SerFormat.XML,
                        SerGraph.FLAT_TREE,
                        SerClass.MANUAL_OPT,
                        ""
                )
        );
        // runtime (reflection)
        groups.media.add(JavaBuiltIn.mediaTransformer, XmlRuntimeMediaSerializer,
                new SerFeatures(
                        SerFormat.XML,
                        SerGraph.FLAT_TREE,
                        SerClass.ZERO_KNOWLEDGE,
                        ""
                )
        );

        /* protostuff has too many entries

        // generated code
        groups.media.add(Protostuff.MediaTransformer, XmlMediaSerializer);*/
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

    public static final Serializer<data.media.MediaContent> XmlRuntimeMediaSerializer = 
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

    public static final Serializer<data.media.MediaContent> XmlManualMediaSerializer = 
        new Serializer<data.media.MediaContent>()
    {

        public data.media.MediaContent deserialize(byte[] array) throws Exception
        {
            data.media.MediaContent mc = new data.media.MediaContent();
            XmlIOUtil.mergeFrom(array, mc, MEDIA_CONTENT_SCHEMA);
            return mc;
        }

        public byte[] serialize(data.media.MediaContent content) throws Exception
        {
            return XmlIOUtil.toByteArray(content, MEDIA_CONTENT_SCHEMA);
        }
        
        public String getName()
        {
            return "xml/protostuff-manual";
        }
        
    };
}
