package serializers.jackson;

import serializers.JavaBuiltIn;
import serializers.TestGroups;

import com.fasterxml.jackson.databind.*;

import data.media.MediaContent;

/**
 * Codec(s) for serializing logical JSON content as JSON Array instead
 * of the usual JSON Object; this condenses data size significantly and
 * typically improves performance similarly.
 */
public class JacksonAsArrayDatabind
{
    public static void register(TestGroups groups)
    {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.setAnnotationIntrospector(new AsArrayIntrospector());
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new StdJacksonDataBind<MediaContent>("json/jackson-ARRAY/databind", MediaContent.class, jsonMapper));

        // and Smile one?
        ObjectMapper smileMapper = new ObjectMapper(new com.fasterxml.jackson.dataformat.smile.SmileFactory());
        smileMapper.setAnnotationIntrospector(new AsArrayIntrospector());
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new StdJacksonDataBind<MediaContent>("smile/jackson-ARRAY/databind", MediaContent.class, smileMapper));
    }
}
