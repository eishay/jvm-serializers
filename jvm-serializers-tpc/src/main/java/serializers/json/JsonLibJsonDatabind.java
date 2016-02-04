package serializers.json;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import serializers.*;

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
    groups.media.add(JavaBuiltIn.mediaTransformer,
        new GenericSerializer<MediaContent>("json/json-lib/databind", MediaContent.class),
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
    // private final net.sf.ezmorph.MorpherRegistry _morpherRegistry;
    private final net.sf.json.JsonConfig _jsonConfig;

    public GenericSerializer(String name, Class<T> clazz)
    {
      this.name = name;
      type = clazz;

      net.sf.ezmorph.MorpherRegistry _morpherRegistry = net.sf.json.util.JSONUtils.getMorpherRegistry();
      _morpherRegistry.registerMorpher(new net.sf.json.util.EnumMorpher(Media.Player.class));
      _morpherRegistry.registerMorpher(new net.sf.json.util.EnumMorpher(Image.Size.class));
      // "preferred" approach with BeanMorpher causes excessive info logging
      // net.sf.ezmorph.Morpher imageMorpher = new net.sf.ezmorph.bean.BeanMorpher(Image.class, _morpherRegistry);
      // _morpherRegistry.registerMorpher(imageMorpher);

      _jsonConfig = new net.sf.json.JsonConfig();
      _jsonConfig.setRootClass(type);

      // else JSON null is turned into empty string, which fails equality tests
      _jsonConfig.registerDefaultValueProcessor(String.class, 
          new net.sf.json.processors.DefaultValueProcessor() 
          {
            @Override
            public Object getDefaultValue(Class type)
            {
                return net.sf.json.JSONNull.getInstance();
            }
          });
    }

    public String getName()
    {
      return name;
    }

    @SuppressWarnings("unchecked")
    public T deserialize(byte[] array) throws Exception
    {
      String jsonInput = new String(array, "UTF-8");
      net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(jsonInput);
      T result = (T) net.sf.json.JSONObject.toBean(jsonObject, _jsonConfig);

      // JSON-lib cannot deserialize into a list of a custom type, when the list is a member of something.
      // Instead, JSON-lib populates the list with MorphDynaBeans, which can be transformed to the target type.
      MediaContent mediaContent = (MediaContent) result;
      int size = mediaContent.images.size();
      List<Image> replacementImages = new ArrayList<Image> (size);
      for (int i = 0; i < size; i++)
      {
        // "preferred" approach with BeanMorpher causes excessive info logging -- TODO: figure out how to config
        // Object image = mediaContent.images.get(i);
        // replacementImages.add((Image)_morpherRegistry.morph(Image.class, image));

        // alternate approach, still using JSON-lib components to transform data
        Object o = mediaContent.images.get(i);
        net.sf.ezmorph.bean.MorphDynaBean image = (net.sf.ezmorph.bean.MorphDynaBean) o;
        net.sf.json.JSONObject imageJsonObject = net.sf.json.JSONObject.fromObject(image);
        Image replacementImage = (Image) net.sf.json.JSONObject.toBean(imageJsonObject, Image.class);

        // a slightly "manual" approach (?) that is about 10% faster, but isn't it cheating?
        //Image replacementImage = new Image();
        //replacementImage.height = (Integer) image.get("height");
        //replacementImage.size = Image.Size.valueOf((String) image.get("size"));
        //replacementImage.title = (String) image.get("title");
        //replacementImage.uri = (String) image.get("uri");
        //replacementImage.width = (Integer) image.get("width");

        replacementImages.add(replacementImage);
      }
      mediaContent.images = replacementImages;
      return result;
    }

    public byte[] serialize(T data) throws IOException
    {
      StringWriter w = new StringWriter();
      net.sf.json.JSONSerializer.toJSON(data, _jsonConfig).write(w);
      w.flush();
      return w.toString().getBytes("UTF-8");
    }
  }
}
