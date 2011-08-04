package serializers.jackson;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import serializers.JavaBuiltIn;
import serializers.Serializer;
import serializers.TestGroups;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

/**
 * Driver that uses Jackson for manual tree processing (to/from byte[]).
 */
public class JacksonJsonTree extends Serializer<MediaContent>
{
  public static void register(TestGroups groups)
  {
    groups.media.add(JavaBuiltIn.MediaTransformer, new JacksonJsonTree("json/jackson/tree"));
  }

  protected static final ObjectMapper mapper = new ObjectMapper();

  private final String name;

  public JacksonJsonTree(String name) {
      this.name = name;
  }

  public String getName() {
      return name;
  }

  public MediaContent deserialize(byte[] array) throws Exception
  {
      return readMediaContent(mapper.readTree(new ByteArrayInputStream(array)));
  }

  public byte[] serialize(MediaContent mediaContent) throws Exception
  {
      JsonNode root = asTree(mediaContent, mapper.createObjectNode());
      return mapper.writeValueAsBytes(root);
  }

    // // // Methods for deserializing using intermediate Tree representation
    
    protected static Image readImage(JsonNode node)
    {
      Image image = new Image();
      image.height = node.get("height").getIntValue();
      image.size = Image.Size.valueOf(node.get("size").getTextValue());
      image.title = node.get("title").getTextValue();
      image.uri = node.get("uri").getTextValue();
      image.width = node.get("width").getIntValue();
      return image;
    }

    protected static List<Image> readImages(ArrayNode imagesNode)
    {
      int size = imagesNode.size();
      List<Image> images = new ArrayList<Image>(size);
      for (JsonNode image : imagesNode) {
        images.add(readImage(image));
      }
      return images;
    }

    protected static MediaContent readMediaContent(JsonNode root) throws Exception
    {
      MediaContent mediaContent = new MediaContent();
      mediaContent.media = readMedia(root.get("media"));
      mediaContent.images = readImages((ArrayNode) root.get("images"));
      return mediaContent;
    }

    protected static Media readMedia(JsonNode node)
    {
      Media media = new Media();
      JsonNode bitrate = node.get("bitrate");
      if (bitrate != null && !bitrate.isNull()) {
        media.bitrate = bitrate.getIntValue();
        media.hasBitrate = true;
      }
      media.copyright = node.path("copyright").getTextValue();
      media.duration = node.path("duration").getLongValue();
      media.format = node.path("format").getTextValue();
      media.height = node.path("height").getIntValue();
      media.player = Media.Player.valueOf(node.get("player").getTextValue());
      ArrayNode personsArrayNode = (ArrayNode) node.get("persons");
      int size = personsArrayNode.size();
      List<String> persons = new ArrayList<String>(size);
      for (JsonNode person : personsArrayNode) {
        persons.add(person.getTextValue());
      }
      media.persons = persons;
      media.size = node.get("size").getIntValue();
      media.title = node.get("title").getTextValue();
      media.uri = node.get("uri").getTextValue();
      media.width = node.get("width").getIntValue();
      return media;
    }

    // // // Methods for serializing using intermediate Tree representation
    
    protected static JsonNode asTree(MediaContent mediaContent, ObjectNode node) throws Exception
    {
        addMedia(mediaContent.media, node.putObject("media"));
        addImages(mediaContent.images, node.putArray("images"));
        return node;
    }
    
    protected static ObjectNode addImage(Image image, ObjectNode node)
    {
      node.put("height", image.height);
      node.put("size", image.size.name());
      node.put("title", image.title);
      node.put("uri", image.uri);
      node.put("width", image.width);
      return node;
    }

    protected static ArrayNode addImages(List<Image> images, ArrayNode node)
    {
      for (Image image : images) {
        addImage(image, node.addObject());
      }
      return node;
    }

    protected static ObjectNode addMedia(Media media, ObjectNode node)
    {
      if (media.hasBitrate) {
        node.put("bitrate", media.bitrate);
      }
      node.put("copyright", media.copyright);
      node.put("duration", media.duration);
      node.put("format", media.format);
      node.put("height", media.height);
      ArrayNode persons = mapper.createArrayNode();
      for (String person : media.persons) {
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
}
