package serializers.json;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;

import serializers.*;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

/**
 * Driver that uses the json.org reference JSON implementation in Java, with semi-manual parsing.
 */
public class JsonDotOrgManualTree
{
  public static void register(TestGroups groups)
  {
    groups.media.add(JavaBuiltIn.mediaTransformer,
        new ManualTreeSerializer("json/org.json/manual-tree"),
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

    public ManualTreeSerializer(String name)
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
      writeMediaContent(new JSONWriter(writer), mediaContent);
      writer.flush();
      return writer.toString().getBytes("UTF-8");
    }

    static Media readMedia(String mediaJsonInput) throws Exception
    {
      JSONObject mediaJsonObject = new JSONObject(mediaJsonInput);
      return readMedia(mediaJsonObject);
    }

    static Media readMedia(JSONObject mediaJsonObject) throws Exception
    {
      Media media = new Media();
      Object bitrate = mediaJsonObject.get("bitrate");
      if (bitrate != null && bitrate instanceof Integer)
      {
        media.bitrate = ((Integer) bitrate).intValue();
        media.hasBitrate = true;
      }
      Object copyright = mediaJsonObject.get("copyright");
      if (copyright != null && !JSONObject.NULL.equals(copyright))
      {
        media.copyright = (String) mediaJsonObject.get("copyright");
      }
      media.duration = ((Integer) mediaJsonObject.get("duration")).longValue();
      media.format = (String) mediaJsonObject.get("format");
      media.height = ((Integer) mediaJsonObject.get("height")).intValue();
      List<String> persons = new ArrayList<String>();
      JSONArray personsJsonArray = (JSONArray) mediaJsonObject.get("persons");
      for (int i = 0, size = personsJsonArray.length(); i < size; i++)
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

    static MediaContent readMediaContent(String mediaContentJsonInput) throws Exception
    {
      JSONObject mediaContentJsonObject = new JSONObject(mediaContentJsonInput);
      MediaContent mediaContent = new MediaContent();
      mediaContent.images = readImages((JSONArray) mediaContentJsonObject.get("images"));
      mediaContent.media = readMedia((JSONObject) mediaContentJsonObject.get("media"));
      return mediaContent;
    }

    static Image readImage(String imageJsonInput) throws Exception
    {
      JSONObject imageJsonObject = new JSONObject(imageJsonInput);
      return readImage(imageJsonObject);
    }

    static Image readImage(JSONObject imageJsonObject) throws Exception
    {
      Image image = new Image();
      image.height = ((Integer) imageJsonObject.get("height")).intValue();
      image.size = Image.Size.valueOf((String) imageJsonObject.get("size"));
      image.title = (String) imageJsonObject.get("title");
      image.uri = (String) imageJsonObject.get("uri");
      image.width = ((Integer) imageJsonObject.get("width")).intValue();
      return image;
    }

    static List<Image> readImages(String imagesJsonInput) throws Exception
    {
      JSONArray imagesJsonArray = new JSONArray(imagesJsonInput);
      return readImages(imagesJsonArray);
    }

    static List<Image> readImages(JSONArray imagesJsonArray) throws Exception
    {
      int size = imagesJsonArray.length();
      List<Image> images = new ArrayList<Image>(size);
      for (int i = 0; i < size; i++)
      {
        images.add(readImage((JSONObject) imagesJsonArray.get(i)));
      }
      return images;
    }

    static void writeJsonObject(JSONWriter jsonWriter, Media media) throws Exception
    {
      jsonWriter.object();
      if (media.hasBitrate)
      {
        jsonWriter.key("bitrate").value(media.bitrate);
      }
      jsonWriter.key("copyright").value(media.copyright);
      jsonWriter.key("duration").value(media.duration);
      jsonWriter.key("format").value(media.format);
      jsonWriter.key("height").value(media.height);
      JSONArray personsJsonArray = new JSONArray();
      for (int i = 0, size = media.persons.size(); i < size; i++)
      {
        personsJsonArray.put(media.persons.get(i));
      }
      jsonWriter.key("persons").value(personsJsonArray);
      jsonWriter.key("player").value(media.player.name());
      jsonWriter.key("size").value(media.size);
      jsonWriter.key("title").value(media.title);
      jsonWriter.key("uri").value(media.uri);
      jsonWriter.key("width").value(media.width);
      jsonWriter.endObject();
    }

    static void writeMedia(JSONWriter jsonWriter, Media media) throws Exception
    {
      writeJsonObject(jsonWriter, media);
    }

    static void writeImage(JSONWriter jsonWriter, Image image) throws Exception
    {
      writeJsonObject(jsonWriter, image);
    }

    static void writeJsonObject(JSONWriter jsonWriter, Image image) throws Exception
    {
      jsonWriter.object();
      jsonWriter.key("height").value(image.height);
      jsonWriter.key("size").value(image.size.name());
      jsonWriter.key("title").value(image.title);
      jsonWriter.key("uri").value(image.uri);
      jsonWriter.key("width").value(image.width);
      jsonWriter.endObject();
    }

    static void writeMediaContent(JSONWriter jsonWriter, MediaContent mediaContent) throws Exception
    {
      jsonWriter.object();
      jsonWriter.key("media");
      writeJsonObject(jsonWriter, mediaContent.media);
      jsonWriter.key("images");
      writeJsonArray(jsonWriter, mediaContent.images);
      jsonWriter.endObject();
    }

    static void writeJsonArray(JSONWriter jsonWriter, List<Image> images) throws Exception
    {
      jsonWriter.array();
      for (Image image : images)
      {
        writeJsonObject(jsonWriter, image);
      }
      jsonWriter.endArray();
    }

    static void writeImages(JSONWriter jsonWriter, List<Image> images) throws Exception
    {
      writeJsonArray(jsonWriter, images);
    }
  }
}
