package serializers.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

import serializers.*;
import data.media.MediaContent;

public class JacksonJsonAfterburner
{
    public static void register(TestGroups groups)
    {
        ObjectMapper mapper = new ObjectMapper();

        // to force as-array serialization, uncomment:
//        mapper.setAnnotationIntrospector(new AsArrayIntrospector());

        mapper.registerModule(new AfterburnerModule());
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new StdJacksonDataBind<MediaContent>("json/jackson+afterburner/databind", MediaContent.class, mapper),
                new SerFeatures(
                        SerFormat.JSON,
                        SerGraph.FLAT_TREE,
                        SerClass.ZERO_KNOWLEDGE,
                        "uses bytecode generation to reduce overhead"
                )
        );
    }
}
