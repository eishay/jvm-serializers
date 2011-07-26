package serializers;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.smile.SmileFactory;
import org.codehaus.jackson.smile.SmileGenerator;

import serializers.jackson.StdJacksonDataBind;

import data.media.MediaContent;

public class JacksonSmileDatabind
{
    public static void register(TestGroups groups)
    {
        SmileFactory factory = new SmileFactory();
	factory.configure(SmileGenerator.Feature.CHECK_SHARED_NAMES, true);
	factory.configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, false);
//	factory.configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, true);
        ObjectMapper mapper = new ObjectMapper(factory);
        
        groups.media.add(JavaBuiltIn.MediaTransformer,
                new StdJacksonDataBind<MediaContent>("smile/jackson-databind",
                        MediaContent.class, mapper));
    }

}
