package serializers;

import serializers.java.MediaContent;
import serializers.java.Media;
import serializers.java.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonParser;

public class JsonSerializer extends StdMediaSerializer
{
  private final JsonFactory _factory;
  public int expectedSize = 0;

  public JsonSerializer()
  {
      super("json (jackson)");
      _factory = new JsonFactory();
  }


    public byte[] serialize(MediaContent content) throws Exception
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(expectedSize);
    JsonGenerator generator = _factory.createJsonGenerator(baos, JsonEncoding.UTF8);
    generator.writeStartObject();
    writeMedia(generator, content.getMedia());
    writeImage(generator, content.getImage(0));
    writeImage(generator, content.getImage(1));
    generator.writeEndObject();
    generator.close();
    byte[] array = baos.toByteArray();
    expectedSize = array.length;
    return array;
  }

  public MediaContent deserialize(byte[] array) throws Exception
  {
    JsonParser parser = _factory.createJsonParser(array);
    parser.nextToken(); // start object
    MediaContent mc = new MediaContent(readMedia(parser));
    mc.addImage(readImage(parser));
    mc.addImage(readImage(parser));
    parser.nextToken(); // end object
    parser.close();
    return mc;
  }

  private void writeMedia(JsonGenerator generator, Media media) throws IOException
  {
    generator.writeFieldName("md");
    generator.writeStartObject();
    writeStringElement(generator, "pl", media.getPlayer().name());
    writeStringElement(generator, "ul", media.getUri());
    writeStringElement(generator, "tl", media.getTitle());
    writeInt(generator, "wd", media.getWidth());
    writeInt(generator, "hg", media.getHeight());
    writeStringElement(generator, "fr", media.getFormat());
    writeLong(generator, "dr", media.getDuration());
    writeLong(generator, "sz", media.getSize());
    writeInt(generator, "br", media.getBitrate());
    writeStringElement(generator, "pr", media.getPersons().get(0));
    writeStringElement(generator, "pr", media.getPersons().get(1));
    generator.writeEndObject();
  }

  private void writeImage(JsonGenerator generator, Image image) throws IOException
  {
    generator.writeFieldName("im");
    generator.writeStartObject();
    writeStringElement(generator, "ul", image.getUri());
    writeStringElement(generator, "tl", image.getTitle());
    writeInt(generator, "wd", image.getWidth());
    writeInt(generator, "hg", image.getHeight());
    writeStringElement(generator, "sz", image.getSize().name());
    generator.writeEndObject();
  }

  private void writeStringElement(JsonGenerator generator, String name, String value) throws IOException
  {
      generator.writeStringField(name, value);
  }

  private void writeInt(JsonGenerator generator, String name, int value) throws IOException
  {
      generator.writeNumberField(name, value);
  }

  private void writeLong(JsonGenerator generator, String name, long value) throws IOException
  {
      generator.writeNumberField(name, value);
  }

  private Media readMedia(JsonParser parser) throws IOException
  {
    parser.nextToken(); // field name
    parser.nextToken(); // start object
    Media media = new Media();
    media.setPlayer(Media.Player.valueOf(readStringElement(parser, "pl")));
    media.setUri(readStringElement(parser, "ul"));
    media.setTitle(readStringElement(parser, "tl"));
    media.setWidth(readIntElement(parser, "wd"));
    media.setHeight(readIntElement(parser, "hg"));
    media.setFormat(readStringElement(parser, "fr"));
    media.setDuration(readLongElement(parser, "dr"));
    media.setSize(readLongElement(parser, "sz"));
    media.setBitrate(readIntElement(parser, "br"));
    media.addToPerson(readStringElement(parser, "pr"));
    media.addToPerson(readStringElement(parser, "pr"));
    parser.nextToken(); // end object
    return media;
  }

  private Image readImage(JsonParser parser) throws IOException
  {
    parser.nextToken(); // field name
    parser.nextToken(); // start object
    Image image = new Image();
    image.setUri(readStringElement(parser, "ul"));
    image.setTitle(readStringElement(parser, "tl"));
    image.setWidth(readIntElement(parser, "wd"));
    image.setHeight(readIntElement(parser, "hg"));
    image.setSize(Image.Size.valueOf(readStringElement(parser, "sz")));
    parser.nextToken(); // end object
    return image;
  }

  private String readStringElement(JsonParser parser, String name) throws IOException
  {
    findToken(parser, name);
    return parser.getText();
  }

  private long readLongElement(JsonParser parser, String name) throws IOException
  {
    findToken(parser, name);
    return parser.getLongValue();
  }

  private int readIntElement(JsonParser parser, String name) throws IOException
  {
    findToken(parser, name);
    return parser.getIntValue();
  }

  private void findToken(JsonParser parser, String name) throws IOException
  {
    while (parser.nextToken() != null)
    {
      if (parser.getCurrentName().equals(name))
      {
        if (parser.nextToken() == null)
        {
          throw new IllegalStateException("Missing value for attribute: " + name);
        }
        return;
      }
    }

    throw new IllegalStateException("Could not find expected token: " + name);
  }
}
