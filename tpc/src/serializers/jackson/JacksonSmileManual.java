package serializers.jackson;

import org.codehaus.jackson.smile.SmileFactory;
import org.codehaus.jackson.smile.SmileGenerator;

import serializers.JavaBuiltIn;
import serializers.TestGroups;

public class JacksonSmileManual
{
    public static void register(TestGroups groups)
    {
        SmileFactory factory = new SmileFactory();
	factory.configure(SmileGenerator.Feature.CHECK_SHARED_NAMES, true);
//	factory.configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, true);
	factory.configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, false);
        groups.media.add(JavaBuiltIn.MediaTransformer, new JacksonJsonManual("smile/jackson/manual", factory));
    }

}
