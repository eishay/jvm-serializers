package serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
		_charset = Charset.forName("UTF8");
	}
	
	@Override
	public MediaContent deserialize(byte[] array) throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream(array);
		return _marshaller.unmarshall((Json.Object) Json.read(
				new InputStreamReader(in, _charset)));
	}

	@Override
	public byte[] serialize(MediaContent content) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream(_expectedSize);
		OutputStreamWriter writer = new OutputStreamWriter(out, _charset);
		_marshaller.marshall(content).write(writer);
		writer.flush();
		byte[] result = out.toByteArray();
		_expectedSize = result.length;
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		JsonMarshallerSerializer serializer = new JsonMarshallerSerializer();
		serializer.deserialize(serializer.serialize(serializer.create()));
	}

}
