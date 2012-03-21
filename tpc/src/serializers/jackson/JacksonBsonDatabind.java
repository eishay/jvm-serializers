package serializers.jackson;

import core.TestGroups;
import data.media.MediaContent;

import org.codehaus.jackson.map.ObjectMapper;

import serializers.JavaBuiltIn;
import core.TestGroups;
import de.undercouch.bson4jackson.BsonFactory;

/**
 * This serializer uses bson4jackson in full automated data binding mode, which
 * can handle typical Java POJOs (esp. beans; otherwise may need to annotate
 * to configure)
 */
public class JacksonBsonDatabind
{
	public static void register(TestGroups groups)
	{
	    ObjectMapper mapper = new ObjectMapper(new BsonFactory());
	    groups.media.add(JavaBuiltIn.mediaTransformer,
	            new StdJacksonDataBind<MediaContent>(
	                    "bson/jackson/databind", MediaContent.class, mapper));
	}
}
