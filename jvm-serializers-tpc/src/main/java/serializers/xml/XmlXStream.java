package serializers.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;
import data.media.Media.Player;
import data.media.Image.Size;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import serializers.*;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.io.xml.CompactWriter;

@SuppressWarnings("rawtypes")
public class XmlXStream
{
	public static void register(TestGroups groups)
	{
		// The default XStream serializer.
        // commented-out by dyu: the perf of the default sux.
		/*groups.media.add(JavaBuiltIn.MediaTransformer, new ConverterSerializer<MediaContent>("xml/xstream",
			new com.thoughtworks.xstream.XStream(new XppDriver() {
				public HierarchicalStreamWriter createWriter(Writer out) {
					//return new PrettyPrintWriter(out, xmlFriendlyReplacer());
					return new CompactWriter(out, xmlFriendlyReplacer());
				}
			}), EmptyConfiguration));*/

		groups.media.add(JavaBuiltIn.mediaTransformer, new ConverterSerializer<MediaContent>("xml/xstream+c",
			new com.thoughtworks.xstream.XStream(new XppDriver() {
				public HierarchicalStreamWriter createWriter(Writer out) {
					//return new PrettyPrintWriter(out, xmlFriendlyReplacer());
					return new CompactWriter(out, xmlFriendlyReplacer());
				}
			}), MediaConfiguration),
                new SerFeatures(
                        SerFormat.XML,
                        SerGraph.FLAT_TREE,
                        SerClass.ZERO_KNOWLEDGE,
                        ""
                )
        );

        // commented-out by dyu: use the non-abbreviated version
		/*groups.media.add(JavaBuiltIn.MediaTransformer, new ConverterSerializer<MediaContent>("xml/xstream+c-abbrev",
			new com.thoughtworks.xstream.XStream(new XppDriver() {
				public HierarchicalStreamWriter createWriter(Writer out) {
					//return new PrettyPrintWriter(out, xmlFriendlyReplacer());
					return new CompactWriter(out, xmlFriendlyReplacer());
				}
			}), MediaConfigurationAbbreviated));*/

		// Adapt each of the STAX handlers to use XStream
		for (XmlStax.Handler h : XmlStax.HANDLERS) {
			// TODO: This doesn't work yet.  Need to properly handle optional fields in readMedia/readImage.
            // commented-out by dyu: use the non-abbreviated version (+c) because the perf of the default sux.
			//groups.media.add(JavaBuiltIn.MediaTransformer, XStream.<MediaContent>mkStaxSerializer(h, "",  EmptyConfiguration));
			groups.media.add(JavaBuiltIn.mediaTransformer, XmlXStream.<MediaContent>mkStaxSerializer(h, "+c", MediaConfiguration),
                    new SerFeatures(
                            SerFormat.XML,
                            SerGraph.FLAT_TREE,
                            SerClass.MANUAL_OPT,
                            ""
                    )
            );
			//groups.media.add(JavaBuiltIn.MediaTransformer, XStream.<MediaContent>mkStaxSerializer(h, "+c-abbrev", MediaConfigurationAbbreviated));
		}
	}

	private static <T> ConverterSerializer<T> mkStaxSerializer(final XmlStax.Handler handler, String suffix, Configuration config)
	{
		return new ConverterSerializer<T>("xml/xstream" + suffix + "-" + handler.name,
			new com.thoughtworks.xstream.XStream(new StaxDriver() {
				public XMLInputFactory getInputFactory() { return handler.inFactory; }
				public XMLOutputFactory getOutputFactory() { return handler.outFactory; }
			}), config);
	}

	public static final class ConverterSerializer<T> extends Serializer<T>
	{
		private String name;
		private com.thoughtworks.xstream.XStream xstream;

		public ConverterSerializer(String name, com.thoughtworks.xstream.XStream xstream, Configuration config)
		{
			this.name = name;
			this.xstream = xstream;

			config.run(xstream);
		}

		public String getName() { return name; }

		public T deserialize(byte[] array) throws Exception
		{
			@SuppressWarnings("unchecked")
			T value = (T) xstream.fromXML(new ByteArrayInputStream(array));
			return value;
		}

		public byte[] serialize(T content) throws IOException
		{
			ByteArrayOutputStream baos = outputStream(content);
			xstream.toXML(content, baos);
			return baos.toByteArray();
		}
	}

	public static abstract class Configuration
	{
		public abstract void run(com.thoughtworks.xstream.XStream xstream);
	}

