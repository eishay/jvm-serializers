package serializers.avro;

import org.apache.avro.Schema;

public class Avro
{
	// -------------------------------------------------------------
	// Media

	public static final class Media
	{
		public static final Schema sMediaContent = serializers.avro.media.MediaContent.SCHEMA$;
		public static final Schema sMedia = serializers.avro.media.Media.SCHEMA$;
		public static final Schema sImage = serializers.avro.media.Image.SCHEMA$;

		public static final Schema sImages = sMediaContent.getField("images").schema();
		public static final Schema sPersons = sMedia.getField("persons").schema();
	}

}
