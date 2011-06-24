package serializers;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.jayway.jsonpath.JsonPath;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

/**
 * Driver that uses JsonPath [http://code.google.com/p/json-path/], with JSONPath parsing.
 * Uses JSON.simple for serialization.
 */
public class JsonPathDeserializerOnly
{
  public static void register(TestGroups groups)
  {
    groups.media.add(JavaBuiltIn.MediaTransformer,
        new SemiManualSerializer("json/jsonpath/json.simple"));
  }

  static class SemiManualSerializer extends Serializer<MediaContent>
  {
    private final String name;

    public SemiManualSerializer(String name)
    {
      this.name = name;
    }

    public String getName()
    {
      return name;
    }

    public MediaContent deserialize(byte[] array) throws Exception
    {
      String mediaContentJsonInput = new String(array, "UTF-8");
      return readMediaContent(mediaContentJsonInput);
    }

    public byte[] serialize(MediaContent mediaContent) throws Exception
    {
      StringWriter writer = new StringWriter();
      writeMediaContent(writer, mediaContent);
      writer.flush();
      return writer.toString().getBytes("UTF-8");
    }

    @SuppressWarnings("unchecked")
    private static JSONObject createJsonObject(Media media)
    {
      JSONObject jsonObject = new JSONObject();
      if (media.hasBitrate)
      {
        jsonObject.put("bitrate", media.bitrate);
      }
      jsonObject.put("copyright", media.copyright);
      jsonObject.put("duration", media.duration);
      jsonObject.put("format", media.format);
      jsonObject.put("height", media.height);
      int size = media.persons.size();
      JSONArray personsJsonArray = new JSONArray();
      for (int i = 0; i < size; i++)
      {
        personsJsonArray.add(media.persons.get(i));
      }
      jsonObject.put("persons", personsJsonArray);
      jsonObject.put("player", media.player.name());
      jsonObject.put("size", media.size);
      jsonObject.put("title", media.title);
      jsonObject.put("uri", media.uri);
      jsonObject.put("width", media.width);
      return jsonObject;
    }

    private static void writeMedia(Writer writer, Media media) throws Exception
    {
      JSONObject jsonObject = createJsonObject(media);
      jsonObject.writeJSONString(writer);
    }

    @SuppressWarnings("unchecked")
    private static JSONObject createJsonObject(Image image)
    {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("height", image.height);
      jsonObject.put("size", image.size.name());
      jsonObject.put("title", image.title);
      jsonObject.put("uri", image.uri);
      jsonObject.put("width", image.width);
      return jsonObject;
    }

    private static void writeImage(Writer writer, Image image) throws Exception
    {
      JSONObject jsonObject = createJsonObject(image);
      jsonObject.writeJSONString(writer);
    }

    private static void writeImages(Writer writer, List<Image> images) throws Exception
    {
      JSONArray jsonArray = createJsonArray(images);
      jsonArray.writeJSONString(writer);
    }

    @SuppressWarnings("unchecked")
    private static JSONArray createJsonArray(List<Image> images)
    {
      JSONArray jsonArray = new JSONArray();
      for (Image image : images)
      {
        jsonArray.add(createJsonObject(image));
      }
      return jsonArray;
    }

    @SuppressWarnings("unchecked")
    static void writeMediaContent(Writer writer, MediaContent mediaContent) throws Exception
    {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("media", createJsonObject(mediaContent.media));
      jsonObject.put("images", createJsonArray(mediaContent.images));
      jsonObject.writeJSONString(writer);
    }

