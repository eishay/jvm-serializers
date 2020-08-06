package serializers.json;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import serializers.*;

import jsonij.json.JSON;
import jsonij.json.JSONMarshaler;
import jsonij.json.JSONParser;
import jsonij.json.Value;
import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

/**
 * Driver that uses JSONiJ [http://projects.plural.cc/projects/jsonij], with manual tree processing.
 */
public class JsonijManualTree
{
  public static void register(TestGroups groups)
  {
    groups.media.add(JavaBuiltIn.mediaTransformer,
        new ManualTreeSerializer("json/jsonij/manual-tree"),
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

    public byte[] serialize(MediaContent mediaContent) throws IOException
    {
      StringWriter writer = new StringWriter();
      writeMediaContent(writer, mediaContent);
      writer.flush();
      return writer.toString().getBytes("UTF-8");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static MediaContent readMediaContent(String mediaContentJsonInput) throws Exception
    {
      JSONParser parser = new JSONParser();
      JSON.Object mediaContentJsonObject = (JSON.Object) parser.parse(mediaContentJsonInput);

      JSON.Object mediaJsonObject = (JSON.Object) mediaContentJsonObject.get("media");
      Media media = readMedia(mediaJsonObject);

      JSON.Array<Value> imageValues = (JSON.Array<Value>) mediaContentJsonObject.get("images");
      List<Image> images = readImages(imageValues);

      MediaContent mediaContent = new MediaContent();
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
    static List<Image> readImages(JSON.Array<Value> imageValues) throws Exception
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

    @SuppressWarnings("rawtypes")
    static Image readImage(String imageJsonInput) throws Exception
    {
      JSONParser parser = new JSONParser();
      JSON.Object imageJsonObject = (JSON.Object) parser.parse(imageJsonInput);
      return readImage(imageJsonObject);
    }

    @SuppressWarnings("rawtypes")
    static Image readImage(JSON.Object imageJsonObject) throws Exception
    {
      Image image = new Image();
      image.height = imageJsonObject.get("height").getInt();
      image.size = Image.Size.valueOf(imageJsonObject.get("size").getString());
      image.title = imageJsonObject.get("title").getString();
      image.uri = imageJsonObject.get("uri").getString();
      image.width = imageJsonObject.get("width").getInt();
      return image;
    }

    @SuppressWarnings({ "rawtypes" })
    static Media readMedia(String mediaJsonInput) throws Exception
    {
      JSONParser parser = new JSONParser();
      JSON.Object mediaJsonObject = (JSON.Object) parser.parse(mediaJsonInput);
      return readMedia(mediaJsonObject);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static Media readMedia(JSON.Object mediaJsonObject)
    {
      Media media = new Media();
      Value bitrate = mediaJsonObject.get("bitrate");
      if (bitrate != null && !bitrate.isNull())
      {
        media.bitrate = bitrate.getInt();
        media.hasBitrate = true;
      }
      Value copyright = mediaJsonObject.get("copyright");
      if (copyright != null && !copyright.isNull())
      {
        media.copyright = copyright.getString();
      }
      media.duration = mediaJsonObject.get("duration").getNumber().longValue();
      media.format = mediaJsonObject.get("format").getString();
      media.height = mediaJsonObject.get("height").getInt();
      JSON.Array<Value> personValues = (JSON.Array<Value>) mediaJsonObject.get("persons");
      int size = personValues.size();
      List<String> persons = new ArrayList<String>(size);
      for (int i = 0; i < size; i++)
      {
        persons.add(personValues.get(i).getString());
      }
      media.persons = persons;
      media.player = Media.Player.valueOf(mediaJsonObject.get("player").getString());
      media.size = mediaJsonObject.get("size").getNumber().longValue();
      media.title = mediaJsonObject.get("title").getString();
      media.uri = mediaJsonObject.get("uri").getString();
      media.width = mediaJsonObject.get("width").getInt();
      return media;
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
