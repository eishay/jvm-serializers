package serializers.cks;

import java.io.*;

import serializers.Serializer;
import serializers.TestGroups;
import serializers.cks.media.MediaContent;

public class CksText
{
	public static void register(TestGroups groups)
	{
		groups.media.add(Cks.mediaTransformer, MediaSerializer, "cks");
	}
	
	// ------------------------------------------------------------
	// Serializers

        private final static byte[] FOUR_ATS = "@@@@".getBytes();
	
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

		// Note: code below is inefficient and ugly; but we need just for bootstrapping and
		// it's never included in measurements.

		public MediaContent[] deserializeItems(InputStream in, int numberOfItems) throws Exception
                {
                    MediaContent[] result = new MediaContent[numberOfItems];
                    // very inefficient; but performance of CKS is never compared, hence no worries
                    byte[] bytes = readAll(in);
                    String text = new String(bytes, "UTF-8");
                    String[] parts = text.split("@@@@");
                    if (parts.length != numberOfItems) {
                        throw new IOException("Expected "+numberOfItems+", found "+parts.length+" CKS segments");
                    }
                    for (int i = 0; i < numberOfItems; ++i) {
                        result[i] = MediaContent._TextReader.read(new StringReader(parts[i]));
                    }
                    return result;
                }

                public void serializeItems(MediaContent[] items, OutputStream out) throws Exception
                {
                    for (int i = 0, len = items.length; i < len; ++i) {
                        if (i > 0) {
                            out.write(FOUR_ATS);
                        }
                        MediaContent._TextWriter.write(out, items[i]);
                    }
                }
	};
}

