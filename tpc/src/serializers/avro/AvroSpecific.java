package serializers.avro;

import com.linkedin.avro.fastserde.FastSpecificDatumReader;
import com.linkedin.avro.fastserde.FastSpecificDatumWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
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
        groups.media.add(new AvroTransformer(), new SpecificSerializer<>(MediaContent.class),
                new SerFeatures(
                        SerFormat.BIN_CROSSLANG,
                        SerGraph.FLAT_TREE,
                        SerClass.CLASSES_KNOWN,
                        "",
												Avro.miscFeatures
                )
        );
				groups.media.add(new AvroTransformer(), new SpecificSerializerWithObjectReuse<>(MediaContent.class),
								new SerFeatures(
												SerFormat.BIN_CROSSLANG,
												SerGraph.FLAT_TREE,
												SerClass.MANUAL_OPT,
												"",
												Avro.miscFeatures
								)
				);
				groups.media.add(new AvroTransformer(), new FastSpecificSerializer<>(MediaContent.class, Avro.Media.sMediaContent),
								new SerFeatures(
												SerFormat.BIN_CROSSLANG,
												SerGraph.FLAT_TREE,
												SerClass.CLASSES_KNOWN,
												"",
												Avro.miscFeatures
								)
				);
				groups.media.add(new AvroTransformer(), new FastGenericSerializerWithObjectReuse<>(MediaContent.class, Avro.Media.sMediaContent),
								new SerFeatures(
												SerFormat.BIN_CROSSLANG,
												SerGraph.FLAT_TREE,
												SerClass.MANUAL_OPT,
												"",
												Avro.miscFeatures
								)
				);
		}

    private static final DecoderFactory DECODER_FACTORY = DecoderFactory.get();
    private static final EncoderFactory ENCODER_FACTORY = EncoderFactory.get();

	public static class SpecificSerializer<T> extends Serializer<T>
	{
		public String getName() { return "avro-specific"; }

		protected final DatumReader<T> READER;
		protected final DatumWriter<T> WRITER;

		protected BinaryEncoder encoder;
		protected BinaryDecoder decoder;

		protected final Class<T> clazz;

		public SpecificSerializer(Class<T> clazz, DatumReader<T> reader, DatumWriter<T> writer)
		{
		    this.clazz = clazz;
				this.READER = reader;
				this.WRITER = writer;
		}

		public SpecificSerializer(Class<T> clazz)
		{
				this(clazz, new SpecificDatumReader<T>(clazz), new SpecificDatumWriter<T>(clazz));
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

	public static class SpecificSerializerWithObjectReuse<T> extends AvroSpecific.SpecificSerializer<T>
	{
		public String getName() { return "avro-specific-manual"; }

		private final ByteArrayOutputStream out = outputStream(null);
		private final T reuse;

		public SpecificSerializerWithObjectReuse(Class<T> clazz) {
			super(clazz);
			try {
				this.reuse = clazz.newInstance();
			} catch (InstantiationException|IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public T deserialize(byte[] array) throws Exception
		{
			decoder = DECODER_FACTORY.binaryDecoder(array, decoder);
			return READER.read(reuse, decoder);
		}

		@Override
		public byte[] serialize(T data) throws IOException
		{
			out.reset();
			encoder = ENCODER_FACTORY.binaryEncoder(out, encoder);
			WRITER.write(data, encoder);
			encoder.flush();
			return out.toByteArray();
		}
	}

	public static final class FastSpecificSerializer<T> extends AvroSpecific.SpecificSerializer<T>
	{
		public String getName() { return "avro-fastserde-specific"; }

		public FastSpecificSerializer(Class<T> clazz, Schema schema)
		{
			super(clazz, new FastSpecificDatumReader<>(schema), new FastSpecificDatumWriter<>(schema));
		}
	}

	public static class FastGenericSerializerWithObjectReuse<T> extends AvroSpecific.SpecificSerializer<T>
	{
		public String getName() { return "avro-fastserde-specific-manual"; }

		public FastGenericSerializerWithObjectReuse(Class<T> clazz, Schema schema) {
			super(clazz, new FastSpecificDatumReader<>(schema), new FastSpecificDatumWriter<>(schema));
		}
	}
}
