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
      org.svenson.tokenize.JSONCharacterSource source = 
        new org.svenson.tokenize.InputStreamSource(new ByteArrayInputStream(array), true);
      T result = _jsonParser.parse(type, source);
      return result;
    }

    public byte[] serialize(T data) throws IOException
    {
      ByteArrayOutputStream baos = outputStream(data);
      OutputStreamWriter w = new OutputStreamWriter(baos, "UTF-8");
      _jsonWriter.writeJSONToWriter(data, w);
      w.close();
      return baos.toByteArray();
    }
  }
}