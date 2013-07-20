package serializers.json;

import java.io.*;

import serializers.JavaBuiltIn;
import serializers.Serializer;
import serializers.TestGroups;

import data.media.MediaContent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.parser.Feature;

/**
 * This serializer uses FastJSON [http://code.alibabatech.com/wiki/display/FastJSON] for JSON data binding.
 */
public class FastJSONDatabindArray
{
  public static void register(TestGroups groups)
  {
    groups.media.add(JavaBuiltIn.mediaTransformer,
        new GenericSerializer<MediaContent>("json/fastjson/databind-array", MediaContent.class));
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

    @SuppressWarnings("unchecked")
    public T deserialize(byte[] array) throws Exception
    {
	// fastjson can parse from byte array, yay:
	return (T) JSON.parseObject(array, type, Feature.DisableCircularReferenceDetect, Feature.SupportArrayToBean);
    }

    public byte[] serialize(T data) throws IOException
    {
      return JSON.toJSONBytes(data, SerializerFeature.WriteEnumUsingToString,
    		  SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.BeanToArray);
    }
  }
}
