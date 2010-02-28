package serializers;

import java.io.ByteArrayOutputStream;

import org.codehaus.jackson.JsonParser;

import serializers.protobuf.media.MediaContentHolder.MediaContent;
import serializers.protostuff.MediaContentHolderJSON;

/**
 * @author David Yu
 * @created Oct 26, 2009
 */

public class ProtostuffJson
{
    private static final MediaContentHolderJSON json = new MediaContentHolderJSON();

	public static void register(TestGroups groups)
	{
		groups.media.add(Protobuf.MediaTransformer, MediaSerializer);
	}

	// ------------------------------------------------------------
	// Serializers

	public static final Serializer<MediaContent> MediaSerializer = new Serializer<MediaContent>()
	{
		public MediaContent deserialize(byte[] array) throws Exception
		{
			MediaContent.Builder builder = MediaContent.newBuilder();
			JsonParser parser = json.getJsonFactory().createJsonParser(array);
			json.mergeFrom(parser, builder);
			parser.close();
			return builder.build();
		}

		public byte[] serialize(MediaContent content) throws Exception
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream(512);
			json.writeTo(out, content);
			return out.toByteArray();
		}

		public String getName()
		{
			return "protostuff-json";
		}
	};
}
