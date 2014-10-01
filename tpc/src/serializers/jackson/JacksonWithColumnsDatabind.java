package serializers.jackson;

import serializers.*;

import com.fasterxml.jackson.databind.*;

import data.media.MediaContent;

/**
 * Codec(s) for serializing logical JSON content as JSON Array instead
 * of the usual JSON Object; this condenses data size significantly and
 * typically improves performance similarly.
 */
public class JacksonWithColumnsDatabind
{
    private final static String STD_DESC = "uses positional (column) laytout to eliminate use of names";

    public static void registerAll(TestGroups groups)
    {
        registerJSON(groups);
        registerSmile(groups);
        registerCBOR(groups);
    }

    public static void registerCBOR(TestGroups groups)
    {
        ObjectMapper cborMapper = new ObjectMapper(new com.fasterxml.jackson.dataformat.cbor.CBORFactory());
        cborMapper.setAnnotationIntrospector(new AsArrayIntrospector());
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new StdJacksonDataBind<MediaContent>("cbor-col/jackson/databind", MediaContent.class, cborMapper),
                new SerFeatures(SerFormat.JSON,SerGraph.FLAT_TREE,SerClass.ZERO_KNOWLEDGE,STD_DESC));
    }
    
    public static void registerJSON(TestGroups groups)
    {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.setAnnotationIntrospector(new AsArrayIntrospector());
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new StdJacksonDataBind<MediaContent>("json-col/jackson/databind", MediaContent.class, jsonMapper),
                new SerFeatures(SerFormat.JSON,SerGraph.FLAT_TREE,SerClass.ZERO_KNOWLEDGE,STD_DESC));
    }

    public static void registerSmile(TestGroups groups)
    {
        ObjectMapper smileMapper = new ObjectMapper(new com.fasterxml.jackson.dataformat.smile.SmileFactory());
        smileMapper.setAnnotationIntrospector(new AsArrayIntrospector());
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new StdJacksonDataBind<MediaContent>("smile-col/jackson/databind", MediaContent.class, smileMapper),
                new SerFeatures(SerFormat.JSON,SerGraph.FLAT_TREE,SerClass.ZERO_KNOWLEDGE,STD_DESC));
    }
}
