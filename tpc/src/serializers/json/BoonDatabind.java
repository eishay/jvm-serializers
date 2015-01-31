package serializers.json;

import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;

import data.media.MediaContent;
import serializers.*;

public class BoonDatabind<T> extends Serializer<T>
{
    public static void register(TestGroups groups)
    {
      groups.media.add(JavaBuiltIn.mediaTransformer,
          new BoonDatabind<MediaContent>("json/boon/databind", MediaContent.class),
              new SerFeatures(
                      SerFormat.JSON,
                      SerGraph.FLAT_TREE,
                      SerClass.ZERO_KNOWLEDGE,
                      ""
              )
      );
    }

    private final ObjectMapper mapper = JsonFactory
            .createUseProperties(true);
    
    private final String name;
    private final Class<T> type;

    public BoonDatabind(String n, Class<T> t) {
        name = n;
        type = t;
    }
    
    @Override
    public T deserialize(byte[] array) throws Exception {
        return mapper.readValue(array, type);
    }

    @Override
    public byte[] serialize(T content) throws Exception {
        return mapper.writeValueAsBytes(content);
    }

    @Override
    public String getName() {
        return name;
    }
}
