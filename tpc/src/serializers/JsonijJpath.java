package serializers;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import jsonij.json.JPath;
import jsonij.json.JSON;
import jsonij.json.JSONMarshaler;
import jsonij.json.JSONParser;
import jsonij.json.Value;
import jsonij.json.JSON.Array;
import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

/**
 * Driver that uses JSONiJ [http://projects.plural.cc/projects/jsonij], with JPath parsing.
 */
public class JsonijJpath
{
  public static void register(TestGroups groups)
  {
    groups.media.add(JavaBuiltIn.MediaTransformer,
        new SemiManualSerializer("json/jsonij-jpath"));
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

    public byte[] serialize(MediaContent mediaContent) throws IOException
    {
      StringWriter writer = new StringWriter();
      writeMediaContent(writer, mediaContent);
      writer.flush();
      return writer.toString().getBytes("UTF-8");
    }

    @SuppressWarnings({ "unchecked" })
    static MediaContent readMediaContent(String mediaContentJsonInput) throws Exception
    {
      MediaContent mediaContent = new MediaContent();
      JSON json = JSON.parse(mediaContentJsonInput);

      Media media = new Media();
      Value bitrate = JPath.parse("/media/bitrate").evaluate(json);
      if (bitrate != null && !bitrate.isNull())
      {
        media.bitrate = bitrate.getInt();
        media.hasBitrate = true;
      }
      Value copyright = JPath.parse("/media/copyright").evaluate(json);
      if (copyright != null && !copyright.isNull())
      {
        media.copyright = copyright.getString();
      }
      media.duration = JPath.parse("/media/duration").evaluate(json).getNumber().longValue();
      media.format = JPath.parse("/media/format").evaluate(json).getString();
      media.height = JPath.parse("/media/height").evaluate(json).getInt();
      JSON.Array<Value> personValues = (JSON.Array<Value>) JPath.parse("/media/persons").evaluate(json);
      int size = personValues.size();
      List<String> persons = new ArrayList<String>(size);
      for (int i = 0; i < size; i++)
      {
        persons.add(personValues.get(i).getString());
      }
      media.persons = persons;
      media.player = Media.Player.valueOf(JPath.parse("/media/player").evaluate(json).getString());
      media.size = JPath.parse("/media/size").evaluate(json).getNumber().longValue();
      media.title = JPath.parse("/media/title").evaluate(json).getString();
      media.uri = JPath.parse("/media/uri").evaluate(json).getString();
      media.width = JPath.parse("/media/width").evaluate(json).getInt();

      JSON.Array<Value> imageValues = (JSON.Array<Value>) JPath.parse("/images").evaluate(json);
      List<Image> images = readImages(imageValues);

      mediaContent.media = media;
      mediaContent.images = images;
      return mediaContent;
    }

    @SuppressWarnings({ "unchecked" })
    static List<Image> readImages(String imagesJsonInput) throws Exception
    {
      JSONParser parser = new JSONParser();
      JSON.Array<Value> imageValues = (JSON.Array<Value>) parser.parse(imagesJsonInput);
      return readImages(imageValues);
    }

    @SuppressWarnings("rawtypes")
    static List<Image> readImages(Array<Value> imageValues) throws Exception
    {
      int size = imageValues.size();
      List<Image> images = new ArrayList<Image>(size);
      for (int i = 0; i < size; i++)
      {
        JSON.Object imageJsonObject = (JSON.Object) imageValues.get(i);
        Image image = readImage(imageJsonObject);
        images.add(image);
      }
      return images;
    }

    @SuppressWarnings("unchecked")
    static Media readMedia(String mediaJsonInput) throws Exception
    {
      JSON json = JSON.parse(mediaJsonInput);

      Media media = new Media();
      Value bitrate = JPath.parse("/bitrate").evaluate(json);
      if (bitrate != null && !bitrate.isNull())
      {
        media.bitrate = bitrate.getInt();
        media.hasBitrate = true;
      }
      Value copyright = JPath.parse("/copyright").evaluate(json);
      if (copyright != null && !copyright.isNull())
      {
        media.copyright = copyright.getString();
      }
      media.duration = JPath.parse("/duration").evaluate(json).getNumber().longValue();
      media.format = JPath.parse("/format").evaluate(json).getString();
      media.height = JPath.parse("/height").evaluate(json).getInt();
      JSON.Array<Value> personValues = (JSON.Array<Value>) JPath.parse("/persons").evaluate(json);
      int size = personValues.size();
      List<String> persons = new ArrayList<String>(size);
      for (int i = 0; i < size; i++)
      {
        persons.add(personValues.get(i).getString());
      }
      media.persons = persons;
      media.player = Media.Player.valueOf(JPath.parse("/player").evaluate(json).getString());
      media.size = JPath.parse("/size").evaluate(json).getNumber().longValue();
      media.title = JPath.parse("/title").evaluate(json).getString();
      media.uri = JPath.parse("/uri").evaluate(json).getString();
      media.width = JPath.parse("/width").evaluate(json).getInt();
      return media;
    }

    static Image readImage(String imageJsonInput) throws Exception
    {
      Image image = new Image();
      JSON json = JSON.parse(imageJsonInput);
      image.height = JPath.parse("/height").evaluate(json).getInt();
      image.size = Image.Size.valueOf(JPath.parse("/size").evaluate(json).getString());
      image.title = JPath.parse("/title").evaluate(json).getString();
      image.uri = JPath.parse("/uri").evaluate(json).getString();
      image.width = JPath.parse("/width").evaluate(json).getInt();
      return image;
    }

    @SuppressWarnings("rawtypes")
    static Image readImage(JSON.Object imageJsonObject) throws Exception
    {
      Image image = new Image();
      image.height = JPath.parse("/height").evaluate(imageJsonObject).getInt();
      image.size = Image.Size.valueOf(JPath.parse("/size").evaluate(imageJsonObject).getString());
      image.title = JPath.parse("/title").evaluate(imageJsonObject).getString();
      image.uri = JPath.parse("/uri").evaluate(imageJsonObject).getString();
      image.width = JPath.parse("/width").evaluate(imageJsonObject).getInt();
      return image;
    }

    static void writeMedia(StringWriter writer, Media media)
    {
      JSON json = JSONMarshaler.marshalObject(media);
      writer.write(json.toJSON());
    }

    static void writeImage(StringWriter writer, Image image)
    {
      JSON json = JSONMarshaler.marshalObject(image);
      writer.write(json.toJSON());
    }

    static void writeImages(StringWriter writer, List<Image> images)
    {
      JSON json = JSONMarshaler.marshalObject(images);
      writer.write(json.toJSON());
    }

    static void writeMediaContent(StringWriter writer, MediaContent mediaContent)
    {
      JSON json = JSONMarshaler.marshalObject(mediaContent);
      writer.write(json.toJSON());
    }
  }
}
