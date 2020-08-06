package serializers.json;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import serializers.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

/**
 * Driver that uses Gson for manual tree processing.
 */
public class JsonGsonTree
{
  public static void register(TestGroups groups)
  {
    groups.media.add(JavaBuiltIn.mediaTransformer,
        new SemiManualSerializer("json/gson/manual-tree"),
            new SerFeatures(
                    SerFormat.JSON,
                    SerGraph.FLAT_TREE,
                    SerClass.MANUAL_OPT,
                    ""
            )
    );
  }

  static class SemiManualSerializer extends Serializer<MediaContent>
  {
    private final String name;
    private final JsonParser parser = new JsonParser();

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
      return readMediaContent(parser, mediaContentJsonInput);
    }

    public byte[] serialize(MediaContent mediaContent) throws Exception
    {
      StringWriter writer = new StringWriter();
      writeMediaContent(writer, mediaContent);
      writer.flush();
      return writer.toString().getBytes("UTF-8");
    }

    private static Image readImage(JsonParser parser, String imageJsonInput)
    {
      JsonObject imageJsonObject = parser.parse(imageJsonInput).getAsJsonObject();
      return readImage(imageJsonObject);
    }

    private static Image readImage(JsonObject imageJsonObject)
    {
      Image image = new Image();
      image.height = imageJsonObject.get("height").getAsInt();
      image.size = Image.Size.valueOf(imageJsonObject.get("size").getAsString());
      image.title = imageJsonObject.get("title").getAsString();
      image.uri = imageJsonObject.get("uri").getAsString();
      image.width = imageJsonObject.get("width").getAsInt();
      return image;
    }

    private static MediaContent readMediaContent(JsonParser parser, String mediaContentJsonInput) throws Exception
    {
      JsonObject mediaContentJsonObject = parser.parse(mediaContentJsonInput).getAsJsonObject();
      MediaContent mediaContent = new MediaContent();
      mediaContent.images = readImages(mediaContentJsonObject.get("images").getAsJsonArray());
      mediaContent.media = readMedia(mediaContentJsonObject.get("media").getAsJsonObject());
      return mediaContent;
    }

    private static Media readMedia(JsonParser parser, String mediaJsonInput)
    {
      JsonObject mediaJsonObject = parser.parse(mediaJsonInput).getAsJsonObject();
      return readMedia(mediaJsonObject);
    }

    private static Media readMedia(JsonObject mediaJsonObject)
    {
      Media media = new Media();
      JsonElement bitrate = mediaJsonObject.get("bitrate");
      if (bitrate != null && !bitrate.isJsonNull())
      {
        media.bitrate = bitrate.getAsInt();
        media.hasBitrate = true;
      }
      JsonElement copyright = mediaJsonObject.get("copyright");
      if (copyright != null && !copyright.isJsonNull())
      {
        media.copyright = copyright.getAsString();
      }
      media.duration = mediaJsonObject.get("duration").getAsLong();
      media.format = mediaJsonObject.get("format").getAsString();
      media.height = mediaJsonObject.get("height").getAsInt();
      media.player = Media.Player.valueOf(mediaJsonObject.get("player").getAsString());
      JsonArray personsJsonArray = mediaJsonObject.get("persons").getAsJsonArray();
      int size = personsJsonArray.size();
      List<String> persons = new ArrayList<String>(size);
      for (JsonElement person : personsJsonArray)
      {
        persons.add(person.getAsString());
      }
      media.persons = persons;
      media.size = mediaJsonObject.get("size").getAsInt();
      media.title = mediaJsonObject.get("title").getAsString();
      media.uri = mediaJsonObject.get("uri").getAsString();
      media.width = mediaJsonObject.get("width").getAsInt();
      return media;
    }

    private static List<Image> readImages(JsonParser parser, String imagesJsonInput) throws Exception
    {
      JsonArray imagesJsonArray = parser.parse(imagesJsonInput).getAsJsonArray();
      return readImages(imagesJsonArray);
    }

    private static List<Image> readImages(JsonArray imagesJsonArray)
    {
      int size = imagesJsonArray.size();
      List<Image> images = new ArrayList<Image>(size);
      for (JsonElement image : imagesJsonArray)
      {
        images.add(readImage(image.getAsJsonObject()));
      }
      return images;
    }

    private static JsonObject createJsonObject(Image image)
    {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("height", image.height);
      jsonObject.addProperty("size", image.size.name());
      jsonObject.addProperty("title", image.title);
      jsonObject.addProperty("uri", image.uri);
      jsonObject.addProperty("width", image.width);
      return jsonObject;
    }

    private static JsonObject createJsonObject(Media media)
    {
      JsonObject jsonObject = new JsonObject();
      if (media.hasBitrate)
      {
        jsonObject.addProperty("bitrate", media.bitrate);
      }
      jsonObject.addProperty("copyright", media.copyright);
      jsonObject.addProperty("duration", media.duration);
      jsonObject.addProperty("format", media.format);
      jsonObject.addProperty("height", media.height);
      int size = media.persons.size();
      JsonArray personsJsonArray = new JsonArray();
      for (int i = 0; i < size; i++)
      {
        personsJsonArray.add(new JsonPrimitive(media.persons.get(i)));
      }
      jsonObject.add("persons", personsJsonArray);
      jsonObject.addProperty("player", media.player.name());
      jsonObject.addProperty("size", media.size);
      jsonObject.addProperty("title", media.title);
      jsonObject.addProperty("uri", media.uri);
      jsonObject.addProperty("width", media.width);
      return jsonObject;
    }

    private static void writeMedia(StringWriter writer, Media media)
    {
      JsonObject jsonObject = createJsonObject(media);
      writer.write(jsonObject.toString());
    }

    private static void writeMediaContent(Writer writer, MediaContent mediaContent) throws Exception
    {
      JsonObject jsonObject = new JsonObject();
      jsonObject.add("media", createJsonObject(mediaContent.media));
      jsonObject.add("images", createJsonArray(mediaContent.images));
      writer.write(jsonObject.toString());
    }

    private static void writeImage(Writer writer, Image image) throws Exception
    {
      JsonObject jsonObject = createJsonObject(image);
      writer.write(jsonObject.toString());
    }

    private static JsonArray createJsonArray(List<Image> images)
    {
      JsonArray jsonArray = new JsonArray();
      for (Image image : images)
      {
        jsonArray.add(createJsonObject(image));
      }
      return jsonArray;
    }

    private static void writeImages(Writer writer, List<Image> images) throws Exception
    {
      JsonArray jsonArray = createJsonArray(images);
      writer.write(jsonArray.toString());
    }
  }
}
