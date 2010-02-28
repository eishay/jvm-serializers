package serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

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
			ByteArrayInputStream bais = new ByteArrayInputStream(array);
			DataInputStream din = new DataInputStream(bais);
			return MediaContent._BinaryReader.read(din);
		}

		public byte[] serialize(MediaContent content) throws Exception
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dout = new DataOutputStream(baos);
			MediaContent._BinaryWriter.write(dout, content);
			return baos.toByteArray();
		}

		public String getName ()
		{
			return "cks/binary";
		}
	};
}
