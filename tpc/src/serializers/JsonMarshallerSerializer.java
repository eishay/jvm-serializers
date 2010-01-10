package serializers;

import java.io.*;
import java.nio.charset.Charset;

import com.twolattes.json.Json;
import com.twolattes.json.Marshaller;
import com.twolattes.json.TwoLattes;

import serializers.java.MediaContent;

public class JsonMarshallerSerializer extends StdMediaSerializer
{

	private final Marshaller<MediaContent> _marshaller;
	private final Charset _charset;
	private int _expectedSize = 0;

	public JsonMarshallerSerializer() {
		super("JsonMarshaller");
		_marshaller = TwoLattes.createMarshaller(MediaContent.class);
		_charset = Charset.forName("UTF-8");
	}
	
	public MediaContent deserialize(byte[] array) throws Exception {
		String str = new String(array, _charset.toString());
		return _marshaller.unmarshall((Json.Object) Json.read(
				new StringReader(str)));
	}

	public byte[] serialize(MediaContent content) throws Exception {
		StringWriter sw = new StringWriter(_expectedSize);
		_marshaller.marshall(content).write(sw);
		sw.flush();
    byte[] result = sw.toString().getBytes(_charset.toString());
		_expectedSize = result.length;
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		JsonMarshallerSerializer serializer = new JsonMarshallerSerializer();
		serializer.deserialize(serializer.serialize(serializer.create()));
	}

}
