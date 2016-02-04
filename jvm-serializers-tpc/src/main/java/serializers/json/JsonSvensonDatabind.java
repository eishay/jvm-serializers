package serializers.json;

import java.io.*;

import serializers.*;

import data.media.Image;
import data.media.MediaContent;

/**
 * This serializer uses svenson for JSON data binding.
 */
public class JsonSvensonDatabind
{
  public static void register(TestGroups groups)
  {
    groups.media.add(JavaBuiltIn.mediaTransformer,
        new GenericSerializer<MediaContent>("json/svenson/databind", MediaContent.class),
            new SerFeatures(
                    SerFormat.JSON,
                    SerGraph.FLAT_TREE,
                    SerClass.MANUAL_OPT,
                    ""
            )
    );
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

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public T deserialize(byte[] array) throws Exception
    {
        return _jsonParser.parse(type, new String(array, "UTF-8"));
    }

    @Override
    public byte[] serialize(T data) throws IOException
    {
        String result = _jsonWriter.forValue(data);
        return result.getBytes();
    }
  }
}
