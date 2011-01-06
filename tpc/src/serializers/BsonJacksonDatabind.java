package serializers;

import java.io.IOException;

import data.media.MediaContent;

import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import de.undercouch.bson4jackson.BsonFactory;

/**
 * This serializer uses bson4jackson in full automated data binding mode, which
 * can handle typical Java POJOs (esp. beans; otherwise may need to annotate
 * to configure)
 */
public class BsonJacksonDatabind
{
	public static void register(TestGroups groups)
	{
		BsonFactory factory = new BsonFactory();
		ObjectMapper mapper = new ObjectMapper(factory);
		groups.media.add(JavaBuiltIn.MediaTransformer,
			new JsonJacksonDatabind.GenericSerializer<MediaContent>(
				"bson/jackson-databind", mapper, MediaContent.class));
		
		BsonFactory factory2 = new BsonFactory();
		ObjectMapper mapper2 = new ObjectMapper(factory2);
		groups.media.add(JavaBuiltIn.MediaTransformer,
			new JsonJacksonDatabind.GenericSerializer<MediaContent>(
				"bson/jackson-databind-streaming", mapper2, MediaContent.class));
	}
}
