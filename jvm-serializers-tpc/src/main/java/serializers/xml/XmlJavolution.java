package serializers.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import serializers.*;

import javolution.text.CharArray;
import javolution.xml.XMLBinding;
import javolution.xml.XMLFormat;
import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;
import javolution.xml.stream.XMLStreamException;

import data.media.*;
import static data.media.FieldMapping.*;

public class XmlJavolution
{
    public final static String ROOT_ELEMENT = "mc";

	public static void register(TestGroups groups)
	{
		groups.media.add(JavaBuiltIn.mediaTransformer, new JavolutionSerializer<MediaContent>(MediaBinding, MediaContent.class),
                new SerFeatures(
                        SerFormat.XML,
                        SerGraph.FLAT_TREE,
                        SerClass.MANUAL_OPT,
                        ""
                )
        );
        // commented-out by dyu: use the non-abbreviated version
		//groups.media.add(JavaBuiltIn.MediaTransformer, new JavolutionSerializer<MediaContent>("-abbrev", MediaBindingAbbreviated, MediaContent.class));
	}

	private static final class JavolutionSerializer<T> extends Serializer<T>
	{
		private final XMLBinding binding;
		private final Class<T> clazz;

		public JavolutionSerializer(XMLBinding binding, Class<T> clazz)
		{
			this.binding = binding;
			this.clazz = clazz;
		}

		@Override
		public String getName() { return "xml/javolution/manual"; }

          @Override
		public T deserialize(byte[] array) throws Exception
		{
			XMLObjectReader reader = XMLObjectReader.newInstance(new ByteArrayInputStream(array)).setBinding(binding);
			try {
				return reader.read(ROOT_ELEMENT, clazz);
			} finally {
				reader.close();
			}
		}

          @Override
		public byte[] serialize(T content) throws Exception
		{
			ByteArrayOutputStream baos = outputStream(content);
			XMLObjectWriter writer = XMLObjectWriter.newInstance(baos).setBinding(binding);
			writer.write(content, ROOT_ELEMENT, clazz);
			writer.close();
			baos.close();
			return baos.toByteArray();
		}
	}

