package serializers.jackson;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import serializers.*;

import data.media.MediaContent;

/**
 * Driver that uses Jackson for manual tree processing (to/from String).
 */
public class JacksonJsonTreeWithStrings extends JacksonJsonTree
{
  public static void register(TestGroups groups)
  {
    groups.media.add(JavaBuiltIn.mediaTransformer,
            new JacksonJsonTreeWithStrings("json/jackson/tree+strings", new ObjectMapper()),
            new SerFeatures(
                    SerFormat.JSON,
                    SerGraph.FLAT_TREE,
                    SerClass.MANUAL_OPT,
                    ""
            )
    );
  }

    public JacksonJsonTreeWithStrings(String name, ObjectMapper mapper) {
        super(name, mapper);
    }

    @Override
    public MediaContent deserialize(byte[] array) throws IOException
    {
      String json = new String(array, "UTF-8");
      return readMediaContent(mapper.readTree(json));
    }
    
    @Override
    public byte[] serialize(MediaContent mediaContent) throws IOException
    {
        JsonNode root = asTree(mediaContent, mapper.createObjectNode());
        return mapper.writeValueAsString(root).getBytes("UTF-8");
    }

    // Note: we won't override multi-data write methods from base class
}
