package serializers.jackson;

import org.codehaus.jackson.map.ObjectMapper;

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
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new StdJacksonDataBind<MediaContent>("json/jackson/db-afterburner", MediaContent.class, mapper));
    }
}
