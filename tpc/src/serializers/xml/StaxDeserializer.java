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

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.*;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

public class StaxDeserializer
{
    public final MediaContent readDocument(XMLStreamReader parser) throws XMLStreamException
    {
        return readMediaContent(parser);
    }

    public final MediaContent[] readDocument(XMLStreamReader parser, int items) throws XMLStreamException
    {
        MediaContent[] result = new MediaContent[items];
        searchTag(parser, XmlStax.STREAM_ROOT);
        for (int i = 0; i < items; ++i) {
            result[i] = readMediaContent(parser);
        }
        // and should have closing tag at this point
        if (parser.nextTag() != XMLStreamConstants.END_ELEMENT) {
            throw new IllegalStateException("Expected closing tag, got: "+parser.getEventType());
        }
        if (!XmlStax.STREAM_ROOT.equals(parser.getLocalName())) {
            throw new IllegalStateException("Expected closing </"+XmlStax.STREAM_ROOT+">, got </"+parser.getLocalName()+">");
        }
        return result;
    }
    
    private MediaContent readMediaContent(XMLStreamReader parser) throws XMLStreamException
    {
        searchTag(parser, "mc");
        Media media = readMedia(parser);
        List<Image> images = new ArrayList<Image>();
        if (parser.nextTag() != XMLStreamConstants.START_ELEMENT) {
            throw new IllegalStateException("Expected <im>, no START_ELEMENT encountered but "+parser.getEventType());
        }
        do {
            if (!FULL_FIELD_NAME_IMAGES.equals(parser.getLocalName())) {
                throw new IllegalStateException("Expected <"+FULL_FIELD_NAME_IMAGES+">, got <"+parser.getLocalName()+">");
            }
            images.add(readImage(parser));
        } while (parser.nextTag() == XMLStreamConstants.START_ELEMENT);
        // and should have closing </mc> at this point
        if (!"mc".equals(parser.getLocalName())) {
            throw new IllegalStateException("Expected closing </mc>, got </"+parser.getLocalName()+">");
        }
        return new MediaContent(media, images);
    }
    
      private Image readImage (XMLStreamReader parser) throws XMLStreamException
      {
           Image image = new Image();
           image.uri = readElement(parser, FULL_FIELD_NAME_URI);
           image.title = readElement(parser, FULL_FIELD_NAME_TITLE);
           image.width = Integer.parseInt(readElement(parser, FULL_FIELD_NAME_WIDTH));
           image.height = Integer.parseInt(readElement(parser, FULL_FIELD_NAME_HEIGHT));
           image.size = Image.Size.valueOf(readElement(parser, FULL_FIELD_NAME_SIZE));
           // need to match close tag
           if (parser.nextTag() != XMLStreamConstants.END_ELEMENT) {
                throw new IllegalStateException("Expected closing </"+FULL_FIELD_NAME_IMAGES+">");
           }
           return image;
      }

      private Media readMedia (XMLStreamReader parser) throws XMLStreamException
      {
           Media media = new Media();
           media.player = Media.Player.valueOf(readElement(parser, FULL_FIELD_NAME_PLAYER));
           media.uri = readElement(parser, FULL_FIELD_NAME_URI);
           media.title = readElementMaybe(parser, FULL_FIELD_NAME_TITLE);
           media.width = Integer.parseInt(readElement(parser, FULL_FIELD_NAME_WIDTH));
           media.height = Integer.parseInt(readElement(parser, FULL_FIELD_NAME_HEIGHT));
           media.format = readElement(parser, FULL_FIELD_NAME_FORMAT);
           media.duration = Long.parseLong(readElement(parser, FULL_FIELD_NAME_DURATION));
           media.size = Long.parseLong(readElement(parser, FULL_FIELD_NAME_SIZE));
           String bitrateString = readElement(parser, FULL_FIELD_NAME_BITRATE);
           if (bitrateString != null) {
                media.hasBitrate = true;
                media.bitrate = Integer.parseInt(bitrateString);
           }
           media.copyright = readElementMaybe(parser, FULL_FIELD_NAME_COPYRIGHT);

           searchTag(parser, FULL_FIELD_NAME_PERSONS);
           List<String> persons = new ArrayList<String>();
           do {
                persons.add(parser.getElementText());
           } while (parser.nextTag() == XMLStreamConstants.START_ELEMENT
                && FULL_FIELD_NAME_PERSONS.equals(parser.getLocalName()));
           if (!FULL_FIELD_NAME_MEDIA.equals(parser.getLocalName())) {
                throw new IllegalStateException("Expected closing </"+FULL_FIELD_NAME_MEDIA+">, got </"+parser.getLocalName()+">");
           }
           media.persons = persons;
           return media;
      }

      private String readElementMaybe(XMLStreamReader parser, String string) throws XMLStreamException
      {
           if (parser.getEventType() != XMLStreamConstants.START_ELEMENT) {
                while (parser.next() != XMLStreamConstants.START_ELEMENT) { }
           }
           return (parser.getLocalName().equals(string)) ? parser.getElementText() : null;
      }

      private String readElement(XMLStreamReader parser, String string) throws XMLStreamException
      {
           // If not at START_ELEMENT, find one (usually called when at END_ELEMENT)
           if (parser.getEventType() != XMLStreamConstants.START_ELEMENT) {
                while (parser.next() != XMLStreamConstants.START_ELEMENT) { }
           }
           while (true) {
                if (parser.getLocalName().equals(string)) {
                     return parser.getElementText();
                }
                while (parser.next() != XMLStreamConstants.START_ELEMENT) { }
           }
      }

      protected void searchTag(XMLStreamReader parser, String string) throws XMLStreamException
      {
           // may already be located at the start element
           if (parser.getEventType() == XMLStreamConstants.START_ELEMENT
                && parser.getLocalName().equals(string)) {
                return;
           }
           while (true) {
                if (parser.nextTag() == XMLStreamConstants.START_ELEMENT
                     && parser.getLocalName().equals(string)) {
                     return;
                }
           }
      }
}
