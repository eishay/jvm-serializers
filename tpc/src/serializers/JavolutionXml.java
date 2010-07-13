package serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javolution.text.CharArray;
import javolution.xml.XMLBinding;
import javolution.xml.XMLFormat;
import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;
import javolution.xml.stream.XMLStreamException;

import data.media.*;

public class JavolutionXml
{
	public static void register(TestGroups groups)
	{
		groups.media.add(JavaBuiltIn.MediaTransformer, new JavolutionSerializer<MediaContent>("", MediaBinding, MediaContent.class));
		groups.media.add(JavaBuiltIn.MediaTransformer, new JavolutionSerializer<MediaContent>("-abbrev", MediaBindingAbbreviated, MediaContent.class));
	}

	private static final class JavolutionSerializer<T> extends Serializer<T>
	{
		private final String append;
		private final XMLBinding binding;
		private final Class<T> clazz;

		public JavolutionSerializer(String append, XMLBinding binding, Class<T> clazz)
		{
			this.append = append;
			this.binding = binding;
			this.clazz = clazz;
		}

		public String getName() { return "xml/javolution" + append; }

		public T deserialize(byte[] array)
			throws Exception
		{
			XMLObjectReader reader = XMLObjectReader.newInstance(new ByteArrayInputStream(array)).setBinding(binding);
			try {
				return reader.read("mc", clazz);
			} finally {
				reader.close();
			}
		}

		public byte[] serialize(T content)
			throws Exception
		{
			ByteArrayOutputStream baos = outputStream(content);
			XMLObjectWriter writer = XMLObjectWriter.newInstance(baos).setBinding(binding);
			writer.write(content, "mc", clazz);
			writer.close();
			return baos.toByteArray();
		}
	}

	private static final XMLBinding MediaBinding = new XMLBinding()
	{
		{
			setAlias(MediaContent.class, "mc");
			setAlias(Image.class, "im");
			setAlias(Media.class, "me");
			setAlias(String.class, "str");
		}

		private <G,S> XMLFormat<G> toGeneric(XMLFormat<S> specific)
		{
			@SuppressWarnings("unchecked")
			XMLFormat<G> generic = (XMLFormat<G>) specific;
			return generic;
		}

		public <T> XMLFormat<T> getFormat(Class<T> cls)
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
				xml.setAttribute("uri", image.uri);
				xml.setAttribute("title", image.title);
				xml.setAttribute("width", image.width);
				xml.setAttribute("height", image.height);
				xml.setAttribute("size", image.size.ordinal());
			}

			@Override
			public void read(XMLFormat.InputElement xml, Image image) throws XMLStreamException
			{
				image.uri = xml.getAttribute("uri").toString();
				image.title = xml.getAttribute("title", null);
				image.width = xml.getAttribute("width").toInt();
				image.height = xml.getAttribute("height").toInt();
				image.size = Image.Size.values()[xml.getAttribute("size", 0)];
			}
		};

		private final XMLFormat<Media> MediaConverter = new XMLFormat<Media>(null)
		{
			@Override
			public void write(Media media, XMLFormat.OutputElement xml) throws XMLStreamException
			{
				xml.setAttribute("uri", media.uri);
				xml.setAttribute("title", media.title);
				xml.setAttribute("width", media.width);
				xml.setAttribute("height", media.height);
				xml.setAttribute("format", media.format);
				xml.setAttribute("duration", media.duration);
				xml.setAttribute("size", media.size);
				if (media.hasBitrate) xml.setAttribute("bitrate", media.bitrate);
				xml.setAttribute("player", media.player.ordinal());
				xml.setAttribute("copyright", media.copyright);

				for (String p : media.persons) {
					xml.add(p);
				}
			}

			@Override
			public void read(XMLFormat.InputElement xml, Media media) throws XMLStreamException
			{
				media.uri = xml.getAttribute("uri").toString();
				media.title = xml.getAttribute("title", null);
				media.width = xml.getAttribute("width").toInt();
				media.height = xml.getAttribute("height").toInt();
				media.format = xml.getAttribute("format").toString();
				media.duration = xml.getAttribute("duration").toLong();
				media.size = xml.getAttribute("size").toLong();
				CharArray caBitrate = xml.getAttribute("bitrate");
				media.hasBitrate = (caBitrate != null);
				if (caBitrate != null)  media.bitrate = caBitrate.toInt();
				media.player = Media.Player.values()[xml.getAttribute("player", 0)];
				media.copyright = xml.getAttribute("copyright", null);

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

	private static final XMLBinding MediaBindingAbbreviated = new XMLBinding()
	{
		{
			setAlias(MediaContent.class, "mc");
			setAlias(Image.class, "im");
			setAlias(Media.class, "me");
			setAlias(String.class, "str");
		}

		private <G,S> XMLFormat<G> toGeneric(XMLFormat<S> specific)
		{
			@SuppressWarnings("unchecked")
			XMLFormat<G> generic = (XMLFormat<G>) specific;
			return generic;
		}

		public <T> XMLFormat<T> getFormat(Class<T> cls)
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
				xml.setAttribute("ul", image.uri);
				xml.setAttribute("tl", image.title);
				xml.setAttribute("wd", image.width);
				xml.setAttribute("hg", image.height);
				xml.setAttribute("sz", image.size.ordinal());
			}

			@Override
			public void read(XMLFormat.InputElement xml, Image image) throws XMLStreamException
			{
				image.uri = xml.getAttribute("ul").toString();
				image.title = xml.getAttribute("tl", null);
				image.width = xml.getAttribute("wd").toInt();
				image.height = xml.getAttribute("hg").toInt();
				image.size = Image.Size.values()[xml.getAttribute("sz", 0)];
			}
		};

		private final XMLFormat<Media> MediaConverter = new XMLFormat<Media>(null)
		{
			@Override
			public void write(Media media, XMLFormat.OutputElement xml) throws XMLStreamException
			{
				xml.setAttribute("ul", media.uri);
				xml.setAttribute("tl", media.title);
				xml.setAttribute("wd", media.width);
				xml.setAttribute("hg", media.height);
				xml.setAttribute("fr", media.format);
				xml.setAttribute("dr", media.duration);
				xml.setAttribute("sz", media.size);
				if (media.hasBitrate) xml.setAttribute("br", media.bitrate);
				xml.setAttribute("pl", media.player.ordinal());
				xml.setAttribute("cp", media.copyright);
				
				for (String p : media.persons) {
					xml.add(p);
				}
			}

			@Override
			public void read(XMLFormat.InputElement xml, Media media) throws XMLStreamException
			{
				media.uri = xml.getAttribute("ul").toString();
				media.title = xml.getAttribute("tl", null);
				media.width = xml.getAttribute("wd").toInt();
				media.height = xml.getAttribute("hg").toInt();
				media.format = xml.getAttribute("fr").toString();
				media.duration = xml.getAttribute("dr").toLong();
				media.size = xml.getAttribute("sz").toLong();
				CharArray caBitrate = xml.getAttribute("br");
				media.hasBitrate = (caBitrate != null);
				if (caBitrate != null)  media.bitrate = caBitrate.toInt();
				media.player = Media.Player.values()[xml.getAttribute("pl", 0)];
				media.copyright = xml.getAttribute("cp", null);
				
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
