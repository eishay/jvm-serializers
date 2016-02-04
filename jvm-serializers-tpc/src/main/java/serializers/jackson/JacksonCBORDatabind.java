package serializers.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.*;

import serializers.JavaBuiltIn;
import serializers.SerClass;
import serializers.SerFeatures;
import serializers.SerFormat;
import serializers.SerGraph;
import serializers.TestGroups;
import data.media.MediaContent;

public class JacksonCBORDatabind
{
    public static void register(TestGroups groups) { // Jackson Smile defaults: share names, not values
        register(groups, true, false);
    }

    public static void register(TestGroups groups, boolean sharedNames, boolean sharedValues)
    {
        CBORFactory factory = new CBORFactory();
        // no point in using enum names with binary format, so:
        ObjectMapper mapper = new ObjectMapper(factory);
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new StdJacksonDataBind<MediaContent>("cbor/jackson/databind", MediaContent.class, mapper),
                new SerFeatures(SerFormat.BIN_CROSSLANG,
                        SerGraph.FLAT_TREE,
                        SerClass.ZERO_KNOWLEDGE,
                        ""
                ));

        groups.media.add(JavaBuiltIn.mediaTransformer,
                new JacksonJsonManual("cbor/jackson/manual", factory),
                new SerFeatures(SerFormat.BIN_CROSSLANG,
                        SerGraph.FLAT_TREE,
                        SerClass.MANUAL_OPT,
                        ""
                ));
    }

}
