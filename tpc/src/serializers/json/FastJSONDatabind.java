package serializers.json;

import java.io.*;

import serializers.*;

import data.media.MediaContent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

/**
 * This serializer uses FastJSON [https://github.com/alibaba/fastjson] for JSON data binding.
 */
public class FastJSONDatabind
{
  public static void register(TestGroups groups)
  {
    groups.media.add(JavaBuiltIn.mediaTransformer,
        new GenericSerializer<MediaContent>("json/fastjson/databind", MediaContent.class),
            new SerFeatures(
                    SerFormat.JSON,
                    SerGraph.FLAT_TREE,
                    SerClass.ZERO_KNOWLEDGE,
                    ""
            )
    );
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

    @Override
    public String getName()
    {
      return name;
    }

    public void serializeItems(T[] items, OutputStream out) throws IOException
    {
      for (int i = 0, len = items.length; i < len; ++i) {
        JSON.writeJSONString(out, items[i], SerializerFeature.WriteEnumUsingToString,
                                         SerializerFeature.DisableCircularReferenceDetect);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(byte[] array) throws Exception
    {
	// fastjson can parse from byte array, yay:
	return (T) JSON.parseObject(array, type, Feature.DisableCircularReferenceDetect);
    }

    @Override
    public byte[] serialize(T data) throws IOException
    {
      return JSON.toJSONBytes(data, SerializerFeature.WriteEnumUsingToString,SerializerFeature.DisableCircularReferenceDetect);
    }
  }
}
