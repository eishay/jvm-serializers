package serializers;

import java.io.IOException;

import data.media.MediaContent;

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
	        ObjectMapper mapper = new ObjectMapper();
		groups.media.add(JavaBuiltIn.MediaTransformer,
		        new GenericSerializer<MediaContent>("json/jackson-databind", mapper, MediaContent.class));
	}

	// ------------------------------------------------------------
	// Serializer (just one)
	
	public static class GenericSerializer<T> extends Serializer<T>
	{
	        private final String name;
	        private final ObjectMapper mapper;

	        private final Class<T> clazz;
		public GenericSerializer(String name, ObjectMapper mapper, Class<T> clazz)
		{
		    this.name = name;
		    this.mapper = mapper;
		    this.clazz = clazz;
		}

		public String getName() { return name; }

		public byte[] serialize(T data) throws IOException
		{
			return mapper.writeValueAsBytes(data);
		}

		public T deserialize(byte[] array) throws Exception
		{
			return mapper.readValue(array, 0, array.length, clazz);
		}
	};
}
