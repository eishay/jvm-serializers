package serializers;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericData;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.util.Utf8;
import serializers.avro.media.*;

public class AvroSpecific
{
	public static void register(TestGroups groups)
	{
		groups.media.add(MediaTransformer, new GenericSerializer<MediaContent>(MediaContent.class));
	}

	// ------------------------------------------------------------
	// Serializers

	private static final DecoderFactory FACTORY = DecoderFactory.defaultFactory();

	public static final class GenericSerializer<T> extends Serializer<T>
	{
		public String getName() { return "avro"; }

		private final SpecificDatumReader<T> READER;
		private final SpecificDatumWriter<T> WRITER;

		public GenericSerializer(Class<T> clazz)
		{
			this.READER = new SpecificDatumReader<T>(clazz);
			this.WRITER = new SpecificDatumWriter<T>(clazz);
		}


		public T deserialize(byte[] array) throws Exception {
			return READER.read(null, FACTORY.createBinaryDecoder(array, null));
		}

		public byte[] serialize(T content) throws Exception {
			ByteArrayOutputStream out = outputStream(content);
			WRITER.write(content, new BinaryEncoder(out));
			return out.toByteArray();
		}
	}

	// ------------------------------------------------------------
	// Transformers

	public static final Transformer<data.media.MediaContent,MediaContent> MediaTransformer = new Transformer<data.media.MediaContent,MediaContent>()
	{
		// ----------------------------------------------------------
		// Forward

		public MediaContent forward(data.media.MediaContent mc)
		{
			GenericArray<Image> images = new GenericData.Array<Image>(mc.images.size(), Avro.Media.sImages);
			for (data.media.Image image : mc.images) {
				images.add(forwardImage(image));
			}

			MediaContent amc = new MediaContent();
			amc.media = forwardMedia(mc.media);
			amc.images = images;
			return amc;
		}

		private Media forwardMedia(data.media.Media media)
		{
			Media m = new Media();

			m.uri = new Utf8(media.uri);
			m.title = media.title != null ? new Utf8(media.title) : null;
			m.width = media.width;
			m.height = media.height;
			m.format = new Utf8(media.format);
			m.duration = media.duration;
			m.size = media.size;
			if (media.hasBitrate) m.bitrate = media.bitrate;

			GenericArray<Utf8> persons = new GenericData.Array<Utf8>(media.persons.size(), Avro.Media.sPersons);
			for (String s : media.persons) {
				persons.add(new Utf8(s));
			}
			m.persons = persons;

			m.player = forwardPlayer(media.player);
			if (media.copyright != null) m.copyright = new Utf8(media.copyright);

			return m;
		}

		public int forwardPlayer(data.media.Media.Player p)
		{
			switch (p) {
				case JAVA: return 1;
				case FLASH: return 2;
				default: throw new AssertionError("invalid case: " + p);
			}
		}

		private Image forwardImage(data.media.Image image)
		{
			Image i = new Image();
			i.uri = new Utf8(image.uri);
			if (image.title != null) i.title = new Utf8(image.title);
			i.width = image.width;
			i.height = image.height;
			i.size = forwardSize(image.size);
			return i;
		}

		public int forwardSize(data.media.Image.Size s)
		{
			switch (s) {
				case SMALL: return 1;
				case LARGE: return 2;
				default: throw new AssertionError("invalid case: " + s);
			}
		}

		// ----------------------------------------------------------
		// Reverse

		public data.media.MediaContent reverse(MediaContent mc)
		{
			List<data.media.Image> images = new ArrayList<data.media.Image>((int) mc.images.size());

			for (Image image : mc.images) {
				images.add(reverseImage(image));
			}

			return new data.media.MediaContent(reverseMedia(mc.media), images);
		}

		private data.media.Media reverseMedia(Media media)
		{
			// Media
			List<String> persons = new ArrayList<String>();
			for (Utf8 p : media.persons) {
				persons.add(p.toString());
			}

			return new data.media.Media(
				media.uri.toString(),
				media.title != null ? media.title.toString() : null,
				media.width,
				media.height,
				media.format.toString(),
				media.duration,
				media.size,
				media.bitrate != null ? media.bitrate : 0,
				media.bitrate != null,
				persons,
				reversePlayer(media.player),
				media.copyright != null ? media.copyright.toString() : null
			);
		}

		public data.media.Media.Player reversePlayer(int p)
		{
			switch (p) {
				case 1: return data.media.Media.Player.JAVA;
				case 2: return data.media.Media.Player.FLASH;
				default: throw new AssertionError("invalid case: " + p);
			}
		}

		private data.media.Image reverseImage(Image image)
		{
			return new data.media.Image(
				image.uri.toString(),
				image.title != null ? image.title.toString() : null,
				image.width,
				image.height,
				reverseSize(image.size));
		}

		public data.media.Image.Size reverseSize(int s)
		{
			switch (s) {
				case 1: return data.media.Image.Size.SMALL;
				case 2: return data.media.Image.Size.LARGE;
				default: throw new AssertionError("invalid case: " + s);
			}
		}

		public data.media.MediaContent shallowReverse(MediaContent mc)
		{
			return new data.media.MediaContent(reverseMedia(mc.media), Collections.<data.media.Image>emptyList());
		}
	};
}
