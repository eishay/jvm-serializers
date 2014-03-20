package serializers.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.*;

import serializers.JavaBuiltIn;
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
                new StdJacksonDataBind<MediaContent>("CBOR/jackson/databind",
                        MediaContent.class, mapper));

        groups.media.add(JavaBuiltIn.mediaTransformer, new JacksonJsonManual("CBOR/jackson/manual", factory));
    }

}