    private static Media readMedia(String mediaJsonInput) throws Exception
    {
      Media media = new Media();
      Object bitrate = JsonPath.read(mediaJsonInput, "$.bitrate");
      if (bitrate != null && bitrate instanceof Long)
      {
        media.bitrate = ((Long) bitrate).intValue();
        media.hasBitrate = true;
      }
      media.copyright = JsonPath.read(mediaJsonInput, "$.copyright");
      media.duration = (Long) JsonPath.read(mediaJsonInput, "$.duration");
      media.format = JsonPath.read(mediaJsonInput, "$.format");
      media.height = ((Long) JsonPath.read(mediaJsonInput, "$.height")).intValue();
      List<String> persons = JsonPath.read(mediaJsonInput, "$.persons[*]");
      media.persons = persons;
      media.player = Media.Player.valueOf((String) JsonPath.read(mediaJsonInput, "$.player"));
      media.size = (Long) JsonPath.read(mediaJsonInput, "$.size");
      media.title = JsonPath.read(mediaJsonInput, "$.title");
      media.uri = JsonPath.read(mediaJsonInput, "$.uri");
      media.width = ((Long) JsonPath.read(mediaJsonInput, "$.width")).intValue();
      return media;
    }

    private static Image readImage(String imageJsonInput) throws Exception
    {
      Image image = new Image();
      image.height = ((Long) JsonPath.read(imageJsonInput, "$.height")).intValue();
      image.size = Image.Size.valueOf((String) JsonPath.read(imageJsonInput, "$.size"));
      image.title = JsonPath.read(imageJsonInput, "$.title");
      image.uri = JsonPath.read(imageJsonInput, "$.uri");
      image.width = ((Long) JsonPath.read(imageJsonInput, "$.width")).intValue();
      return image;
    }

    private static Image readImage(JSONObject jsonObject)
    {
      Image image = new Image();
      image.height = ((Long) JsonPath.read(jsonObject, "$.height")).intValue();
      image.size = Image.Size.valueOf((String) JsonPath.read(jsonObject, "$.size"));
      image.title = JsonPath.read(jsonObject, "$.title");
      image.uri = JsonPath.read(jsonObject, "$.uri");
      image.width = ((Long) JsonPath.read(jsonObject, "$.width")).intValue();
      return image;
    }

    private static List<Image> readImages(String imagesJsonInput) throws Exception
    {
      JSONArray imagesJsonArray = (JSONArray) JsonPath.read(imagesJsonInput, "$[*]");
      return readImages(imagesJsonArray);
    }

    private static List<Image> readImages(JSONArray imagesJsonArray) throws Exception
    {
      int size = imagesJsonArray.size();
      List<Image> images = new ArrayList<Image>(size);
      for (int i = 0; i < size; i++)
      {
        images.add(readImage((JSONObject) imagesJsonArray.get(i)));
      }
      return images;
    }

    private static MediaContent readMediaContent(String mediaContentJsonInput) throws Exception
    {
      MediaContent mediaContent = new MediaContent();
      Media media = new Media();
      Object bitrate = JsonPath.read(mediaContentJsonInput, "$.media.bitrate");
      if (bitrate != null && bitrate instanceof Long)
      {
        media.bitrate = ((Long) bitrate).intValue();
        media.hasBitrate = true;
      }
      media.copyright = JsonPath.read(mediaContentJsonInput, "$.media.copyright");
      media.duration = (Long) JsonPath.read(mediaContentJsonInput, "$.media.duration");
      media.format = JsonPath.read(mediaContentJsonInput, "$.media.format");
      media.height = ((Long) JsonPath.read(mediaContentJsonInput, "$.media.height")).intValue();
      List<String> persons = JsonPath.read(mediaContentJsonInput, "$.media.persons[*]");
      media.persons = persons;
      media.player = Media.Player.valueOf((String) JsonPath.read(mediaContentJsonInput, "$.media.player"));
      media.size = (Long) JsonPath.read(mediaContentJsonInput, "$.media.size");
      media.title = JsonPath.read(mediaContentJsonInput, "$.media.title");
      media.uri = JsonPath.read(mediaContentJsonInput, "$.media.uri");
      media.width = ((Long) JsonPath.read(mediaContentJsonInput, "$.media.width")).intValue();

      JSONArray imagesJsonArray = (JSONArray) JsonPath.read(mediaContentJsonInput, "$.images[*]");
      List<Image> images = readImages(imagesJsonArray);

      mediaContent.media = media;
      mediaContent.images = images;
      return mediaContent;
    }
  }
}
