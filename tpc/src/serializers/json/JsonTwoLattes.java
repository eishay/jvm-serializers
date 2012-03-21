package serializers.json;

import java.io.*;
import java.nio.charset.Charset;

import core.TestGroups;
import core.serializers.Serializer;

import com.twolattes.json.Json;
import com.twolattes.json.Marshaller;
import com.twolattes.json.TwoLattes;

public class JsonTwoLattes
{
	public static void register(TestGroups groups)
	{
		// TODO: Need classes with @Entity and @Value annotations.
		//groups.media.add(JavaBuiltIn.MediaTransformer, new GenericSerializer<MediaContent>(MediaContent.class));
	}

	// ------------------------------------------------------------
	// Serializer (just one class)

	public static final class GenericSerializer<T> extends Serializer<T>
	{
		public String getName() { return "json/twolattes"; }

		private final Marshaller<T> _marshaller;
		private final Charset _charset = Charset.forName("UTF-8");

		public GenericSerializer(Class<T> clazz)
		{
			_marshaller = TwoLattes.createMarshaller(clazz);
		}


		public T deserialize(byte[] array) throws Exception {
			String str = new String(array, _charset.toString());
			return _marshaller.unmarshall((Json.Object) Json.read(
				new StringReader(str)));
		}

		public byte[] serialize(T content) throws Exception {
			StringWriter sw = new StringWriter();
			_marshaller.marshall(content).write(sw);
			sw.flush();
			return sw.toString().getBytes(_charset.toString());
		}
	}
}
