package serializers.jackson;

import org.codehaus.jackson.smile.SmileFactory;
import org.codehaus.jackson.smile.SmileGenerator;

import serializers.JavaBuiltIn;
import core.TestGroups;

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
