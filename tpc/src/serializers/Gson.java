package serializers;

import java.io.*;

import data.media.MediaContent;

/**
 * This serializer uses Google-gson for data binding.
 * to configure)
 */
public class Gson
{
	public static void register(TestGroups groups)
	{
		groups.media.add(JavaBuiltIn.MediaTransformer, Gson.<MediaContent>GenericSerializer());
	}

	public static <T> Serializer<T> GenericSerializer()
	{
		@SuppressWarnings("unchecked")
		Serializer<T> s = (Serializer<T>) GenericSerializer;
		return s;
	}

	// ------------------------------------------------------------
	// Serializer (just one)

	public static Serializer<Object> GenericSerializer = new Serializer<Object>()
	{
		private final com.google.gson.Gson _gson = new com.google.gson.Gson();

		public MediaContent deserialize(byte[] array) throws Exception
		{
			Reader r = new InputStreamReader(new ByteArrayInputStream(array), "UTF-8");
			MediaContent result = _gson.fromJson(r, MediaContent.class);
			r.close();
			return result;
		}

		public byte[] serialize(Object data) throws IOException
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputStreamWriter w = new OutputStreamWriter(baos, "UTF-8");
			_gson.toJson(data, w);
			w.close();
			return baos.toByteArray();
		}

		public String getName()
		{
			return "json/google-gson";
		}
	};
}
