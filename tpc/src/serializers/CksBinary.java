package serializers;

import serializers.cks.media.MediaContent;

public class CksBinary
{
	public static void register(TestGroups groups)
	{
		groups.media.add(Cks.MediaTransformer, MediaSerializer);
	}

	// ------------------------------------------------------------
	// Serializers

	public static final Serializer<MediaContent> MediaSerializer = new Serializer<MediaContent>()
	{
		public MediaContent deserialize(byte[] array) throws Exception
		{
			return MediaContent._BinaryReader.readFromByteArray(array);
		}

		public byte[] serialize(MediaContent content) throws Exception
		{
			return MediaContent._BinaryWriter.writeToByteArray(content);
		}

		public String getName ()
		{
			return "cks";
		}
	};
}
