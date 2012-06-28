package serializers.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import serializers.JavaBuiltIn;
import serializers.TestGroups;

import data.media.MediaContent;

public class JacksonYAMLDatabind
{
    public static void register(TestGroups groups)
    {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new StdJacksonDataBind<MediaContent>("yaml/jackson",
                        MediaContent.class, mapper));
    }
}
