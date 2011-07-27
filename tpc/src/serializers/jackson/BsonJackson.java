package serializers.jackson;

import serializers.JavaBuiltIn;
import serializers.TestGroups;
import de.undercouch.bson4jackson.BsonFactory;

public class BsonJackson
{
	public static void register(TestGroups groups)
	{
		BsonFactory factory = new BsonFactory();
		groups.media.add(JavaBuiltIn.MediaTransformer,
			new JsonJacksonManual.GenericSerializer("bson/jackson-manual", factory));
	}
}
