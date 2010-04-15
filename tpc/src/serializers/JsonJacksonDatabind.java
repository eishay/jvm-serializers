package serializers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import data.media.MediaContent;

import org.codehaus.jackson.*;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * This serializer uses Jackson in full automated data binding mode, which
 * can handle typical Java POJOs (esp. beans; otherwise may need to annotate
 * to configure)
 */
public class JsonJacksonDatabind
{
	public static void register(TestGroups groups)
	{
		groups.media.add(JavaBuiltIn.MediaTransformer, new GenericSerializer<MediaContent>(MediaContent.class));
	}

	// ------------------------------------------------------------
	// Serializer (just one)

	private static final JsonFactory _factory = new JsonFactory();
	private static final ObjectMapper _mapper = new ObjectMapper();

	public static class GenericSerializer<T> extends Serializer<T>
	{
		private final Class<T> clazz;
		public GenericSerializer(Class<T> clazz)
		{
			this.clazz = clazz;
		}

		public String getName() { return "json/jackson-databind"; }

		public byte[] serialize(T data) throws IOException
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			JsonGenerator generator = constructGenerator(baos);
			_mapper.writeValue(generator, data);
			generator.close();
			return baos.toByteArray();
		}

		public T deserialize(byte[] array) throws Exception
		{
			JsonParser parser = constructParser(array);
			T o = _mapper.readValue(parser, clazz);
			return o;
		}

		protected JsonParser constructParser(byte[] data) throws IOException
		{
			return _factory.createJsonParser(data);
		}

		protected JsonGenerator constructGenerator(ByteArrayOutputStream baos) throws IOException
		{
			return _factory.createJsonGenerator(baos, JsonEncoding.UTF8);
		}
	};
}
