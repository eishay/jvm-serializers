package serializers;

import static argo.jdom.JsonNodeBuilders.aNullBuilder;
import static argo.jdom.JsonNodeBuilders.aNumberBuilder;
import static argo.jdom.JsonNodeBuilders.aStringBuilder;
import static argo.jdom.JsonNodeBuilders.anArrayBuilder;
import static argo.jdom.JsonNodeBuilders.anObjectBuilder;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import argo.format.CompactJsonFormatter;
import argo.format.JsonFormatter;
import argo.jdom.JdomParser;
import argo.jdom.JsonArrayNodeBuilder;
import argo.jdom.JsonNode;
import argo.jdom.JsonObjectNodeBuilder;
import argo.jdom.JsonRootNode;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

/**
 * Driver that uses Argo [http://argo.sourceforge.net], with manual tree processing.
 */
public class ArgoManualTree
{
  public static void register(TestGroups groups)
  {
    groups.media.add(JavaBuiltIn.MediaTransformer,
        new ManualTreeSerializer("json/argo-manual/tree"));
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

    private static final JsonFormatter JSON_FORMATTER = new CompactJsonFormatter();
    private static final JdomParser JDOM_PARSER = new JdomParser();

    private static Image readImage(JsonNode node)
    {
      Image image = new Image();
      image.height = Integer.parseInt(node.getNumberValue("height"));
      image.size = Image.Size.valueOf(node.getStringValue("size"));
      image.title = node.getNullableStringValue("title");
      image.uri = node.getNullableStringValue("uri");
      image.width = Integer.parseInt(node.getNumberValue("width"));
      return image;
    }

    private static Image readImage(String imageJsonInput) throws Exception
    {
      JsonRootNode root = JDOM_PARSER.parse(imageJsonInput);
      return readImage(root);
    }

    private static List<Image> readImages(String imagesJsonInput) throws Exception
    {
      JsonRootNode root = JDOM_PARSER.parse(imagesJsonInput);
      return readImages(root);
    }

    private static List<Image> readImages(JsonNode node)
    {
      List<JsonNode> nodes = node.getElements();
      int size = nodes.size();
      List<Image> images = new ArrayList<Image>(size);
      for (int i = 0; i < size; i++)
      {
        images.add(readImage(nodes.get(i)));
      }
      return images;
    }

    private static MediaContent readMediaContent(String mediaContentJsonInput) throws Exception
    {
      JsonRootNode root = JDOM_PARSER.parse(mediaContentJsonInput);
      MediaContent mediaContent = new MediaContent();
      mediaContent.media = readMedia(root.getNode("media"));
      mediaContent.images = readImages(root.getNode("images"));
      return mediaContent;
    }

    private static Media readMedia(JsonNode node)
    {
      Media media = new Media();
      String bitrate = node.getNullableNumberValue("bitrate");
      if (bitrate != null && bitrate.length() > 0)
      {
        media.bitrate = Integer.parseInt(bitrate);
        media.hasBitrate = true;
      }
      media.copyright = node.getNullableStringValue("copyright");
      media.duration = Long.parseLong(node.getNumberValue("duration"));
      media.format = node.getNullableStringValue("format");
      media.height = Integer.parseInt(node.getNumberValue("height"));
      List<JsonNode> personJsonNodes = node.getArrayNode("persons");
      int size = personJsonNodes.size();
      List<String> persons = new ArrayList<String>(size);
      for (int i = 0; i < size; i++)
      {
        persons.add(personJsonNodes.get(i).getText());
      }
      media.persons = persons;
      media.player = Media.Player.valueOf(node.getStringValue("player"));
      media.size = Long.parseLong(node.getNumberValue("size"));
      media.title = node.getNullableStringValue("title");
      media.uri = node.getNullableStringValue("uri");
      media.width = Integer.parseInt(node.getNumberValue("width"));
      return media;
    }

    private static Media readMedia(String mediaJsonInput) throws Exception
    {
      JsonRootNode root = JDOM_PARSER.parse(mediaJsonInput);
      return readMedia(root);
    }

    private static void writeImages(StringWriter imagesWriter, List<Image> images)
    {
      JsonArrayNodeBuilder arrayBuilder = createImagesArrayBuilder(images);
      JsonRootNode json = arrayBuilder.build();
      imagesWriter.write(JSON_FORMATTER.format(json));
    }

    private static JsonArrayNodeBuilder createImagesArrayBuilder(List<Image> images)
    {
      JsonArrayNodeBuilder arrayBuilder = anArrayBuilder();
      for (Image image : images)
      {
        arrayBuilder.withElement(createImageObjectBuilder(image));
      }
      return arrayBuilder;
    }
    
    private static void writeMediaContent(StringWriter mediaContentWriter, MediaContent mediaContent)
    {
      JsonObjectNodeBuilder builder = anObjectBuilder()
          .withField("media", createMediaObjectBuilder(mediaContent.media))
          .withField("images", createImagesArrayBuilder(mediaContent.images));
      JsonRootNode json = builder.build();
      mediaContentWriter.write(JSON_FORMATTER.format(json));
    }

    private static void writeMedia(StringWriter mediaWriter, Media media)
    {
      JsonObjectNodeBuilder builder = createMediaObjectBuilder(media);
      JsonRootNode json = builder.build();
      mediaWriter.write(JSON_FORMATTER.format(json));
    }
    
    private static JsonObjectNodeBuilder createMediaObjectBuilder(Media media)
    {
      JsonObjectNodeBuilder builder = anObjectBuilder()
          .withField("uri", aStringBuilder(media.uri))
          .withField("title", aStringBuilder(media.title))
          .withField("width", aNumberBuilder(String.valueOf(media.width)))
          .withField("height", aNumberBuilder(String.valueOf(media.height)))
          .withField("format", aStringBuilder(media.format))
          .withField("duration", aNumberBuilder(String.valueOf(media.duration)))
          .withField("size", aNumberBuilder(String.valueOf(media.size)));
      if (media.hasBitrate)
      {
        builder.withField("bitrate", aNumberBuilder(String.valueOf(media.bitrate)));
      }
      builder.withField("player", aStringBuilder(media.player.name()));
      if (media.copyright != null)
      {
        builder.withField("copyright", aStringBuilder(media.copyright));
      }
      else
      {
        builder.withField("copyright", aNullBuilder());
      }
      JsonArrayNodeBuilder arrayBuilder = anArrayBuilder();
      for (String person : media.persons)
      {
        arrayBuilder.withElement(aStringBuilder(person));
      }
      builder.withField("persons", arrayBuilder);
      return builder;
    }

    private static JsonObjectNodeBuilder createImageObjectBuilder(Image image)
    {
      return anObjectBuilder()
          .withField("height", aNumberBuilder(String.valueOf(image.height)))
          .withField("size", aStringBuilder(image.size.name()))
          .withField("title", aStringBuilder(image.title))
          .withField("uri", aStringBuilder(image.uri))
          .withField("width", aNumberBuilder(String.valueOf(image.width)));
    }

    private static void writeImage(StringWriter imageWriter, Image image)
    {
      JsonObjectNodeBuilder builder = createImageObjectBuilder(image);
      JsonRootNode json = builder.build();
      imageWriter.write(JSON_FORMATTER.format(json));
    }
  }
}
