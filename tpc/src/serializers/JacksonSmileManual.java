package serializers;

import org.codehaus.jackson.smile.SmileFactory;
import org.codehaus.jackson.smile.SmileGenerator;

public class JacksonSmileManual
{
    public static void register(TestGroups groups)
    {
        SmileFactory factory = new SmileFactory();
	factory.configure(SmileGenerator.Feature.CHECK_SHARED_NAMES, true);
//	factory.configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, true);
	factory.configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, false);
        groups.media.add(JavaBuiltIn.MediaTransformer, new JsonJacksonManual.GenericSerializer("smile/jackson-manual", factory));
    }

}
