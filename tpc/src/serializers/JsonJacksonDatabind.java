package serializers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
			return _mapper.writeValueAsBytes(data);
		}

		public T deserialize(byte[] array) throws Exception
		{
			return _mapper.readValue(array, 0, array.length, clazz);
		}
	};
}
