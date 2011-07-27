package serializers.json;

import data.media.MediaContent;

import org.codehaus.jackson.map.*;

import serializers.JavaBuiltIn;
import serializers.TestGroups;
import serializers.jackson.StdJacksonDataBind;

import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

/**
 * Variant of Jackson data-binder that uses Afterburner to
 * use code generation for faster data access
 */
public class JsonJacksonAfterburner
{
    public static void register(TestGroups groups)
    {
        AfterburnerModule module = new AfterburnerModule();
        // Since tests are run on single JVM do this:
        // (avoid polluting value class loader as we don't need to
        // access private fields)
        module.setUseValueClassLoader(false);

        ObjectMapper mapper = new ObjectMapper(new org.codehaus.jackson.smile.SmileFactory());
        mapper.registerModule(module);
        groups.media.add(JavaBuiltIn.MediaTransformer,
                new StdJacksonDataBind<MediaContent>("smile/jackson-MACH-5",
                        MediaContent.class, mapper));

        mapper = new ObjectMapper(new org.codehaus.jackson.JsonFactory());
        mapper.registerModule(module);
        groups.media.add(JavaBuiltIn.MediaTransformer,
                new StdJacksonDataBind<MediaContent>("json/jackson-MACH-5",
                        MediaContent.class, mapper));
    }
}
