package serializers.jackson;

import serializers.JavaBuiltIn;
import core.TestGroups;
import de.undercouch.bson4jackson.BsonFactory;

public class JacksonBsonManual
{
    public static void register(TestGroups groups)
    {
        BsonFactory factory = new BsonFactory();
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new JacksonJsonManual("bson/jackson/manual", factory));
    }
}
