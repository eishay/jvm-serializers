package serializers;

import java.io.*;

import data.media.MediaContent;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * This serializer uses Flexjson for data binding.  
 * http://flexjson.sourceforge.net
 */
public class FlexjsonDatabind
{
  public static void register(TestGroups groups)
  {
    groups.media.add(JavaBuiltIn.MediaTransformer,
                    new GenericSerializer<MediaContent>("json/flexjson/databind", MediaContent.class));
  }

  static class GenericSerializer<T> extends Serializer<T>
  {
    private final String name;
    private final Class<T> type;

    public GenericSerializer(String name, Class<T> clazz)
    {
      this.name = name;
      type = clazz;
    }

    public String getName()
    {
      return name;
    }

    public T deserialize(byte[] array) throws Exception
    {
      return new JSONDeserializer<T>().deserialize(new String(array, "UTF-8"), type);
    }

    public byte[] serialize(T data) throws IOException
    {
      String jsonString = new JSONSerializer().exclude("*.class").deepSerialize(data);
      return jsonString.getBytes("UTF-8");
    }
  }
}
