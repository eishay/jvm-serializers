package serializers;

import data.media.MediaContent;

import org.codehaus.jackson.map.ObjectMapper;

import serializers.jackson.StdJacksonDataBind;
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
	    ObjectMapper mapper = new ObjectMapper(new BsonFactory());
	    groups.media.add(JavaBuiltIn.MediaTransformer,
	            new StdJacksonDataBind<MediaContent>(
	                    "bson/jackson-databind", MediaContent.class, mapper));
	}
}
