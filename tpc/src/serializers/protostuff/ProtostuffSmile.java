package serializers.protostuff;

import static serializers.protostuff.Protostuff.MEDIA_CONTENT_SCHEMA;

import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.SmileIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import serializers.*;
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
        groups.media.add(JavaBuiltIn.mediaTransformer, SmileManualMediaSerializer,
                new SerFeatures(
                        SerFormat.JSON,
                        SerGraph.FLAT_TREE,
                        SerClass.MANUAL_OPT,
                        ""
                )
        );
        // runtime (reflection)
        groups.media.add(JavaBuiltIn.mediaTransformer, SmileRuntimeMediaSerializer,
                new SerFeatures(
                        SerFormat.JSON,
                        SerGraph.FULL_GRAPH,
                        SerClass.ZERO_KNOWLEDGE,
                        ""
                )
        );

        /* protostuff has too many entries

        // generated code
        groups.media.add(Protostuff.MediaTransformer, SmileMediaSerializer);*/
    }

    public static final Serializer<MediaContent> SmileMediaSerializer = 
        new Serializer<MediaContent>()
    {

        public MediaContent deserialize(byte[] array) throws Exception
        {
            final MediaContent mc = new MediaContent();
            SmileIOUtil.mergeFrom(array, mc, mc.cachedSchema(), false);
            return mc;
        }

        public byte[] serialize(MediaContent content) throws Exception
        {
            return SmileIOUtil.toByteArray(content, content.cachedSchema(), false);
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
            SmileIOUtil.mergeFrom(array, mc, schema, false);
            return mc;
        }

        public byte[] serialize(data.media.MediaContent content) throws Exception
        {
            return SmileIOUtil.toByteArray(content, schema, false);
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
            SmileIOUtil.mergeFrom(array, mc, MEDIA_CONTENT_SCHEMA, false);
            return mc;
        }

        public byte[] serialize(data.media.MediaContent content) throws Exception
        {
            return SmileIOUtil.toByteArray(content, MEDIA_CONTENT_SCHEMA, false);
        }
        
        public String getName()
        {
            return "smile/protostuff-manual";
        }
        
    };
}
