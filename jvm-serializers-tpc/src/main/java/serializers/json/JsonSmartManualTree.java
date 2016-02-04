package serializers.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import serializers.*;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

/**
 * Driver that uses json-smart [http://code.google.com/p/json-smart/], with manual tree parsing.
 */
public class JsonSmartManualTree
{
  public static void register(TestGroups groups)
  {
    groups.media.add(JavaBuiltIn.mediaTransformer,
        new ManualTreeSerializer("json/json-smart/manual-tree"),
            new SerFeatures(
                    SerFormat.JSON,
                    SerGraph.FLAT_TREE,
                    SerClass.MANUAL_OPT,
                    ""
            )
    );
  }

  static class ManualTreeSerializer extends Serializer<MediaContent>
  {
    private final String name;
    private final JSONParser parser;

    public ManualTreeSerializer(String name)
    {
      this.name = name;
      parser = new JSONParser(JSONParser.MODE_RFC4627);
    }

    public String getName()
    {
      return name;
    }

    public MediaContent deserialize(byte[] array) throws Exception
    {
      String mediaContentJsonInput = new String(array, "UTF-8");
      return readMediaContent(parser, mediaContentJsonInput);
    }

    public byte[] serialize(MediaContent mediaContent) throws IOException
    {
      StringWriter writer = new StringWriter();
      writeMediaContent(writer, mediaContent);
      writer.flush();
      return writer.toString().getBytes("UTF-8");
    }

    static Media readMedia(JSONParser parser, String mediaJsonInput) throws Exception
    {
      JSONObject mediaJsonObject = (JSONObject) parser.parse(mediaJsonInput);
      return readMedia(parser, mediaJsonObject);
    }

    static Media readMedia(JSONParser parser, JSONObject mediaJsonObject) throws Exception
    {
      Media media = new Media();
      Object bitrate = mediaJsonObject.get("bitrate");
      if (bitrate != null && bitrate instanceof Integer)
      {
        media.bitrate = ((Integer) bitrate).intValue();
        media.hasBitrate = true;
      }
      media.copyright = (String) mediaJsonObject.get("copyright");
      media.duration = ((Integer) mediaJsonObject.get("duration")).longValue();
      media.format = (String) mediaJsonObject.get("format");
      media.height = ((Integer) mediaJsonObject.get("height")).intValue();
      List<String> persons = new ArrayList<String>();
      JSONArray personsJsonArray = (JSONArray) mediaJsonObject.get("persons");
      for (int i = 0, size = personsJsonArray.size(); i < size; i++)
      {
        persons.add((String) personsJsonArray.get(i));
      }
      media.persons = persons;
      media.player = Media.Player.valueOf((String) mediaJsonObject.get("player"));
      media.size = ((Integer) mediaJsonObject.get("size")).longValue();
      media.title = (String) mediaJsonObject.get("title");
      media.uri = (String) mediaJsonObject.get("uri");
      media.width = ((Integer) mediaJsonObject.get("width")).intValue();
      return media;
    }
    
    static MediaContent readMediaContent(JSONParser parser, String mediaContentJsonInput) throws Exception
    {
      JSONObject mediaContentJsonObject = (JSONObject) parser.parse(mediaContentJsonInput);
      MediaContent mediaContent = new MediaContent();
      mediaContent.images = readImages(parser, (JSONArray) mediaContentJsonObject.get("images"));
      mediaContent.media = readMedia(parser, (JSONObject) mediaContentJsonObject.get("media"));
      return mediaContent;
    }
    
    static Image readImage(JSONParser parser, String imageJsonInput) throws Exception
    {
      JSONObject imageJsonObject = (JSONObject) parser.parse(imageJsonInput);
      return readImage(parser, imageJsonObject);
    }
    
    static Image readImage(JSONParser parser, JSONObject imageJsonObject) throws Exception
    {
      Image image = new Image();
      image.height = ((Integer) imageJsonObject.get("height")).intValue();
      image.size = Image.Size.valueOf((String) imageJsonObject.get("size"));
      image.title = (String) imageJsonObject.get("title");
      image.uri = (String) imageJsonObject.get("uri");
      image.width = ((Integer) imageJsonObject.get("width")).intValue();
      return image;
    }
    
    static List<Image> readImages(JSONParser parser, String imagesJsonInput) throws Exception
    {
      JSONArray imagesJsonArray = (JSONArray) parser.parse(imagesJsonInput);
      return readImages(parser, imagesJsonArray);
    }
    
    static List<Image> readImages(JSONParser parser, JSONArray imagesJsonArray) throws Exception
    {
      int size = imagesJsonArray.size();
      List<Image> images = new ArrayList<Image> (size);
      for (int i = 0; i < size; i++)
      {
        images.add(readImage(parser, (JSONObject) imagesJsonArray.get(i)));
      }
      return images;
    }

    static JSONObject createJsonObject(Media media)
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
    
    static void writeMedia(Writer writer, Media media) throws Exception
    {
      JSONObject jsonObject = createJsonObject(media);
      jsonObject.writeJSONString(writer);
    }
    
    static void writeImage(Writer writer, Image image) throws Exception
    {
      JSONObject jsonObject = createJsonObject(image);
      jsonObject.writeJSONString(writer);
    }

    static JSONObject createJsonObject(Image image)
    {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("height", image.height);
      jsonObject.put("size", image.size.name());
      jsonObject.put("title", image.title);
      jsonObject.put("uri", image.uri);
      jsonObject.put("width", image.width);
      return jsonObject;
    }
    
    static void writeMediaContent(Writer writer, MediaContent mediaContent) throws IOException
    {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("media", createJsonObject(mediaContent.media));
      jsonObject.put("images", createJsonArray(mediaContent.images));
      jsonObject.writeJSONString(writer);
    }
    
    static JSONArray createJsonArray(List<Image> images)
    {
      JSONArray jsonArray = new JSONArray();
      for (Image image : images)
      {
        jsonArray.add(createJsonObject(image));
      }
      return jsonArray;
    }
    
    static void writeImages(Writer writer, List<Image> images) throws Exception
    {
      JSONArray jsonArray = createJsonArray(images);
      jsonArray.writeJSONString(writer);
    }
  }
}