	// -----------------------------------------------------------------------
	// Serializer: Media

	public static final Configuration EmptyConfiguration = new Configuration()
	{
		public void run(com.thoughtworks.xstream.XStream xstream) {}
	};

	public static final Configuration MediaConfiguration = new MediaConfiguration();
	public static class MediaConfiguration extends Configuration
	{
		public void run(com.thoughtworks.xstream.XStream xstream)
		{
			xstream.alias("Image", Image.class);
			xstream.registerConverter(new ImageConverter());

			xstream.alias("Media", Media.class);
			xstream.registerConverter(new MediaConverter());

			xstream.alias("MediaContent", MediaContent.class);
			xstream.registerConverter(new MediaContentConverter());
		}

		private static class MediaContentConverter implements Converter
		{
			public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context)
			{
				MediaContent content = (MediaContent) obj;
				writer.startNode("Media");
				context.convertAnother(content.media);
				writer.endNode();
				for (Image image : content.images) {
					writer.startNode("Image");
					context.convertAnother(image);
					writer.endNode();
				}
			}

			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
			{
				reader.moveDown();
				Media media = (Media) context.convertAnother(null, Media.class);
				reader.moveUp();

				List<Image> images = new ArrayList<Image>();
				while (reader.hasMoreChildren()) {
					reader.moveDown();
					images.add((Image) context.convertAnother(images, Image.class));
					reader.moveUp();
				}

				return new MediaContent(media, images);
			}

			@Override
			public boolean canConvert(Class arg0)
			{
				return MediaContent.class.equals(arg0);
			}
		}

		private static class ImageConverter implements Converter
		{
			public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context)
			{
				Image image = (Image) obj;
				writer.addAttribute("uri", image.uri);
				if (image.title != null) writer.addAttribute("title", image.title);
				writer.addAttribute("width", String.valueOf(image.width));
				writer.addAttribute("height", String.valueOf(image.height));
				writer.addAttribute("size", String.valueOf(image.size.ordinal()));
			}

			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
			{
				Image image = new Image();
				image.uri = reader.getAttribute("uri");
				image.title = reader.getAttribute("title");
				image.width = Integer.valueOf(reader.getAttribute("width"));
				image.height = Integer.valueOf(reader.getAttribute("height"));
				image.size = Size.values()[Integer.valueOf(reader.getAttribute("size"))];
				return image;
			}

