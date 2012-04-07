package serializers.jackson;

import com.fasterxml.jackson.dataformat.smile.*;

import serializers.JavaBuiltIn;
import serializers.TestGroups;

public class JacksonSmileManual
{
    public static void register(TestGroups groups) { // Jackson Smile defaults: share names, not values
        register(groups, true, false);
    }

    public static void register(TestGroups groups, boolean sharedNames, boolean sharedValues)
    {
        SmileFactory factory = new SmileFactory();
	factory.configure(SmileGenerator.Feature.CHECK_SHARED_NAMES, sharedNames);
	factory.configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, sharedValues);
        groups.media.add(JavaBuiltIn.mediaTransformer, new JacksonJsonManual("smile/jackson/manual", factory));
    }

}
