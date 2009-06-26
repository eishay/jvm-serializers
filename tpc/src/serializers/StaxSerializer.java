package serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import serializers.java.Image;
import serializers.java.Media;
import serializers.java.MediaContent;
import serializers.java.Image.Size;
import serializers.java.Media.Player;

public class StaxSerializer extends StdMediaSerializer
{
    public int expectedSize = 0;
    final XMLInputFactory inFactory;
    final XMLOutputFactory outFactory;

    public StaxSerializer(String name, XMLInputFactory inf, XMLOutputFactory outf) {
        super(name);
        inFactory = inf;
        outFactory = outf;
    }

    // // // Deserialization

  public MediaContent deserialize (byte[] array) throws Exception
  {
    XMLStreamReader parser = inFactory.createXMLStreamReader(new ByteArrayInputStream(array));
    searchTag(parser, "mc");
    MediaContent content = new MediaContent(readMedia(parser));
    if (parser.nextTag() != XMLStreamConstants.START_ELEMENT) {
        throw new IllegalStateException("Expected <im>, no START_ELEMENT encountered but "+parser.getEventType());
    }
    do {
        if (!"im".equals(parser.getLocalName())) {
            throw new IllegalStateException("Expected <im>, got <"+parser.getLocalName()+">");
        }
        content.addImage(readImage(parser));
    } while (parser.nextTag() == XMLStreamConstants.START_ELEMENT);
    // and should have closing </mc> at this point
    if (!"mc".equals(parser.getLocalName())) {
        throw new IllegalStateException("Expected closing </mc>, got </"+parser.getLocalName()+">");
    }
    parser.close();
    return content;
  }

  private Image readImage (XMLStreamReader parser) throws Exception
  {
    Image image = new Image();
    image.setUri(readElement(parser, FIELD_NAME_URI));
    image.setTitle(readElement(parser, FIELD_NAME_TITLE));
    image.setWidth(Integer.parseInt(readElement(parser, FIELD_NAME_WIDTH)));
    image.setHeight(Integer.parseInt(readElement(parser, FIELD_NAME_HEIGHT)));
    image.setSize(Size.valueOf(readElement(parser, FIELD_NAME_SIZE)));
    // need to match close tag
    if (parser.nextTag() != XMLStreamConstants.END_ELEMENT) {
        throw new IllegalStateException("Expected closing </"+FIELD_NAME_IMAGES+">");
    }
    return image;
  }

  private Media readMedia (XMLStreamReader parser) throws Exception
  {
    Media media = new Media();
    media.setPlayer(Player.valueOf(readElement(parser, FIELD_NAME_PLAYER)));
    media.setUri(readElement(parser, FIELD_NAME_URI));
    media.setTitle(readElement(parser, FIELD_NAME_TITLE));
    media.setWidth(Integer.parseInt(readElement(parser, FIELD_NAME_WIDTH)));
    media.setHeight(Integer.parseInt(readElement(parser, FIELD_NAME_HEIGHT)));
    media.setFormat(readElement(parser, FIELD_NAME_FORMAT));
    media.setDuration(Long.parseLong(readElement(parser, FIELD_NAME_DURATION)));
    media.setSize(Long.parseLong(readElement(parser, FIELD_NAME_SIZE)));
    media.setBitrate(Integer.parseInt(readElement(parser, FIELD_NAME_BITRATE)));

    searchTag(parser, FIELD_NAME_PERSONS);
    do {
        media.addToPerson(parser.getElementText());
    } while (parser.nextTag() == XMLStreamConstants.START_ELEMENT
             && FIELD_NAME_PERSONS.equals(parser.getLocalName()));
    if (!"md".equals(parser.getLocalName())) {
        throw new IllegalStateException("Expected closing </md>, got </"+parser.getLocalName()+">");
    }
    return media;
  }

  private String readElement(XMLStreamReader parser, String string) throws XMLStreamException
  {
      while (true) {
          if (parser.nextTag() == XMLStreamConstants.START_ELEMENT
              && parser.getLocalName().equals(string)) {
              return parser.getElementText();
          }
      }
  }

  private void searchTag(XMLStreamReader parser, String string) throws XMLStreamException
  {
      while (true) {
          if (parser.nextTag() == XMLStreamConstants.START_ELEMENT
              && parser.getLocalName().equals(string)) {
              return;
          }
      }
  }

    // // // Serialization

  public byte[] serialize(MediaContent content) throws Exception
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(expectedSize);
    XMLStreamWriter writer = outFactory.createXMLStreamWriter(baos, "UTF-8");
    writer.writeStartDocument("UTF-8", "1.0");
    writer.writeStartElement("mc");
    writeMedia(writer, content.getMedia());
    for (int i = 0, len = content.imageCount(); i < len; ++i) {
        writeImage(writer, content.getImage(i));
    }
    writer.writeEndElement();
    writer.writeEndDocument();
    writer.flush();
    writer.close();
    byte[] array = baos.toByteArray();
    expectedSize = array.length;
    return array;
  }

  private void writeImage (XMLStreamWriter writer, Image image) throws XMLStreamException
  {
      writer.writeStartElement(FIELD_NAME_IMAGES);
      writeElement(writer, FIELD_NAME_URI, image.getUri());
      writeElement(writer, FIELD_NAME_TITLE, image.getTitle());
      writeElement(writer, FIELD_NAME_WIDTH, String.valueOf(image.getWidth()));
      writeElement(writer, FIELD_NAME_HEIGHT, String.valueOf(image.getHeight()));
      writeElement(writer, FIELD_NAME_SIZE, String.valueOf(image.getSize()));
      writer.writeEndElement();
  }

  private void writeElement(XMLStreamWriter writer, String name, String value) throws XMLStreamException
  {
      writer.writeStartElement(name);
      writer.writeCharacters(value);
      writer.writeEndElement();
  }

  private void writeMedia (XMLStreamWriter writer, Media media) throws XMLStreamException
  {
      writer.writeStartElement(FIELD_NAME_MEDIA);
      writeElement(writer, FIELD_NAME_PLAYER, media.getPlayer().name());
      writeElement(writer, FIELD_NAME_URI, media.getUri());
      writeElement(writer, FIELD_NAME_TITLE, media.getTitle());
      writeElement(writer, FIELD_NAME_WIDTH, String.valueOf(media.getWidth()));
      writeElement(writer, FIELD_NAME_HEIGHT, String.valueOf(media.getHeight()));
      writeElement(writer, FIELD_NAME_FORMAT, media.getFormat());
      writeElement(writer, FIELD_NAME_DURATION, String.valueOf(media.getDuration()));
      writeElement(writer, FIELD_NAME_SIZE, String.valueOf(media.getSize()));
      writeElement(writer, FIELD_NAME_BITRATE, String.valueOf(media.getBitrate()));
      for (String person : media.getPersons()) {
          writeElement(writer, FIELD_NAME_PERSONS, person);
      }
      writer.writeEndElement();
  }
}
