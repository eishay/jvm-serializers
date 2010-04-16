package serializers;

import data.media.*;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.util.Utf8;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AvroGeneric
{
	public static void register(TestGroups groups)
	{
		groups.media.add(MediaTransformer, new GenericSerializer(Avro.Media.sMediaContent));
	}

	// ------------------------------------------------------------
	// Serializer (just one class)

	private static final DecoderFactory FACTORY = DecoderFactory.defaultFactory();

	public static class GenericSerializer extends Serializer<GenericRecord>
	{
		public String getName() { return "avro-generic"; }

		private final GenericDatumWriter<GenericRecord> WRITER;
		private final GenericDatumReader<GenericRecord> READER;

		public GenericSerializer(Schema schema)
		{
			WRITER = new GenericDatumWriter<GenericRecord>(schema);
			READER = new GenericDatumReader<GenericRecord>(schema);
		}

		public GenericRecord deserialize(byte[] array) throws Exception
		{
			return READER.read(null, FACTORY.createBinaryDecoder(array, null));
		}

		public byte[] serialize(GenericRecord data) throws IOException
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			WRITER.write(data, new BinaryEncoder(out));
			return out.toByteArray();
		}
	}

	// ------------------------------------------------------------
	// MediaTransformer

	public static final Schema sMediaContent = serializers.avro.media.MediaContent.SCHEMA$;

	public static final Transformer<MediaContent,GenericRecord> MediaTransformer = new Transformer<MediaContent,GenericRecord>()
	{
		private final Schema sImage = serializers.avro.media.Image.SCHEMA$;
		private final Schema sMedia = serializers.avro.media.Media.SCHEMA$;

		private final Schema sImages = sMediaContent.getField("images").schema();
		private final Schema sPersons = sMedia.getField("persons").schema();

		// ----------------------------------------------------------
		// Forward

		public GenericRecord forward(MediaContent mc)
		{
			GenericRecord content = new GenericData.Record(Avro.Media.sMediaContent);

			content.put("media", forwardMedia(mc.media));

			GenericData.Array<GenericRecord> images = new GenericData.Array<GenericRecord>(mc.images.size(), Avro.Media.sImages);
			for (Image image : mc.images) {
				images.add(forwardImage(image));
			}
			content.put("images", images);

			return content;
		}

		private GenericRecord forwardMedia(Media media)
		{
			GenericRecord m = new GenericData.Record(Avro.Media.sMedia);
			m.put("uri", new Utf8(media.uri));
			m.put("format", new Utf8(media.format));
			if (media.title != null) {
				m.put("title", new Utf8(media.title));
			}
			m.put("duration", media.duration);
			if (media.hasBitrate) {
				m.put("bitrate", media.bitrate);
			}

			GenericData.Array<Utf8> persons =  new GenericData.Array<Utf8>(media.persons.size(), Avro.Media.sPersons);
			for (String p : media.persons) {
				persons.add(new Utf8(p));
			}

			m.put("persons", persons);
			m.put("player", forwardPlayer(media.player));
			m.put("height", media.height);
			m.put("width", media.width);
			m.put("size", media.size);
			if (media.copyright != null) {
				m.put("copyright", new Utf8(media.copyright));
			}

			return m;
		}

		public int forwardPlayer(Media.Player p)
		{
			switch (p) {
				case JAVA: return 1;
				case FLASH: return 2;
				default:
					throw new AssertionError("invalid case: " + p);
			}
		}

		private GenericRecord forwardImage(Image image)
		{
			GenericRecord i = new GenericData.Record(Avro.Media.sImage);
			i.put("uri", new Utf8(image.uri));
			i.put("width", image.width);
			i.put("height", image.height);
			i.put("size", forwardSize(image.size));
			if (image.title != null) {
				i.put("title", new Utf8(image.title));
			}
			return i;
		}

		public int forwardSize(Image.Size s)
		{
			switch (s) {
				case SMALL: return 1;
				case LARGE: return 2;
				default:
					throw new AssertionError("invalid case: " + s);
			}
		}

		// ----------------------------------------------------------
		// Reverse

		public MediaContent reverse(GenericRecord mc)
		{
			@SuppressWarnings("unchecked")
			GenericData.Array<GenericRecord> gimages = (GenericData.Array<GenericRecord>) mc.get("images");
			List<Image> images = new ArrayList<Image>((int) gimages.size());
			for (GenericRecord image : gimages) {
				images.add(reverseImage(image));
			}

			return new MediaContent(reverseMedia((GenericRecord) mc.get("media")), images);
		}

		private Media reverseMedia(GenericRecord media)
		{
			// Media

			@SuppressWarnings("unchecked")
			GenericData.Array<Utf8> gpersons = (GenericData.Array<Utf8>) media.get("persons");
			List<String> persons = new ArrayList<String>((int) gpersons.size());
			for (Utf8 person : gpersons) {
				persons.add(person.toString());
			}

			// Optional fields.
			Utf8 title = (Utf8) media.get("title");
			Integer bitrate = (Integer) media.get("bitrate");
			Utf8 copyright = (Utf8) media.get("copyright");

			return new Media(
				((Utf8) media.get("uri")).toString(),
				title != null ? title.toString() : null,
				(Integer) media.get("width"),
				(Integer) media.get("height"),
				((Utf8) media.get("format")).toString(),
				(Long) media.get("duration"),
				(Long) media.get("size"),
				bitrate != null ? bitrate : 0,
				bitrate != null,
				persons,
				reversePlayer((Integer) media.get("player")),
				copyright != null ? copyright.toString() : null
			);
		}

		public Media.Player reversePlayer(int p)
		{
			switch (p) {
				case 1: return Media.Player.JAVA;
				case 2: return Media.Player.FLASH;
				default: throw new AssertionError("invalid case: " + p);
			}
		}

		private Image reverseImage(GenericRecord image)
		{
			Utf8 title = (Utf8) image.get("title");
			return new Image(
				((Utf8) image.get("uri")).toString(),
				title == null ? null : title.toString(),
				(Integer) image.get("width"),
				(Integer) image.get("height"),
				reverseSize((Integer) image.get("size")));
		}

		public Image.Size reverseSize(int s)
		{
			switch (s) {
				case 1: return Image.Size.SMALL;
				case 2: return Image.Size.LARGE;
				default: throw new AssertionError("invalid case: " + s);
			}
		}

		public MediaContent shallowReverse(GenericRecord mc)
		{
			return new MediaContent(reverseMedia((GenericRecord) mc.get("media")), Collections.<Image>emptyList());
		}
	};
}
