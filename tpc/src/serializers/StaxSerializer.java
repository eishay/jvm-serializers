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

  public MediaContent deserialize (byte[] array) throws Exception
  {
    XMLStreamReader parser = inFactory.createXMLStreamReader(new ByteArrayInputStream(array));
    searchTag(parser, "mc");
    MediaContent content = new MediaContent(readMedia(parser));
    content.addImage(readImage(parser));
    content.addImage(readImage(parser));
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
    media.addToPerson(readElement(parser, "pr"));
    media.addToPerson(readElement(parser, "pr"));
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

    public byte[] serialize(MediaContent content) throws Exception
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(expectedSize);
    XMLStreamWriter writer = outFactory.createXMLStreamWriter(baos);
    writer.writeStartDocument("ISO-8859-1", "1.0");
    writer.writeStartElement("mc");
    writeMedia(writer, content.getMedia());
    writeImage(writer, content.getImage(0));
    writeImage(writer, content.getImage(1));
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
    writeElement(writer, "pr", media.getPersons().get(0));
    writeElement(writer, "pr", media.getPersons().get(1));
    writer.writeEndElement();
  }
}
