package serializers.xml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.*;

import core.serializers.Serializer;
import serializers.JavaBuiltIn;
import core.TestGroups;

import data.media.*;
import static data.media.FieldMapping.*;

public class XmlStax
{
    /**
     * Since XML streams must still have a single root, we'll use
     * this as the tag
     */
    private final static String STREAM_ROOT = "stream";
    
    public static final Handler[] HANDLERS = new Handler[] {
        new Handler("woodstox",
                new com.ctc.wstx.stax.WstxInputFactory(),
                new com.ctc.wstx.stax.WstxOutputFactory()),
        new Handler("aalto",
                new com.fasterxml.aalto.stax.InputFactoryImpl(),
                new com.fasterxml.aalto.stax.OutputFactoryImpl()),
        new Handler("fastinfo",
                new com.sun.xml.fastinfoset.stax.factory.StAXInputFactory(),
                new com.sun.xml.fastinfoset.stax.factory.StAXOutputFactory()),
    };
    
    public static void register(TestGroups groups, boolean woodstox, boolean aalto, boolean fastinfoset)
    {
        if (woodstox) {
            groups.media.add(JavaBuiltIn.mediaTransformer, new MediaSerializer(HANDLERS[0]));
        }
        if (aalto) {
            groups.media.add(JavaBuiltIn.mediaTransformer, new MediaSerializer(HANDLERS[1]));
        }
        if (fastinfoset) {
            groups.media.add(JavaBuiltIn.mediaTransformer, new MediaSerializer(HANDLERS[2]));
        }
    }

    // -------------------------------------------------------------------
    // Implementations

    public static final class Handler
    {
        protected final String name;
        protected final XMLInputFactory inFactory;
        protected final XMLOutputFactory outFactory;

        protected Handler(String name, XMLInputFactory inFactory, XMLOutputFactory outFactory)
        {
            this.name = name;
            this.inFactory = inFactory;
            this.outFactory = outFactory;
        }
    }

    // -------------------------------------------------------------------
    // Serializers

    public static final class MediaSerializer extends Serializer<MediaContent>
    {
        private final Handler handler;

        public MediaSerializer(Handler handler)
        {
            this.handler = handler;
        }

        public String getName() { return "xml/"+handler.name+"-manual"; }

        // // Public API

        @Override
        public MediaContent deserialize (byte[] array) throws XMLStreamException
        {
            XMLStreamReader parser = handler.inFactory.createXMLStreamReader(new ByteArrayInputStream(array));
            MediaContent content = readMediaContent(parser);
            parser.close();
            return content;
        }

        @Override
        public byte[] serialize(MediaContent content) throws Exception
        {
            ByteArrayOutputStream baos = outputStream(content);
            XMLStreamWriter writer = handler.outFactory.createXMLStreamWriter(baos, "UTF-8");
            writer.writeStartDocument("UTF-8", "1.0");
            writeMediaContent(writer, content);
            writer.writeEndDocument();
            writer.flush();
            writer.close();
            return baos.toByteArray();
        }

        @Override
        public final void serializeItems(MediaContent[] items, OutputStream out) throws XMLStreamException
        {
            // XML requires single root, so add bogus "stream"
            XMLStreamWriter writer = handler.outFactory.createXMLStreamWriter(out, "UTF-8");
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement(STREAM_ROOT);
            for (MediaContent item : items) {
                writeMediaContent(writer, item);
            }
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
            writer.close();
        }

        @Override
        public MediaContent[] deserializeItems(InputStream in, int numberOfItems) throws XMLStreamException 
        {
            XMLStreamReader parser = handler.inFactory.createXMLStreamReader(in);
            MediaContent[] result = new MediaContent[numberOfItems];
            searchTag(parser, STREAM_ROOT);
            for (int i = 0; i < numberOfItems; ++i) {
                result[i] = readMediaContent(parser);
            }
            // and should have closing tag at this point
            if (parser.nextTag() != XMLStreamConstants.END_ELEMENT) {
                throw new IllegalStateException("Expected closing tag, got: "+parser.getEventType());
            }
            if (!STREAM_ROOT.equals(parser.getLocalName())) {
                throw new IllegalStateException("Expected closing </"+STREAM_ROOT+">, got </"+parser.getLocalName()+">");
            }
            parser.close();
            return result;
        }
        
       // // Internal methods, deserialization		

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

		private void searchTag(XMLStreamReader parser, String string) throws XMLStreamException
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

        // Serialization

        protected void writeMediaContent(XMLStreamWriter writer, MediaContent content)
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
}