                        @Override
			public boolean canConvert(Class arg0)
			{
				return Image.class.equals(arg0);
			}
		}

		private static class MediaConverter implements Converter
		{
			public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context)
			{
				Media media = (Media) obj;
				writer.addAttribute("player", String.valueOf(media.player.ordinal()));
				writer.addAttribute("uri", media.uri);
				if (media.title != null) writer.addAttribute("title", media.title);
				writer.addAttribute("width", String.valueOf(media.width));
				writer.addAttribute("height", String.valueOf(media.height));
				writer.addAttribute("format", media.format);
				writer.addAttribute("duration", String.valueOf(media.duration));
				writer.addAttribute("size", String.valueOf(media.size));
				if (media.hasBitrate) writer.addAttribute("bitrate", String.valueOf(media.bitrate));
				if (media.copyright != null) writer.addAttribute("copyright", media.copyright);
				for (String p : media.persons)
				{
					writer.startNode("person");
					writer.setValue(p);
					writer.endNode();
				}
			}

			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
			{
				Media media = new Media();
				media.player = Player.values()[Integer.valueOf(reader.getAttribute("player"))];
				media.uri = reader.getAttribute("uri");
				media.title = reader.getAttribute("title");
				media.width = Integer.valueOf(reader.getAttribute("width"));
				media.height = Integer.valueOf(reader.getAttribute("height"));
				media.format = reader.getAttribute("format");
				media.duration = Long.valueOf(reader.getAttribute("duration"));
				media.size = Long.valueOf(reader.getAttribute("size"));
				String stringBitrate = reader.getAttribute("bitrate");
				if (stringBitrate != null) {
					media.hasBitrate = true;
					media.bitrate = Integer.valueOf(stringBitrate);
				}
				media.copyright = reader.getAttribute("copyright");
				List<String> persons = new ArrayList<String>();
				while (reader.hasMoreChildren())
				{
					reader.moveDown();
					persons.add(reader.getValue());
					reader.moveUp();
				}
				media.persons = persons;
				return media;
			}

                        @Override
			public boolean canConvert(Class arg0)
			{
				return Media.class.equals(arg0);
			}
		}
	};

	public static final Configuration MediaConfigurationAbbreviated = new MediaConfigurationAbbreviated();
	public static class MediaConfigurationAbbreviated extends Configuration
	{
		public void run(com.thoughtworks.xstream.XStream xstream)
		{
			xstream.alias("im", Image.class);
			xstream.registerConverter(new ImageConverter());

			xstream.alias("md", Media.class);
			xstream.registerConverter(new MediaConverter());

			xstream.alias("mc", MediaContent.class);
			xstream.registerConverter(new MediaContentConverter());
		}

		private static class MediaContentConverter implements Converter
		{
			public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context)
			{
				MediaContent content = (MediaContent) obj;
				writer.startNode("md");
				context.convertAnother(content.media);
				writer.endNode();
				for (Image image : content.images) {
					writer.startNode("im");
					context.convertAnother(image);
					writer.endNode();
				}
			}

			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
			{
				reader.moveDown();
				Media media = (Media) context.convertAnother(null, Media.class);
				reader.moveUp();

				List<Image> images = new ArrayList<Image>();
				while (reader.hasMoreChildren()) {
					reader.moveDown();
					images.add((Image) context.convertAnother(images, Image.class));
					reader.moveUp();
				}

				return new MediaContent(media, images);
			}

                        @Override
			public boolean canConvert(Class arg0)
			{
				return MediaContent.class.equals(arg0);
			}
		}

		private static class ImageConverter implements Converter
		{
			public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context)
			{
				Image image = (Image) obj;
				writer.addAttribute("ul", image.uri);
				if (image.title != null) writer.addAttribute("tl", image.title);
				writer.addAttribute("wd", String.valueOf(image.width));
				writer.addAttribute("hg", String.valueOf(image.height));
				writer.addAttribute("sz", String.valueOf(image.size.ordinal()));
			}

			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
			{
				Image image = new Image();
				image.uri = reader.getAttribute("ul");
				image.title = reader.getAttribute("tl");
				image.width = Integer.valueOf(reader.getAttribute("wd"));
				image.height = Integer.valueOf(reader.getAttribute("hg"));
				image.size = Size.values()[Integer.valueOf(reader.getAttribute("sz"))];
				return image;
			}

                        @Override
			public boolean canConvert(Class arg0)
			{
				return Image.class.equals(arg0);
			}

		}

		private static class MediaConverter implements Converter
		{
			public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context)
			{
				Media media = (Media) obj;
				writer.addAttribute("pl", String.valueOf(media.player.ordinal()));
				writer.addAttribute("ul", media.uri);
				if (media.title != null) writer.addAttribute("tl", media.title);
				writer.addAttribute("wd", String.valueOf(media.width));
				writer.addAttribute("hg", String.valueOf(media.height));
				writer.addAttribute("fr", media.format);
				writer.addAttribute("dr", String.valueOf(media.duration));
				writer.addAttribute("sz", String.valueOf(media.size));
				if (media.hasBitrate) writer.addAttribute("br", String.valueOf(media.bitrate));
				if (media.copyright != null) writer.addAttribute("cp", media.copyright);
				for (String p : media.persons)
				{
					writer.startNode("pr");
					writer.setValue(p);
					writer.endNode();
				}
			}

			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
			{
				Media media = new Media();
				media.player = Player.values()[Integer.valueOf(reader.getAttribute("pl"))];
				media.uri = reader.getAttribute("ul");
				media.title = reader.getAttribute("tl");
				media.width = Integer.valueOf(reader.getAttribute("wd"));
				media.height = Integer.valueOf(reader.getAttribute("hg"));
				media.format = reader.getAttribute("fr");
				media.duration = Long.valueOf(reader.getAttribute("dr"));
				media.size = Long.valueOf(reader.getAttribute("sz"));
				String stringBitrate = reader.getAttribute("br");
				if (stringBitrate != null) {
					media.hasBitrate = true;
					media.bitrate = Integer.valueOf(stringBitrate);
				}
				media.copyright = reader.getAttribute("cp");
				List<String> persons = new ArrayList<String>();
				while (reader.hasMoreChildren())
				{
					reader.moveDown();
					persons.add(reader.getValue());
					reader.moveUp();
				}
				media.persons = persons;
				return media;
			}

                        @Override
			public boolean canConvert(Class arg0)
			{
				return Media.class.equals(arg0);
			}
		}
	};
}
