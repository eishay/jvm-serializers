package serializers.jackson;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.smile.SmileFactory;
import org.codehaus.jackson.smile.SmileGenerator;

import serializers.JavaBuiltIn;
import serializers.TestGroups;

import data.media.MediaContent;

public class JacksonSmileDatabind
{
    public static void register(TestGroups groups) { // Jackson Smile defaults: share names, not values
        register(groups, true, false);
    }

    public static void register(TestGroups groups, boolean sharedNames, boolean sharedValues)
    {
        SmileFactory factory = new SmileFactory();
	factory.configure(SmileGenerator.Feature.CHECK_SHARED_NAMES, sharedNames);
	factory.configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, sharedValues);
        ObjectMapper mapper = new ObjectMapper(factory);
        
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new StdJacksonDataBind<MediaContent>("smile/jackson/databind",
                        MediaContent.class, mapper));
    }

}
