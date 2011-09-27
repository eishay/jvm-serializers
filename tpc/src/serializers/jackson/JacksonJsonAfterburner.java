package serializers.jackson;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.smile.SmileFactory;
import org.codehaus.jackson.smile.SmileGenerator;

import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

import serializers.JavaBuiltIn;
import serializers.TestGroups;
import data.media.MediaContent;

public class JacksonJsonAfterburner
{
    public static void register(TestGroups groups)
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new AfterburnerModule());

        SmileFactory f = new SmileFactory();
        f.configure(SmileGenerator.Feature.CHECK_SHARED_NAMES, true);
        f.configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, false);
        ObjectMapper smileMapper = new ObjectMapper(f);
        smileMapper.registerModule(new AfterburnerModule());

        groups.media.add(JavaBuiltIn.MediaTransformer,
                new StdJacksonDataBind<MediaContent>("smile/jackson/afterburner", MediaContent.class, smileMapper));

        groups.media.add(JavaBuiltIn.MediaTransformer,
                new StdJacksonDataBind<MediaContent>("json/jackson/afterburner", MediaContent.class, mapper));

    }
}
