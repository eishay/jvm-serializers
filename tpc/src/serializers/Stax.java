package serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import data.media.*;
import static data.media.FieldMapping.*;

public class Stax
{
	public static void register(TestGroups groups)
	{
		for (Handler h : Handlers) {
			// TODO: This doesn't work yet.  Need to properly handle optional fields in readMedia/readImage.
			//groups.media.add(JavaBuiltIn.MediaTransformer, new MediaSerializer(h));
		}
	}

	// -------------------------------------------------------------------
	// Implementations

	public static final class Handler
	{
		public final String name;
		public final XMLInputFactory inFactory;
		public final XMLOutputFactory outFactory;

		protected Handler(String name, XMLInputFactory inFactory, XMLOutputFactory outFactory)
		{
			this.name = name;
			this.inFactory = inFactory;
			this.outFactory = outFactory;
		}
	}

	public static final Handler[] Handlers = new Handler[] {
		new Handler("xml-fi/sun",
			new com.sun.xml.fastinfoset.stax.factory.StAXInputFactory(),
			new com.sun.xml.fastinfoset.stax.factory.StAXOutputFactory()),
		new Handler("xml/woodstox",
			new com.ctc.wstx.stax.WstxInputFactory(),
			new com.ctc.wstx.stax.WstxOutputFactory()),
		new Handler("xml/aalto",
			new com.fasterxml.aalto.stax.InputFactoryImpl(),
			new com.fasterxml.aalto.stax.OutputFactoryImpl()),
	};

	// -------------------------------------------------------------------
	// Serializers

	public static final class MediaSerializer extends Serializer<MediaContent>
	{
		private final Handler handler;

		public String getName() { return handler.name + "-stax"; }

		public MediaSerializer(Handler handler)
		{
			this.handler = handler;
		}

		// Deserialization

		public MediaContent deserialize (byte[] array) throws Exception
		{
			XMLStreamReader parser = handler.inFactory.createXMLStreamReader(new ByteArrayInputStream(array));
			searchTag(parser, "mc");
			Media media = readMedia(parser);
			List<Image> images = new ArrayList<Image>();
			if (parser.nextTag() != XMLStreamConstants.START_ELEMENT) {
				throw new IllegalStateException("Expected <im>, no START_ELEMENT encountered but "+parser.getEventType());
			}
			do {
				if (!"im".equals(parser.getLocalName())) {
					throw new IllegalStateException("Expected <im>, got <"+parser.getLocalName()+">");
				}
				images.add(readImage(parser));
			} while (parser.nextTag() == XMLStreamConstants.START_ELEMENT);
			// and should have closing </mc> at this point
			if (!"mc".equals(parser.getLocalName())) {
				throw new IllegalStateException("Expected closing </mc>, got </"+parser.getLocalName()+">");
			}
			parser.close();
			return new MediaContent(media, images);
		}

		private Image readImage (XMLStreamReader parser) throws Exception
		{
			Image image = new Image();
			image.uri = readElement(parser, FIELD_NAME_URI);
			image.title = readElement(parser, FIELD_NAME_TITLE);
			image.width = Integer.parseInt(readElement(parser, FIELD_NAME_WIDTH));
			image.height = Integer.parseInt(readElement(parser, FIELD_NAME_HEIGHT));
			image.size = Image.Size.valueOf(readElement(parser, FIELD_NAME_SIZE));
			// need to match close tag
			if (parser.nextTag() != XMLStreamConstants.END_ELEMENT) {
				throw new IllegalStateException("Expected closing </"+FIELD_NAME_IMAGES+">");
			}
			return image;
		}

		private Media readMedia (XMLStreamReader parser) throws Exception
		{
			Media media = new Media();
			media.player = Media.Player.valueOf(readElement(parser, FIELD_NAME_PLAYER));
			media.uri = readElement(parser, FIELD_NAME_URI);
			media.title = readElementMaybe(parser, FIELD_NAME_TITLE);
			media.width = Integer.parseInt(readElement(parser, FIELD_NAME_WIDTH));
			media.height = Integer.parseInt(readElement(parser, FIELD_NAME_HEIGHT));
			media.format = readElement(parser, FIELD_NAME_FORMAT);
			media.duration = Long.parseLong(readElement(parser, FIELD_NAME_DURATION));
			media.size = Long.parseLong(readElement(parser, FIELD_NAME_SIZE));
			String bitrateString = readElement(parser, FIELD_NAME_BITRATE);
			if (bitrateString != null) {
				media.hasBitrate = true;
				media.bitrate = Integer.parseInt(bitrateString);
			}
			media.copyright = readElementMaybe(parser, FIELD_NAME_COPYRIGHT);

			searchTag(parser, FIELD_NAME_PERSONS);
			List<String> persons = new ArrayList<String>();
			do {
				persons.add(parser.getElementText());
			} while (parser.nextTag() == XMLStreamConstants.START_ELEMENT
							 && FIELD_NAME_PERSONS.equals(parser.getLocalName()));
			if (!"md".equals(parser.getLocalName())) {
				throw new IllegalStateException("Expected closing </md>, got </"+parser.getLocalName()+">");
			}
			media.persons = persons;
			return media;
		}

		private String readElementMaybe(XMLStreamReader parser, String string) throws XMLStreamException
		{
			while (true) {
				if (parser.nextTag() == XMLStreamConstants.START_ELEMENT) {
					if (parser.getLocalName().equals(string)) {
						return parser.getElementText();
					} else {
						return null;
					}
				}
			}
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

		// Serialization

		public byte[] serialize(MediaContent content) throws Exception
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			XMLStreamWriter writer = handler.outFactory.createXMLStreamWriter(baos, "UTF-8");
			writer.writeStartDocument("UTF-8", "1.0");
			writer.writeStartElement("mc");
			writeMedia(writer, content.media);
			for (Image image : content.images) {
				writeImage(writer, image);
			}
			writer.writeEndElement();
			writer.writeEndDocument();
			writer.flush();
			writer.close();
			return baos.toByteArray();
		}

		private void writeImage (XMLStreamWriter writer, Image image) throws XMLStreamException
		{
			writer.writeStartElement(FIELD_NAME_IMAGES);
			writeElement(writer, FIELD_NAME_URI, image.uri);
			if (image.title != null) writeElement(writer, FIELD_NAME_TITLE, image.title);
			writeElement(writer, FIELD_NAME_WIDTH, String.valueOf(image.width));
			writeElement(writer, FIELD_NAME_HEIGHT, String.valueOf(image.height));
			writeElement(writer, FIELD_NAME_SIZE, image.size.name());
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
			writeElement(writer, FIELD_NAME_PLAYER, media.player.name());
			writeElement(writer, FIELD_NAME_URI, media.uri);
			if (media.title != null) writeElement(writer, FIELD_NAME_TITLE, media.title);
			writeElement(writer, FIELD_NAME_WIDTH, String.valueOf(media.width));
			writeElement(writer, FIELD_NAME_HEIGHT, String.valueOf(media.height));
			writeElement(writer, FIELD_NAME_FORMAT, media.format);
			writeElement(writer, FIELD_NAME_DURATION, String.valueOf(media.duration));
			writeElement(writer, FIELD_NAME_SIZE, String.valueOf(media.size));
			if (media.hasBitrate) writeElement(writer, FIELD_NAME_BITRATE, String.valueOf(media.bitrate));
			if (media.copyright != null) writeElement(writer, FIELD_NAME_COPYRIGHT, media.copyright);
			for (String person : media.persons) {
					writeElement(writer, FIELD_NAME_PERSONS, person);
			}
			writer.writeEndElement();
		}
	}
}
