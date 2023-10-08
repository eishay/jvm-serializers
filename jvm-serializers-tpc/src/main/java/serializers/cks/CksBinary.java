package serializers.cks;

import java.io.InputStream;
import java.io.OutputStream;

import serializers.*;
import serializers.cks.media.MediaContent;

public class CksBinary
{
	public static void register(TestGroups groups)
	{
		groups.media.add(Cks.mediaTransformer, MediaSerializer,
                new SerFeatures(
                        SerFormat.MISC,
                        SerGraph.FLAT_TREE,
                        SerClass.CLASSES_KNOWN,
                        ""
                )
        );
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

	        public MediaContent[] deserializeItems(InputStream in, int numberOfItems) throws Exception
	        {
	            MediaContent[] result = new MediaContent[numberOfItems];
	            for (int i = 0; i < numberOfItems; ++i) {
	                result[i] = MediaContent._BinaryReader.readFromStream(in);
	            }
	            return result;
	        }

	        public void serializeItems(MediaContent[] items, OutputStream out) throws Exception
	        {
	            for (MediaContent item : items) {
	                MediaContent._BinaryWriter.write(out, item);
	            }
	        }
	};
}
