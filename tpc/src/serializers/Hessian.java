package serializers;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import data.media.MediaContent;

import com.caucho.hessian.io.*;

public class Hessian
{
	public static void register(TestGroups groups)
	{
		groups.media.add(JavaBuiltIn.mediaTransformer, Hessian.<MediaContent>GenericSerializer());
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
		public Object deserialize(byte[] array) throws Exception
		{
			ByteArrayInputStream in = new ByteArrayInputStream(array);
			Hessian2StreamingInput hin = new Hessian2StreamingInput(in);
			return hin.readObject();
		}

		public byte[] serialize(Object data) throws java.io.IOException
		{
			ByteArrayOutputStream out = outputStream(data);
			Hessian2StreamingOutput hout = new Hessian2StreamingOutput(out);
			hout.writeObject(data);
			return out.toByteArray();
		}

		public String getName()
		{
			return "hessian";
		}
	};
}
