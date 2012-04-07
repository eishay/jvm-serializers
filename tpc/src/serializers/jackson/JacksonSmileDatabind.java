package serializers.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.fasterxml.jackson.dataformat.smile.*;

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
	// no point in using enum names with binary format, so:
        ObjectMapper mapper = new ObjectMapper(factory);
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
        
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new StdJacksonDataBind<MediaContent>("smile/jackson/databind",
                        MediaContent.class, mapper));
    }

}
