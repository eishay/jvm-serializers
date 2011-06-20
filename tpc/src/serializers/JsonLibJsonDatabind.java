package serializers;

import java.io.*;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

/**
 * This serializer uses JSON-lib [http://json-lib.sourceforge.net] for JSON data binding.
 */
public class JsonLibJsonDatabind
{
  public static void register(TestGroups groups)
  {
    groups.media.add(JavaBuiltIn.MediaTransformer,
        new GenericSerializer<MediaContent>("json/json-lib-databind", MediaContent.class));
  }

  static class GenericSerializer<T> extends Serializer<T>
  {
    private final String name;
    private final Class<T> type;

    public GenericSerializer(String name, Class<T> clazz)
    {
      this.name = name;
      type = clazz;

      net.sf.json.util.JSONUtils.getMorpherRegistry().registerMorpher(
          new net.sf.json.util.EnumMorpher(Media.Player.class));
      net.sf.json.util.JSONUtils.getMorpherRegistry().registerMorpher(
          new net.sf.json.util.EnumMorpher(Image.Size.class));
    }

    public String getName()
    {
      return name;
    }

    public T deserialize(byte[] array) throws Exception
    {
      String jsonInput = new String(array, "UTF-8");
      net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(jsonInput);
      @SuppressWarnings("unchecked")
      T result = (T) net.sf.json.JSONObject.toBean(jsonObject, type);
      return result;
    }

    public byte[] serialize(T data) throws IOException
    {
      ByteArrayOutputStream baos = outputStream(data);
      OutputStreamWriter w = new OutputStreamWriter(baos, "UTF-8");
      net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(data);
      jsonObject.write(w);
      w.close();
      return baos.toByteArray();
    }
  }
}