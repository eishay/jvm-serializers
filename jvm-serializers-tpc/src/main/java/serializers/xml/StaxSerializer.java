package serializers.xml;

import static data.media.FieldMapping.FULL_FIELD_NAME_BITRATE;
import static data.media.FieldMapping.FULL_FIELD_NAME_COPYRIGHT;
import static data.media.FieldMapping.FULL_FIELD_NAME_DURATION;
import static data.media.FieldMapping.FULL_FIELD_NAME_FORMAT;
import static data.media.FieldMapping.FULL_FIELD_NAME_HEIGHT;
import static data.media.FieldMapping.FULL_FIELD_NAME_IMAGES;
import static data.media.FieldMapping.FULL_FIELD_NAME_MEDIA;
import static data.media.FieldMapping.FULL_FIELD_NAME_PERSONS;
import static data.media.FieldMapping.FULL_FIELD_NAME_PLAYER;
import static data.media.FieldMapping.FULL_FIELD_NAME_SIZE;
import static data.media.FieldMapping.FULL_FIELD_NAME_TITLE;
import static data.media.FieldMapping.FULL_FIELD_NAME_URI;
import static data.media.FieldMapping.FULL_FIELD_NAME_WIDTH;

import javax.xml.stream.*;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

public class StaxSerializer
{
    public void writeDocument(XMLStreamWriter writer, MediaContent content)
        throws XMLStreamException
    {
        writer.writeStartDocument("UTF-8", "1.0");
        writeMediaContent(writer, content);
        writer.writeEndDocument();
    }

    public void writeDocument(XMLStreamWriter writer, MediaContent[] items)
            throws XMLStreamException
    {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement(XmlStax.STREAM_ROOT);
        for (MediaContent item : items) {
            writeMediaContent(writer, item);
        }
        writer.writeEndElement();
        writer.writeEndDocument();
    }
    
    private void writeMediaContent(XMLStreamWriter writer, MediaContent content)
        throws XMLStreamException
    {
        writer.writeStartElement("mc");
        writeMedia(writer, content.media);
        for (Image image : content.images) {
            writeImage(writer, image);
        }
        writer.writeEndElement();
    }
          
      private void writeImage (XMLStreamWriter writer, Image image) throws XMLStreamException
      {
           writer.writeStartElement(FULL_FIELD_NAME_IMAGES);
           writeElement(writer, FULL_FIELD_NAME_URI, image.uri);
           if (image.title != null) writeElement(writer, FULL_FIELD_NAME_TITLE, image.title);
           writeElement(writer, FULL_FIELD_NAME_WIDTH, String.valueOf(image.width));
           writeElement(writer, FULL_FIELD_NAME_HEIGHT, String.valueOf(image.height));
           writeElement(writer, FULL_FIELD_NAME_SIZE, image.size.name());
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
           writer.writeStartElement(FULL_FIELD_NAME_MEDIA);
           writeElement(writer, FULL_FIELD_NAME_PLAYER, media.player.name());
           writeElement(writer, FULL_FIELD_NAME_URI, media.uri);
           if (media.title != null) writeElement(writer, FULL_FIELD_NAME_TITLE, media.title);
           writeElement(writer, FULL_FIELD_NAME_WIDTH, String.valueOf(media.width));
           writeElement(writer, FULL_FIELD_NAME_HEIGHT, String.valueOf(media.height));
           writeElement(writer, FULL_FIELD_NAME_FORMAT, media.format);
           writeElement(writer, FULL_FIELD_NAME_DURATION, String.valueOf(media.duration));
           writeElement(writer, FULL_FIELD_NAME_SIZE, String.valueOf(media.size));
           if (media.hasBitrate) writeElement(writer, FULL_FIELD_NAME_BITRATE, String.valueOf(media.bitrate));
           if (media.copyright != null) writeElement(writer, FULL_FIELD_NAME_COPYRIGHT, media.copyright);
           for (String person : media.persons) {
                writeElement(writer, FULL_FIELD_NAME_PERSONS, person);
           }
           writer.writeEndElement();
      }
}
