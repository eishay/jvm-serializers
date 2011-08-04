package serializers.jackson;

import org.codehaus.jackson.JsonNode;

import serializers.JavaBuiltIn;
import serializers.TestGroups;

import data.media.MediaContent;

/**
 * Driver that uses Jackson for manual tree processing (to/from String).
 */
public class JacksonJsonTreeWithStrings extends JacksonJsonTree
{
  public static void register(TestGroups groups)
  {
    groups.media.add(JavaBuiltIn.MediaTransformer, new JacksonJsonTreeWithStrings("json/jackson/tree-strings"));
  }

    public JacksonJsonTreeWithStrings(String name) {
        super(name);
    }
    
    public MediaContent deserialize(byte[] array) throws Exception
    {
      String json = new String(array, "UTF-8");
      return readMediaContent(mapper.readTree(json));
    }
    
    public byte[] serialize(MediaContent mediaContent) throws Exception
    {
        JsonNode root = asTree(mediaContent, mapper.createObjectNode());
        return mapper.writeValueAsString(root).getBytes("UTF-8");
    }
}
