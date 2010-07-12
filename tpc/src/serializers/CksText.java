package serializers;

import serializers.cks.media.MediaContent;

public class CksText
{
	public static void register(TestGroups groups)
	{
		groups.media.add(Cks.MediaTransformer, MediaSerializer, "cks");
	}

	// ------------------------------------------------------------
	// Serializers

	public static final Serializer<MediaContent> MediaSerializer = new Serializer<MediaContent>()
	{
		public MediaContent deserialize(byte[] array) throws Exception
		{
			return MediaContent._TextReader.readFromByteArray(array);
		}

		public byte[] serialize(MediaContent content) throws Exception
		{
			return MediaContent._TextWriter.writeToByteArray(content);
		}

		public String getName ()
		{
			return "cks-text";
		}
	};
}

