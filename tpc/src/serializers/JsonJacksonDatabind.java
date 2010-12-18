package serializers;

import java.io.IOException;

import data.media.MediaContent;

import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;

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
	        // note: could also force static typing; left out to keep defaults
		groups.media.add(JavaBuiltIn.MediaTransformer,
		        new GenericSerializer<MediaContent>("json/jackson-databind", mapper, MediaContent.class));
	}

	// ------------------------------------------------------------
	// Serializer (just one)
	
	public static class GenericSerializer<T> extends Serializer<T>
	{
	        private final String name;
	        private final ObjectMapper mapper;

	        private final JavaType type;

		public GenericSerializer(String name, ObjectMapper mapper, Class<T> clazz)
		{
		    this.name = name;
		    this.mapper = mapper;
		    this.type = TypeFactory.type(clazz);
		}

		public String getName() { return name; }

		public byte[] serialize(T data) throws IOException
		{
			return mapper.writeValueAsBytes(data);
		}

		@SuppressWarnings("unchecked")
                public T deserialize(byte[] array) throws Exception
                {
		    return (T) mapper.readValue(array, 0, array.length, type);
		}
	};
}
