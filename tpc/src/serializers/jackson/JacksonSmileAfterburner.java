package serializers.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.smile.SmileGenerator;

import serializers.*;

import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

import data.media.MediaContent;

public class JacksonSmileAfterburner
{
    // default settings: name backrefs, but no value backrefs
    public static void register(TestGroups groups) {
        register(groups, true, false);
    }

    public static void register(TestGroups groups, boolean sharedNames, boolean sharedValues)
    {
        SmileFactory f = new SmileFactory();
        // use defaults: shared names, not values (but set explicitly just in case)
        f.configure(SmileGenerator.Feature.CHECK_SHARED_NAMES, sharedNames);
        f.configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, sharedValues);
        ObjectMapper smileMapper = new ObjectMapper(f);

        // to force as-array serialization, uncomment:
//        smileMapper.setAnnotationIntrospector(new AsArrayIntrospector());
        
        smileMapper.registerModule(new AfterburnerModule());
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new StdJacksonDataBind<MediaContent>("smile/jackson+afterburner/databind", MediaContent.class, smileMapper),
                new SerFeatures(
                        SerFormat.BINARY,
                        SerGraph.FLAT_TREE,
                        SerClass.ZERO_KNOWLEDGE,
                        ""
                )
        );
    }
}
