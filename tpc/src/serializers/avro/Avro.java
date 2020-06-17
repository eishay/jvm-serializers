package serializers.avro;

import java.util.EnumSet;
import org.apache.avro.Schema;
import serializers.MiscFeatures;


public class Avro
{
	public static final EnumSet<MiscFeatures> miscFeatures = EnumSet.of(
			MiscFeatures.VERSIONING_BACKWARD_COMPATIBLE,
			MiscFeatures.VERSIONING_FORWARD_COMPATIBLE,
			MiscFeatures.VERSIONING_MISMATCH_DETECTION);

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
