package serializers;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import data.media.MediaTransformer;
import serializers.avro.media.*;

public class AvroSpecific
{
	public static void register(TestGroups groups)
	{
		groups.media.add(mediaTransformer, new GenericSerializer<MediaContent>(MediaContent.class));
	}

	// ------------------------------------------------------------
	// Serializers

	private static final DecoderFactory DECODER_FACTORY
          = DecoderFactory.get();
	private static final EncoderFactory ENCODER_FACTORY
          = EncoderFactory.get();

	public static final class GenericSerializer<T> extends Serializer<T>
	{
		public String getName() { return "avro"; }

		private final SpecificDatumReader<T> READER;
		private final SpecificDatumWriter<T> WRITER;

                private BinaryEncoder encoder;
                private BinaryDecoder decoder;

                private final Class<T> clazz;
                
		public GenericSerializer(Class<T> clazz)
		{
		    this.clazz = clazz;
		    this.READER = new SpecificDatumReader<T>(clazz);
		    this.WRITER = new SpecificDatumWriter<T>(clazz);
		}

		public T deserialize(byte[] array) throws Exception {
                  decoder = DECODER_FACTORY.binaryDecoder(array, decoder);
                  return READER.read(null, decoder);
		}

		public byte[] serialize(T content) throws Exception {
                  ByteArrayOutputStream out = outputStream(content);
                  encoder = ENCODER_FACTORY.binaryEncoder(out, encoder);
                  WRITER.write(content, encoder);
                  encoder.flush();
                  return out.toByteArray();
		}

		@Override
	        public void serializeItems(T[] items, OutputStream out) throws IOException
	        {
		    encoder = ENCODER_FACTORY.binaryEncoder(out, encoder);
		    for (T item : items) {
		        WRITER.write(item, encoder);
		    }
		    encoder.flush();
	        }

	        @Override
	        public T[] deserializeItems(InputStream in0, int numberOfItems) throws IOException 
	        {
	            decoder = DECODER_FACTORY.binaryDecoder(in0, decoder);
	            @SuppressWarnings("unchecked")
	            T[] result = (T[]) Array.newInstance(clazz, numberOfItems);
	            T item = null;
	            for (int i = 0; i < numberOfItems; ++i) {
	                result[i] = READER.read(item, decoder);
	            }
	            return result;
	        }
	}

	// ------------------------------------------------------------
	// Transformers

	public static final MediaTransformer<MediaContent> mediaTransformer = new MediaTransformer<MediaContent>()
	{
	        @Override
	        public MediaContent[] resultArray(int size) { return new MediaContent[size]; }
	    
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

			m.uri = media.uri;
			m.title = media.title;
			m.width = media.width;
			m.height = media.height;
			m.format = media.format;
			m.duration = media.duration;
			m.size = media.size;
			if (media.hasBitrate) m.bitrate = media.bitrate;

			GenericArray<CharSequence> persons = new GenericData.Array<CharSequence>(media.persons.size(), Avro.Media.sPersons);
			for (String s : media.persons) {
                          persons.add(s);
			}
			m.persons = persons;

			m.player = forwardPlayer(media.player);
			m.copyright = media.copyright;

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
			i.uri = image.uri;
			i.title = image.title;
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
			for (CharSequence p : media.persons) {
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
