package serializers;

import java.io.ByteArrayOutputStream;

import com.dyuproject.protostuff.json.ProtobufJSON;
import org.codehaus.jackson.JsonParser;

import serializers.protobuf.media.MediaContentHolder.MediaContent;
import serializers.protostuff.MediaContentHolderJSON;
import serializers.protostuff.MediaContentHolderNumericJSON;

/**
 * @author David Yu
 * @created Oct 26, 2009
 */

public class ProtostuffJson
{
	private static final MediaContentHolderJSON json = new MediaContentHolderJSON();
    private static final MediaContentHolderNumericJSON numericJson = new MediaContentHolderNumericJSON();

	public static void register(TestGroups groups)
	{
		groups.media.add(Protobuf.MediaTransformer, new MediaSerializer("", json));
		groups.media.add(Protobuf.MediaTransformer, new MediaSerializer("-numeric", numericJson));
	}

	// ------------------------------------------------------------
	// Serializers

	public static final class MediaSerializer extends Serializer<MediaContent>
	{
		private final String suffix;
		private final ProtobufJSON json;

		public MediaSerializer(String suffix, ProtobufJSON json)
		{
			this.suffix = suffix;
			this.json = json;
		}

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
			return "json/protostuff" + suffix;
		}
	};
}
