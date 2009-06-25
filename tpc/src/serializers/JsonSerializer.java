package serializers;

import serializers.java.MediaContent;
import serializers.java.Media;
import serializers.java.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

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
    generator.writeFieldName("im");
    generator.writeStartArray();
    for (int i = 0, len = content.imageCount(); i < len; ++i) {
        writeImage(generator, content.getImage(i));
    }
    generator.writeEndArray();

    generator.writeEndObject();
    generator.close();
    byte[] array = baos.toByteArray();
    expectedSize = array.length;
    return array;
  }

  public MediaContent deserialize(byte[] array) throws Exception
  {
    JsonParser parser = _factory.createJsonParser(array);
    if (parser.nextToken() != JsonToken.START_OBJECT) {
        reportIllegal(parser, JsonToken.START_OBJECT);
    }
    MediaContent mc = new MediaContent(readMedia(parser));
    if (parser.nextToken() != JsonToken.FIELD_NAME
        || !"im".equals(parser.getCurrentName())) { // im
        reportIllegal(parser, JsonToken.FIELD_NAME);
    }
    if (parser.nextToken() != JsonToken.START_ARRAY) {
        reportIllegal(parser, JsonToken.START_ARRAY);
    }
    while (parser.nextToken() == JsonToken.START_OBJECT) {
        mc.addImage(readImage(parser));
    }
    if (parser.nextToken() != JsonToken.END_OBJECT) {
        reportIllegal(parser, JsonToken.END_OBJECT);
    }
    parser.close();
    return mc;
  }

  private void writeMedia(JsonGenerator generator, Media media) throws IOException
  {
    generator.writeFieldName("md");
    generator.writeStartObject();
    generator.writeStringField("pl", media.getPlayer().name());
    generator.writeStringField("ul", media.getUri());
    generator.writeStringField("tl", media.getTitle());
    generator.writeNumberField("wd", media.getWidth());
    generator.writeNumberField("hg", media.getHeight());
    generator.writeStringField("fr", media.getFormat());
    generator.writeNumberField("dr", media.getDuration());
    generator.writeNumberField("sz", media.getSize());
    generator.writeNumberField("br", media.getBitrate());
    generator.writeFieldName("pr");
    generator.writeStartArray();
    for (String person : media.getPersons()) {
        generator.writeString(person);
    }
    generator.writeEndArray();
    generator.writeEndObject();
  }

  private void writeImage(JsonGenerator generator, Image image) throws IOException
  {
    generator.writeStartObject();
    generator.writeStringField("ul", image.getUri());
    generator.writeStringField("tl", image.getTitle());
    generator.writeNumberField("wd", image.getWidth());
    generator.writeNumberField("hg", image.getHeight());
    generator.writeStringField("sz", image.getSize().name());
    generator.writeEndObject();
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

    if (findToken(parser, "pr") != JsonToken.START_ARRAY) {
        reportIllegal(parser, JsonToken.START_ARRAY);
    }
    while (parser.nextToken() != JsonToken.END_ARRAY) {
        media.addToPerson(parser.getText());
    }
    if (parser.nextToken() != JsonToken.END_OBJECT) {
        reportIllegal(parser, JsonToken.END_OBJECT);
    }
    return media;
  }

  private Image readImage(JsonParser parser) throws IOException
  {
      // gets called with opening START_OBJECT
    Image image = new Image();
    image.setUri(readStringElement(parser, "ul"));
    image.setTitle(readStringElement(parser, "tl"));
    image.setWidth(readIntElement(parser, "wd"));
    image.setHeight(readIntElement(parser, "hg"));
    image.setSize(Image.Size.valueOf(readStringElement(parser, "sz")));
    if (parser.nextToken() != JsonToken.END_OBJECT) {
        reportIllegal(parser, JsonToken.END_OBJECT);
    }
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

  private JsonToken findToken(JsonParser parser, String name) throws IOException
  {
      JsonToken t;
      while ((t = parser.nextToken()) != null) {
          if (t == JsonToken.FIELD_NAME && parser.getCurrentName().equals(name)) {
              t = parser.nextToken();
              if (t == null) {
                  throw new IllegalStateException("Missing value for attribute: " + name);
              }
              return t;
          }
      }
      throw new IllegalStateException("Could not find expected field: " + name);
  }

    private void reportIllegal(JsonParser parser, JsonToken expToken)
        throws IOException
    {
        JsonToken curr = parser.getCurrentToken();
        String msg = "Expected token "+expToken+"; got "+curr;
        if (curr == JsonToken.FIELD_NAME) {
            msg += " (current field name '"+parser.getCurrentName()+"')";
        }
        throw new IllegalStateException(msg);
    }
}
