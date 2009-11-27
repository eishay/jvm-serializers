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
      this("json (jackson)");
  }

    protected JsonSerializer(String id) {
        super(id);
      _factory = new JsonFactory();
    }


    public byte[] serialize(MediaContent content) throws Exception
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(expectedSize);
    JsonGenerator generator = _factory.createJsonGenerator(baos, JsonEncoding.UTF8);
    generator.writeStartObject();
    writeMedia(generator, content.getMedia());
    generator.writeFieldName(FIELD_NAME_IMAGES);
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
    // loop for main-level fields
    MediaContent mc = new MediaContent();
    JsonToken t;

    while ((t = parser.nextToken()) != JsonToken.END_OBJECT) {
        if (t != JsonToken.FIELD_NAME) {
            reportIllegal(parser, JsonToken.FIELD_NAME);
        }
        String field = parser.getCurrentName();
        Integer I = fieldToIndex.get(field);
        if (I != null) {
            switch (I.intValue()) {
            case FIELD_IX_MEDIA:
                mc.setMedia(readMedia(parser));
                continue;
            case FIELD_IX_IMAGES:
                if (parser.nextToken() != JsonToken.START_ARRAY) {
                    reportIllegal(parser, JsonToken.START_ARRAY);
                }
                while (parser.nextToken() == JsonToken.START_OBJECT) {
                    mc.addImage(readImage(parser));
                }
                continue;
            }
        }
        throw new IllegalStateException("Unexpected field '"+field+"'");
    }
    parser.close();
    return mc;
  }

  private void writeMedia(JsonGenerator generator, Media media) throws IOException
  {
    generator.writeFieldName(FIELD_NAME_MEDIA);
    generator.writeStartObject();
    generator.writeStringField(FIELD_NAME_PLAYER, media.getPlayer().name());
    generator.writeStringField(FIELD_NAME_URI, media.getUri());
    generator.writeStringField(FIELD_NAME_TITLE, media.getTitle());
    generator.writeNumberField(FIELD_NAME_WIDTH, media.getWidth());
    generator.writeNumberField(FIELD_NAME_HEIGHT, media.getHeight());
    generator.writeStringField(FIELD_NAME_FORMAT, media.getFormat());
    generator.writeNumberField(FIELD_NAME_DURATION, media.getDuration());
    generator.writeNumberField(FIELD_NAME_SIZE, media.getSize());
    generator.writeNumberField(FIELD_NAME_BITRATE, media.getBitrate());
    generator.writeFieldName(FIELD_NAME_PERSONS);
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
    generator.writeStringField(FIELD_NAME_URI, image.getUri());
    generator.writeStringField(FIELD_NAME_TITLE, image.getTitle());
    generator.writeNumberField(FIELD_NAME_WIDTH, image.getWidth());
    generator.writeNumberField(FIELD_NAME_HEIGHT, image.getHeight());
    generator.writeStringField(FIELD_NAME_SIZE, image.getSize().name());
    generator.writeEndObject();
  }

  private Media readMedia(JsonParser parser) throws IOException
  {
      if (parser.nextToken() != JsonToken.START_OBJECT) {
          reportIllegal(parser, JsonToken.START_OBJECT);
      }
      Media media = new Media();
      JsonToken t;
      
      while ((t = parser.nextToken()) != JsonToken.END_OBJECT) {
          if (t != JsonToken.FIELD_NAME) {
              reportIllegal(parser, JsonToken.FIELD_NAME);
          }
          // read value token (or START_ARRAY) 
          String field = parser.getCurrentName();
          t = parser.nextToken();
          Integer I = fieldToIndex.get(field);
          if (I != null) {
              switch (I.intValue()) {
              case FIELD_IX_PLAYER:
                  media.setPlayer(Media.Player.valueOf(parser.getText()));
                  continue;
              case FIELD_IX_URI:
                  media.setUri(parser.getText());
                  continue;
              case FIELD_IX_TITLE:
                  media.setTitle(parser.getText());
                  continue;
              case FIELD_IX_WIDTH:
                  media.setWidth(parser.getIntValue());
                  continue;
              case FIELD_IX_HEIGHT:
                  media.setHeight(parser.getIntValue());
                  continue;
              case FIELD_IX_FORMAT:
                  media.setFormat(parser.getText());
                  continue;
              case FIELD_IX_DURATION:
                  media.setDuration(parser.getLongValue());
                  continue;
              case FIELD_IX_SIZE:
                  media.setSize(parser.getLongValue());
                  continue;
              case FIELD_IX_BITRATE:
                  media.setBitrate(parser.getIntValue());
                  continue;
              case FIELD_IX_PERSONS:
                  if (t != JsonToken.START_ARRAY) {
                      reportIllegal(parser, JsonToken.START_ARRAY);
                  }
                  while (parser.nextToken() != JsonToken.END_ARRAY) {
                      media.addToPerson(parser.getText());
                  }
                  continue;
              }
          }
          throw new IllegalStateException("Unexpected field '"+field+"'");
      }
      return media;
  }

  private Image readImage(JsonParser parser) throws IOException
  {
      JsonToken t;
      Image image = new Image();
      
      while ((t = parser.nextToken()) != JsonToken.END_OBJECT) {
          if (t != JsonToken.FIELD_NAME) {
              reportIllegal(parser, JsonToken.FIELD_NAME);
          }
          String field = parser.getCurrentName();
          // read value token (or START_ARRAY) 
          t = parser.nextToken();
          Integer I = fieldToIndex.get(field);
          if (I != null) {
              switch (I.intValue()) {
              case FIELD_IX_URI:
                  image.setUri(parser.getText());
                  continue;
              case FIELD_IX_TITLE:
                  image.setTitle(parser.getText());
                  continue;
              case FIELD_IX_WIDTH:
                  image.setWidth(parser.getIntValue());
                  continue;
              case FIELD_IX_HEIGHT:
                  image.setHeight(parser.getIntValue());
                  continue;
              case FIELD_IX_SIZE:
                  image.setSize(Image.Size.valueOf(parser.getText()));
                  continue;
              }
          }
          throw new IllegalStateException("Unexpected field '"+field+"'");
      }
      return image;
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
