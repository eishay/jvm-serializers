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
    image.setUri(readElement(parser, "ul"));
    image.setTitle(readElement(parser, "tl"));
    image.setWidth(Integer.parseInt(readElement(parser, "wd")));
    image.setHeight(Integer.parseInt(readElement(parser, "hg")));
    image.setSize(Size.valueOf(readElement(parser, "sz")));
    // need to match close tag
    if (parser.nextTag() != XMLStreamConstants.END_ELEMENT) {
        throw new IllegalStateException("Expected closing </im>");
    }
    return image;
  }

  private Media readMedia (XMLStreamReader parser) throws Exception
  {
    Media media = new Media();
    media.setPlayer(Player.valueOf(readElement(parser, "pl")));
    media.setUri(readElement(parser, "ul"));
    media.setTitle(readElement(parser, "tl"));
    media.setWidth(Integer.parseInt(readElement(parser, "wd")));
    media.setHeight(Integer.parseInt(readElement(parser, "hg")));
    media.setFormat(readElement(parser, "fr"));
    media.setDuration(Long.parseLong(readElement(parser, "dr")));
    media.setSize(Long.parseLong(readElement(parser, "sz")));
    media.setBitrate(Integer.parseInt(readElement(parser, "br")));

    searchTag(parser, "pr");
    do {
        media.addToPerson(parser.getElementText());
    } while (parser.nextTag() == XMLStreamConstants.START_ELEMENT
             && "pr".equals(parser.getLocalName()));
    if (!"md".equals(parser.getLocalName())) {
        throw new IllegalStateException("Expected closing </md>, got </"+parser.getLocalName()+">");
    }
    return media;
  }

  private String readElement (XMLStreamReader parser, String string) throws XMLStreamException
  {
    while(true)
    {
      if(parser.next() != XMLStreamConstants.START_ELEMENT) continue;
      if(parser.getLocalName().equals(string)) return parser.getElementText();
    }
  }

  private void searchTag (XMLStreamReader parser, String string) throws XMLStreamException
  {
    while(true)
    {
      if(parser.next() != XMLStreamConstants.START_ELEMENT) continue;
      if(parser.getLocalName().equals(string)) return;
    }
  }

    // // // Serialization

  public byte[] serialize(MediaContent content) throws Exception
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(expectedSize);
    XMLStreamWriter writer = outFactory.createXMLStreamWriter(baos);
    writer.writeStartDocument("ISO-8859-1", "1.0");
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
    writer.writeStartElement("im");
    writeElement(writer, "ul", image.getUri());
    writeElement(writer, "tl", image.getTitle());
    writeElement(writer, "wd", String.valueOf(image.getWidth()));
    writeElement(writer, "hg", String.valueOf(image.getHeight()));
    writeElement(writer, "sz", String.valueOf(image.getSize()));
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
    writer.writeStartElement("md");
    writeElement(writer, "pl", media.getPlayer().name());
    writeElement(writer, "ul", media.getUri());
    writeElement(writer, "tl", media.getTitle());
    writeElement(writer, "wd", String.valueOf(media.getWidth()));
    writeElement(writer, "hg", String.valueOf(media.getHeight()));
    writeElement(writer, "fr", media.getFormat());
    writeElement(writer, "dr", String.valueOf(media.getDuration()));
    writeElement(writer, "sz", String.valueOf(media.getSize()));
    writeElement(writer, "br", String.valueOf(media.getBitrate()));
    for (String person : media.getPersons()) {
        writeElement(writer, "pr", person);
    }
    writer.writeEndElement();
  }
}
