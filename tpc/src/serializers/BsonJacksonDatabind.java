package serializers;

import data.media.MediaContent;

import org.codehaus.jackson.map.ObjectMapper;
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
	}
}
