package serializers.json;

import java.io.*;

import serializers.JavaBuiltIn;
import serializers.Serializer;
import serializers.TestGroups;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * This serializer uses FastJSON [http://code.alibabatech.com/wiki/display/FastJSON] for JSON data binding.
 */
public class FastJSONDatabind
{
  public static void register(TestGroups groups)
  {
    groups.media.add(JavaBuiltIn.MediaTransformer,
        new GenericSerializer<MediaContent>("json/fastjson/databind", MediaContent.class));
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
      String jsonInput = new String(array, "UTF-8");
      T result = JSON.parseObject(jsonInput, type);
      return result;
    }

    public byte[] serialize(T data) throws IOException
    {
      String jsonString = JSON.toJSONString(data, SerializerFeature.WriteEnumUsingToString);
      return jsonString.getBytes("UTF-8");
    }
  }
}
