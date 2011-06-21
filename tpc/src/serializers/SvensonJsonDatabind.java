package serializers;

import java.io.*;

import data.media.Image;
import data.media.MediaContent;

/**
 * This serializer uses svenson for JSON data binding.
 */
public class SvensonJsonDatabind
{
  public static void register(TestGroups groups)
  {
    groups.media.add(JavaBuiltIn.MediaTransformer,
        new GenericSerializer<MediaContent>("json/svenson-databind", MediaContent.class));
  }

  static class GenericSerializer<T> extends Serializer<T>
  {
    private final org.svenson.JSONParser _jsonParser;
    private final org.svenson.JSON _jsonWriter;
    private final String name;
    private final Class<T> type;

    public GenericSerializer(String name, Class<T> clazz)
    {
      this.name = name;
      type = clazz;
      
      _jsonParser = org.svenson.JSONParser.defaultJSONParser();
      _jsonParser.addTypeHint(".images[]", Image.class);
      _jsonWriter = org.svenson.JSON.defaultJSON();
    }

    public String getName()
    {
      return name;
    }

    public T deserialize(byte[] array) throws Exception
    {
      return _jsonParser.parse(type, new String(array, "UTF-8"));
    }

    public byte[] serialize(T data) throws IOException
    {
      String result = _jsonWriter.forValue(data);
      return result.getBytes();
    }
  }
}
