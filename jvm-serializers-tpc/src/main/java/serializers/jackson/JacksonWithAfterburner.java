package serializers.jackson;

import serializers.JavaBuiltIn;
import serializers.SerClass;
import serializers.SerFeatures;
import serializers.SerFormat;
import serializers.SerGraph;
import serializers.TestGroups;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.smile.SmileGenerator;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

import data.media.MediaContent;

/**
 * Class for registering variants of Jackson-based tests that use Afterburner
 * for generating byte code to avoid use of Reflection for calling methods
 * and constructing objects.
 */
public class JacksonWithAfterburner
{
    final static String STD_DESC = "uses bytecode generation to reduce overhead";
    
    public static void registerAll(TestGroups groups)
    {
        registerJSON(groups);
        registerSmile(groups);
        registerCBOR(groups);
    }

    public static void registerJSON(TestGroups groups)
    {
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        mapper.registerModule(new AfterburnerModule());
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new StdJacksonDataBind<MediaContent>("json/jackson+afterburner/databind", MediaContent.class, mapper),
                new SerFeatures(SerFormat.BINARY, SerGraph.FLAT_TREE, SerClass.ZERO_KNOWLEDGE, STD_DESC));
    }
    
    public static void registerCBOR(TestGroups groups)
    {
        ObjectMapper mapper = new ObjectMapper(new CBORFactory());
        mapper.registerModule(new AfterburnerModule());
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new StdJacksonDataBind<MediaContent>("cbor/jackson+afterburner/databind", MediaContent.class, mapper),
                new SerFeatures(SerFormat.BINARY, SerGraph.FLAT_TREE, SerClass.ZERO_KNOWLEDGE, STD_DESC));
    }

    public static void registerSmile(TestGroups groups) {
        // use defaults: shared names, not values (but set explicitly just in case)
        // sharing can reduce size, but also adds some processing overhead -- typically name sharing
        // cheap, value sharing bit more expensive; hence the defaults
        registerSmile(groups, true, false);
    }

    public static void registerSmile(TestGroups groups, boolean shareNames, boolean shareValues)
    {
        SmileFactory f = new SmileFactory();
        f.configure(SmileGenerator.Feature.CHECK_SHARED_NAMES, shareNames);
        f.configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, shareValues);
        ObjectMapper smileMapper = new ObjectMapper(f);

        smileMapper.registerModule(new AfterburnerModule());
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new StdJacksonDataBind<MediaContent>("smile/jackson+afterburner/databind", MediaContent.class, smileMapper),
                new SerFeatures(SerFormat.BINARY, SerGraph.FLAT_TREE, SerClass.ZERO_KNOWLEDGE, STD_DESC));
    }
}