	@SuppressWarnings("serial")
	private static final XMLBinding MediaBinding = new XMLBinding()
	{
		{
			setAlias(MediaContent.class, ROOT_ELEMENT);
			setAlias(Image.class, FULL_FIELD_NAME_IMAGES);
			setAlias(Media.class, FULL_FIELD_NAME_MEDIA);
			setAlias(String.class, "str");
		}

		private <G,S> XMLFormat<G> toGeneric(XMLFormat<S> specific)
		{
			@SuppressWarnings("unchecked")
			XMLFormat<G> generic = (XMLFormat<G>) specific;
			return generic;
		}

		// silly javolution does not use generics properly....
		@SuppressWarnings("unchecked")
		@Override
		public XMLFormat getFormat(Class cls) throws XMLStreamException
		{
			if (MediaContent.class.equals(cls))
				return toGeneric(MediaContentConverter);
			if (Media.class.equals(cls))
				return toGeneric(MediaConverter);
			if (Image.class.equals(cls))
				return toGeneric(ImageConverter);
			return super.getFormat(cls);
		}

		private final XMLFormat<Image> ImageConverter = new XMLFormat<Image>(null)
		{
			@Override
			public void write(Image image, XMLFormat.OutputElement xml) throws XMLStreamException
			{
				xml.setAttribute(FULL_FIELD_NAME_URI, image.uri);
				xml.setAttribute(FULL_FIELD_NAME_TITLE, image.title);
				xml.setAttribute(FULL_FIELD_NAME_WIDTH, image.width);
				xml.setAttribute(FULL_FIELD_NAME_HEIGHT, image.height);
				xml.setAttribute(FULL_FIELD_NAME_SIZE, image.size.ordinal());
			}

			@Override
			public void read(XMLFormat.InputElement xml, Image image) throws XMLStreamException
			{
				image.uri = xml.getAttribute(FULL_FIELD_NAME_URI).toString();
				image.title = xml.getAttribute(FULL_FIELD_NAME_TITLE, (String) null);
				image.width = xml.getAttribute(FULL_FIELD_NAME_WIDTH).toInt();
				image.height = xml.getAttribute(FULL_FIELD_NAME_HEIGHT).toInt();
				image.size = Image.Size.values()[xml.getAttribute(FULL_FIELD_NAME_SIZE, 0)];
			}
		};

		private final XMLFormat<Media> MediaConverter = new XMLFormat<Media>(null)
		{
			@Override
			public void write(Media media, XMLFormat.OutputElement xml) throws XMLStreamException
			{
				xml.setAttribute(FULL_FIELD_NAME_URI, media.uri);
				xml.setAttribute(FULL_FIELD_NAME_TITLE, media.title);
				xml.setAttribute(FULL_FIELD_NAME_WIDTH, media.width);
				xml.setAttribute(FULL_FIELD_NAME_HEIGHT, media.height);
				xml.setAttribute(FULL_FIELD_NAME_FORMAT, media.format);
				xml.setAttribute(FULL_FIELD_NAME_DURATION, media.duration);
				xml.setAttribute(FULL_FIELD_NAME_SIZE, media.size);
				if (media.hasBitrate) xml.setAttribute(FULL_FIELD_NAME_BITRATE, media.bitrate);
				xml.setAttribute(FULL_FIELD_NAME_PLAYER, media.player.ordinal());
				xml.setAttribute(FULL_FIELD_NAME_COPYRIGHT, media.copyright);

				for (String p : media.persons) {
					xml.add(p);
				}
			}

			@Override
			public void read(XMLFormat.InputElement xml, Media media) throws XMLStreamException
			{
				media.uri = xml.getAttribute(FULL_FIELD_NAME_URI).toString();
				media.title = xml.getAttribute(FULL_FIELD_NAME_TITLE, (String) null);
				media.width = xml.getAttribute(FULL_FIELD_NAME_WIDTH).toInt();
				media.height = xml.getAttribute(FULL_FIELD_NAME_HEIGHT).toInt();
				media.format = xml.getAttribute(FULL_FIELD_NAME_FORMAT).toString();
				media.duration = xml.getAttribute(FULL_FIELD_NAME_DURATION).toLong();
				media.size = xml.getAttribute(FULL_FIELD_NAME_SIZE).toLong();
				CharArray caBitrate = xml.getAttribute(FULL_FIELD_NAME_BITRATE);
				media.hasBitrate = (caBitrate != null);
				if (caBitrate != null)  media.bitrate = caBitrate.toInt();
				media.player = Media.Player.values()[xml.getAttribute(FULL_FIELD_NAME_PLAYER, 0)];
				media.copyright = xml.getAttribute(FULL_FIELD_NAME_COPYRIGHT, (String) null);

				List<String> persons = new ArrayList<String>();
				while (xml.hasNext()) {
					persons.add((String)xml.getNext());
				}
				media.persons = persons;
			}
		};

		private final XMLFormat<MediaContent> MediaContentConverter = new XMLFormat<MediaContent>(null)
		{
			@Override
			public void write(MediaContent content, XMLFormat.OutputElement xml) throws XMLStreamException
			{
				xml.add(content.media);
				for (Image image : content.images) {
					xml.add(image);
				}
			}

			@Override
			public MediaContent newInstance(java.lang.Class<MediaContent> cls, XMLFormat.InputElement xml) throws XMLStreamException
			{
				Media media = (Media) xml.getNext();
				List<Image> images = new ArrayList<Image>();
				while (xml.hasNext()) {
					images.add((Image) xml.getNext());
				}
				return new MediaContent(media, images);
			}

			@Override
			public void read(javolution.xml.XMLFormat.InputElement arg0, MediaContent arg1)
			{
				// Do nothing; Object loaded by newInstance;
			}
		};
	};
}
