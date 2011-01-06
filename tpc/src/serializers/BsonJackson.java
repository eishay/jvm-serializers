package serializers;

import data.media.*;
import static data.media.FieldMapping.*;

import de.undercouch.bson4jackson.BsonFactory;
import de.undercouch.bson4jackson.BsonGenerator;

public class BsonJackson
{
	public static void register(TestGroups groups)
	{
		BsonFactory factory = new BsonFactory();
		groups.media.add(JavaBuiltIn.MediaTransformer,
			new JsonJackson.GenericSerializer("bson/jackson-manual", factory));
		
		BsonFactory factory2 = new BsonFactory();
		factory2.configure(BsonGenerator.Feature.ENABLE_STREAMING, true);
		groups.media.add(JavaBuiltIn.MediaTransformer,
			new JsonJackson.GenericSerializer("bson/jackson-manual-streaming", factory2));
	}
}
