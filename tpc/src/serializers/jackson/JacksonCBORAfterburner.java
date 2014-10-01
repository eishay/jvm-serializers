package serializers.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.dataformat.cbor.*;

import serializers.*;

import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

import data.media.MediaContent;

public class JacksonCBORAfterburner
{
    public static void register(TestGroups groups) {
        register(groups, true, false);
    }

    public static void register(TestGroups groups, boolean sharedNames, boolean sharedValues)
    {
        ObjectMapper mapper = new ObjectMapper(new CBORFactory());

        // to force as-array serialization, uncomment:
//        mapper.setAnnotationIntrospector(new AsArrayIntrospector());
        
        mapper.registerModule(new AfterburnerModule());
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new StdJacksonDataBind<MediaContent>("cbor/jackson+afterburner/databind", MediaContent.class, mapper),
                new SerFeatures(
                        SerFormat.BINARY,
                        SerGraph.FLAT_TREE,
                        SerClass.ZERO_KNOWLEDGE,
                        "uses bytecode generation to reduce overhead"
                )
        );
    }
}
