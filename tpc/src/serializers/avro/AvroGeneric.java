package serializers.avro;

import com.linkedin.avro.fastserde.FastGenericDatumReader;
import com.linkedin.avro.fastserde.FastGenericDatumWriter;
import data.media.*;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.BinaryDecoder;

import serializers.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AvroGeneric
{
	public static void register(TestGroups groups)
	{
		groups.media.add(MediaTransformer, new GenericSerializer(Avro.Media.sMediaContent),
                new SerFeatures(
                        SerFormat.BIN_CROSSLANG,
                        SerGraph.FLAT_TREE,
                        SerClass.CLASSES_KNOWN,
                        "",
												Avro.miscFeatures
                )
        );

		groups.media.add(MediaTransformer, new GenericSerializerWithObjectReuse(Avro.Media.sMediaContent),
								new SerFeatures(
												SerFormat.BIN_CROSSLANG,
												SerGraph.FLAT_TREE,
												SerClass.MANUAL_OPT,
												"",
												Avro.miscFeatures
								)
				);

		groups.media.add(MediaTransformer, new FastGenericSerializer(Avro.Media.sMediaContent),
								new SerFeatures(
												SerFormat.BIN_CROSSLANG,
												SerGraph.FLAT_TREE,
												SerClass.CLASSES_KNOWN,
												"",
												Avro.miscFeatures
								)
				);

		groups.media.add(MediaTransformer, new FastGenericSerializerWithObjectReuse(Avro.Media.sMediaContent),
								new SerFeatures(
												SerFormat.BIN_CROSSLANG,
												SerGraph.FLAT_TREE,
												SerClass.MANUAL_OPT,
												"",
												Avro.miscFeatures
								)
				);
	}

	// ------------------------------------------------------------
	// Serializer (just one class)

	private static final DecoderFactory DECODER_FACTORY
          = DecoderFactory.get();
	private static final EncoderFactory ENCODER_FACTORY
          = EncoderFactory.get();

	public static class GenericSerializer extends Serializer<GenericRecord>
	{
		public String getName() { return "avro-generic"; }

		protected final DatumWriter<GenericRecord> WRITER;
		protected final DatumReader<GenericRecord> READER;

		protected BinaryEncoder encoder;
		protected BinaryDecoder decoder;

		protected GenericSerializer(DatumWriter<GenericRecord> writer, DatumReader<GenericRecord> reader)
		{
			this.WRITER = writer;
			this.READER = reader;
		}

		public GenericSerializer(Schema schema)
		{
			this(new GenericDatumWriter<>(schema), new GenericDatumReader<>(schema));
		}

		public GenericRecord deserialize(byte[] array) throws Exception
		{
                  decoder = DECODER_FACTORY.binaryDecoder(array, decoder);
                  return READER.read(null, decoder);
		}

		public byte[] serialize(GenericRecord data) throws IOException
		{
                  ByteArrayOutputStream out = outputStream(data);
                  encoder = ENCODER_FACTORY.binaryEncoder(out, encoder);
                  WRITER.write(data, encoder);
                  encoder.flush();
                  return out.toByteArray();
		}

		@Override
		public void serializeItems(GenericRecord[] items, OutputStream out) throws Exception
		{
			encoder = ENCODER_FACTORY.binaryEncoder(out, encoder);
			for (GenericRecord item : items) {
				WRITER.write(item, encoder);
			}
			encoder.flush();
		}

		@Override
		public GenericRecord[] deserializeItems(InputStream in, int numberOfItems) throws Exception
		{
			decoder = DECODER_FACTORY.binaryDecoder(in, decoder);
			@SuppressWarnings("unchecked")
			GenericRecord[] result = (GenericRecord[]) Array.newInstance(GenericRecord.class, numberOfItems);
			GenericRecord item = null;
			for (int i = 0; i < numberOfItems; ++i) {
				result[i] = READER.read(item, decoder);
			}
			return result;
		}
	}

	public static class GenericSerializerWithObjectReuse extends GenericSerializer
	{
		public String getName() { return "avro-generic-manual"; }

		private final ByteArrayOutputStream out = outputStream(null);
		private final GenericRecord reuse;

		public GenericSerializerWithObjectReuse(Schema schema) {
			this(new GenericDatumWriter<>(schema), new GenericDatumReader<>(schema), schema);
		}

		public <T> GenericSerializerWithObjectReuse(DatumWriter<GenericRecord> writer, DatumReader<GenericRecord> reader, Schema schema) {
			super(writer, reader);
			this.reuse = new GenericData.Record(schema);
		}

		@Override
		public GenericRecord deserialize(byte[] array) throws Exception
		{
			decoder = DECODER_FACTORY.binaryDecoder(array, decoder);
			return READER.read(reuse, decoder);
		}

		@Override
		public byte[] serialize(GenericRecord data) throws IOException
		{
			out.reset();
			encoder = ENCODER_FACTORY.binaryEncoder(out, encoder);
			WRITER.write(data, encoder);
			encoder.flush();
			return out.toByteArray();
		}
	}

	public static class FastGenericSerializer extends AvroGeneric.GenericSerializer
	{
		public String getName() { return "avro-fastserde-generic"; }

		public FastGenericSerializer(Schema schema)
		{
			super(new FastGenericDatumWriter<>(schema), new FastGenericDatumReader<>(schema));
		}
	}

	public static class FastGenericSerializerWithObjectReuse extends AvroGeneric.GenericSerializerWithObjectReuse
	{
		public String getName() { return "avro-fastserde-generic-manual"; }

		public FastGenericSerializerWithObjectReuse(Schema schema) {
			super(new FastGenericDatumWriter<>(schema), new FastGenericDatumReader<>(schema), schema);
		}
	}

	// ------------------------------------------------------------
	// MediaTransformer

	public static final Schema sMediaContent = serializers.avro.media.MediaContent.SCHEMA$;

	static int forwardPlayer(Media.Player p)
	{
		switch (p) {
			case JAVA: return 1;
			case FLASH: return 2;
			default:
				throw new AssertionError("invalid case: " + p);
		}
	}

	static int forwardSize(Image.Size s)
	{
		switch (s) {
			case SMALL: return 1;
			case LARGE: return 2;
			default:
				throw new AssertionError("invalid case: " + s);
		}
	}


	public static final Transformer<MediaContent,GenericRecord> MediaTransformer = new MediaTransformer<GenericRecord>()
	{
		@SuppressWarnings("unused")
		private final Schema sImage = serializers.avro.media.Image.SCHEMA$;
		private final Schema sMedia = serializers.avro.media.Media.SCHEMA$;

                @SuppressWarnings("unused")
		private final Schema sImages = sMediaContent.getField("images").schema();
                @SuppressWarnings("unused")
		private final Schema sPersons = sMedia.getField("persons").schema();

                public GenericRecord[] resultArray(int size) {
                    return new GenericRecord[size];
                }
                
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
			m.put("uri", media.uri);
			m.put("format", media.format);
			if (media.title != null) {
				m.put("title", media.title);
			}
			m.put("duration", media.duration);
			if (media.hasBitrate) {
				m.put("bitrate", media.bitrate);
			}

			GenericData.Array<CharSequence> persons =  new GenericData.Array<CharSequence>(media.persons.size(), Avro.Media.sPersons);
			for (String p : media.persons) {
                          persons.add(p);
			}

			m.put("persons", persons);
			m.put("player", forwardPlayer(media.player));
			m.put("height", media.height);
			m.put("width", media.width);
			m.put("size", media.size);
			if (media.copyright != null) {
				m.put("copyright", media.copyright);
			}

			return m;
		}

		private GenericRecord forwardImage(Image image)
		{
			GenericRecord i = new GenericData.Record(Avro.Media.sImage);
			i.put("uri", image.uri);
			i.put("width", image.width);
			i.put("height", image.height);
			i.put("size", forwardSize(image.size));
			if (image.title != null) {
                          i.put("title",  image.title);
			}
			return i;
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
			GenericData.Array<CharSequence> gpersons = (GenericData.Array<CharSequence>) media.get("persons");
			List<String> persons = new ArrayList<String>((int) gpersons.size());
			for (CharSequence person : gpersons) {
				persons.add(person.toString());
			}

			// Optional fields.
			CharSequence title = (CharSequence) media.get("title");
			Integer bitrate = (Integer) media.get("bitrate");
			CharSequence copyright = (CharSequence) media.get("copyright");

			return new Media(
				((CharSequence) media.get("uri")).toString(),
				title != null ? title.toString() : null,
				(Integer) media.get("width"),
				(Integer) media.get("height"),
				((CharSequence) media.get("format")).toString(),
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
			CharSequence title = (CharSequence) image.get("title");
			return new Image(
				((CharSequence) image.get("uri")).toString(),
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
