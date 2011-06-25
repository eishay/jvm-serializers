package serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

/**
 * Driver that uses Jackson for manual tree processing (to/from byte[]).
 */
public class JsonJacksonManualTree
{
  public static void register(TestGroups groups)
  {
    groups.media.add(JavaBuiltIn.MediaTransformer,
        new ManualTreeSerializer("json/jackson-manual/tree"));
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
      return readMediaContent(new ByteArrayInputStream(array));
    }

    public byte[] serialize(MediaContent mediaContent) throws Exception
    {
      ByteArrayOutputStream baos = outputStream(mediaContent);
      JsonGenerator generator = mapper.getJsonFactory().createJsonGenerator(baos);
      writeMediaContent(generator, mediaContent);
      generator.close();
      return baos.toByteArray();
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    private static Image readImage(InputStream json) throws Exception
    {
      JsonNode root = mapper.readTree(json);
      return readImage(root);
    }

    private static Media readMedia(InputStream json) throws Exception
    {
      JsonNode root = mapper.readTree(json);
      return readMedia(root);
    }

    private static List<Image> readImages(InputStream json) throws Exception
    {
      ArrayNode root = (ArrayNode) mapper.readTree(json);
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

    private static MediaContent readMediaContent(InputStream json) throws Exception
    {
      JsonNode root = mapper.readTree(json);
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
      ObjectNode node = mapper.createObjectNode();
      node.put("height", image.height);
      node.put("size", image.size.name());
      node.put("title", image.title);
      node.put("uri", image.uri);
      node.put("width", image.width);
      return node;
    }

    private static ArrayNode createArrayNode(List<Image> images)
    {
      ArrayNode node = mapper.createArrayNode();
      for (Image image : images)
      {
        node.add(createObjectNode(image));
      }
      return node;
    }

    private static ObjectNode createObjectNode(Media media)
    {
      ObjectNode node = mapper.createObjectNode();
      if (media.hasBitrate)
      {
        node.put("bitrate", media.bitrate);
      }
      node.put("copyright", media.copyright);
      node.put("duration", media.duration);
      node.put("format", media.format);
      node.put("height", media.height);
      ArrayNode persons = mapper.createArrayNode();
      for (String person : media.persons)
      {
        persons.add(person);
      }
      node.put("persons", persons);
      node.put("player", media.player.name());
      node.put("size", media.size);
      node.put("title", media.title);
      node.put("uri", media.uri);
      node.put("width", media.width);
      return node;
    }

    private static void writeMediaContent(JsonGenerator generator, MediaContent mediaContent) throws Exception
    {
      ObjectNode node = mapper.createObjectNode();
      node.put("media", createObjectNode(mediaContent.media));
      node.put("images", createArrayNode(mediaContent.images));
      mapper.writeTree(generator, node);
    }

    private static void writeMedia(JsonGenerator generator, Media media) throws Exception
    {
      ObjectNode node = createObjectNode(media);
      mapper.writeTree(generator, node);
    }

    private static void writeImage(JsonGenerator generator, Image image) throws Exception
    {
      ObjectNode node = createObjectNode(image);
      mapper.writeTree(generator, node);
    }

    private static void writeImages(JsonGenerator generator, List<Image> images) throws Exception
    {
      ArrayNode node = createArrayNode(images);
      mapper.writeTree(generator, node);
    }
  }
}
