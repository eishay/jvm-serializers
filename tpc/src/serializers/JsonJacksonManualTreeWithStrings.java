package serializers;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

/**
 * Driver that uses Jackson for manual tree processing (to/from byte[]).
 */
public class JsonJacksonManualTreeWithStrings
{
  public static void register(TestGroups groups)
  {
    groups.media.add(JavaBuiltIn.MediaTransformer,
        new ManualTreeSerializer("json/jackson-manual/tree-strings"));
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
      JsonParser parser = jsonFactory.createJsonParser(mediaContentJsonInput);
      return readMediaContent(parser);
    }

    public byte[] serialize(MediaContent mediaContent) throws Exception
    {
      StringWriter writer = new StringWriter();
      JsonGenerator generator = jsonFactory.createJsonGenerator(writer);
      writeMediaContent(generator, mediaContent);
      writer.flush();
      generator.close();
      return writer.toString().getBytes("UTF-8");
    }

    private static JsonFactory jsonFactory = new JsonFactory(new ObjectMapper());
    private static JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

    private static Image readImage(JsonParser parser) throws Exception
    {
      JsonNode root = parser.readValueAsTree();
      return readImage(root);
    }

    private static Media readMedia(JsonParser parser) throws Exception
    {
      JsonNode root = parser.readValueAsTree();
      return readMedia(root);
    }

    private static List<Image> readImages(JsonParser parser) throws Exception
    {
      ArrayNode root = (ArrayNode) parser.readValueAsTree();
      return readImages(root);
    }

    private static Image readImage(JsonNode node)
    {
      Image image = new Image();
      image.height = node.get("height").getIntValue();
      image.size = Image.Size.valueOf(node.get("size").getTextValue());
      image.title = node.get("title").getTextValue();
      image.uri = node.get("uri").getTextValue();
      image.width = node.get("width").getIntValue();
      return image;
    }

    private static List<Image> readImages(ArrayNode imagesNode)
    {
      int size = imagesNode.size();
      List<Image> images = new ArrayList<Image>(size);
      for (JsonNode image : imagesNode)
      {
        images.add(readImage(image));
      }
      return images;
    }

    private static MediaContent readMediaContent(JsonParser parser) throws Exception
    {
      JsonNode root = parser.readValueAsTree();
      MediaContent mediaContent = new MediaContent();
      mediaContent.media = readMedia(root.get("media"));
      mediaContent.images = readImages((ArrayNode) root.get("images"));
      return mediaContent;
    }

    private static Media readMedia(JsonNode node)
    {
      Media media = new Media();
      JsonNode bitrate = node.get("bitrate");
      if (bitrate != null && !bitrate.isNull())
      {
        media.bitrate = bitrate.getIntValue();
        media.hasBitrate = true;
      }
      JsonNode copyright = node.get("copyright");
      if (copyright != null && !copyright.isNull())
      {
        media.copyright = copyright.getTextValue();
      }
      media.duration = node.get("duration").getLongValue();
      media.format = node.get("format").getTextValue();
      media.height = node.get("height").getIntValue();
      media.player = Media.Player.valueOf(node.get("player").getTextValue());
      ArrayNode personsArrayNode = (ArrayNode) node.get("persons");
      int size = personsArrayNode.size();
      List<String> persons = new ArrayList<String>(size);
      for (JsonNode person : personsArrayNode)
      {
        persons.add(person.getTextValue());
      }
      media.persons = persons;
      media.size = node.get("size").getIntValue();
      media.title = node.get("title").getTextValue();
      media.uri = node.get("uri").getTextValue();
      media.width = node.get("width").getIntValue();
      return media;
    }

    private static ObjectNode createObjectNode(Image image)
    {
      ObjectNode node = nodeFactory.objectNode();
      node.put("height", image.height);
      node.put("size", image.size.name());
      node.put("title", image.title);
      node.put("uri", image.uri);
      node.put("width", image.width);
      return node;
    }

    private static ArrayNode createArrayNode(List<Image> images)
    {
      ArrayNode node = nodeFactory.arrayNode();
      for (Image image : images)
      {
        node.add(createObjectNode(image));
      }
      return node;
    }

    private static ObjectNode createObjectNode(Media media)
    {
      ObjectNode mediaNode = nodeFactory.objectNode();
      if (media.hasBitrate)
      {
        mediaNode.put("bitrate", media.bitrate);
      }
      mediaNode.put("copyright", media.copyright);
      mediaNode.put("duration", media.duration);
      mediaNode.put("format", media.format);
      mediaNode.put("height", media.height);
      ArrayNode persons = nodeFactory.arrayNode();
      for (String person : media.persons)
      {
        persons.add(person);
      }
      mediaNode.put("persons", persons);
      mediaNode.put("player", media.player.name());
      mediaNode.put("size", media.size);
      mediaNode.put("title", media.title);
      mediaNode.put("uri", media.uri);
      mediaNode.put("width", media.width);
      return mediaNode;
    }

    private static void writeMediaContent(JsonGenerator generator, MediaContent mediaContent) throws Exception
    {
      ObjectNode node = nodeFactory.objectNode();
      node.put("media", createObjectNode(mediaContent.media));
      node.put("images", createArrayNode(mediaContent.images));
      generator.writeTree(node);
    }

    private static void writeMedia(JsonGenerator generator, Media media) throws Exception
    {
      ObjectNode node = createObjectNode(media);
      generator.writeTree(node);
    }

    private static void writeImage(JsonGenerator generator, Image image) throws Exception
    {
      ObjectNode node = createObjectNode(image);
      generator.writeTree(node);
    }

    private static void writeImages(JsonGenerator generator, List<Image> images) throws Exception
    {
      ArrayNode node = createArrayNode(images);
      generator.writeTree(node);
    }
  }
}
