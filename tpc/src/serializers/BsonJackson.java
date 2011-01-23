package serializers;

import de.undercouch.bson4jackson.BsonFactory;
import de.undercouch.bson4jackson.BsonGenerator;

public class BsonJackson
{
	public static void register(TestGroups groups)
	{
		BsonFactory factory = new BsonFactory();
		groups.media.add(JavaBuiltIn.MediaTransformer,
			new JsonJacksonManual.GenericSerializer("bson/jackson-manual", factory));
	}
}
