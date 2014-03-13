package serializers.avro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;

import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import serializers.*;
import serializers.avro.media.*;

public class AvroSpecific
{
    public static void register(TestGroups groups)
    {
        groups.media.add(new AvroTransformer(), new GenericSerializer<MediaContent>(MediaContent.class),
                new SerFeatures(
                        SerFormat.BIN_CROSSLANG,
                        SerGraph.UNKNOWN,
                        SerClass.MANUAL_OPT,
                        ""
                )
        );
    }

    private static final DecoderFactory DECODER_FACTORY = DecoderFactory.get();
    private static final EncoderFactory ENCODER_FACTORY = EncoderFactory.get();

	public static final class GenericSerializer<T> extends Serializer<T>
	{
		public String getName() { return "avro-specific"; }

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
}
