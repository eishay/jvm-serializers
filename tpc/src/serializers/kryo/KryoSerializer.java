
package serializers.kryo;

import java.util.ArrayList;

import serializers.ObjectSerializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.ObjectBuffer;

/**
 * This is the most basic Kryo usage. Just register the classes and go.
 */
public class KryoSerializer implements ObjectSerializer<MediaContent> {
	protected Kryo kryo;
	protected ObjectBuffer objectBuffer;

	public KryoSerializer () {
		kryo = new Kryo();
		kryo.register(ArrayList.class);
		kryo.register(MediaContent.class);
		kryo.register(Media.Player.class);
		kryo.register(Media.class);
		kryo.register(Image.Size.class);
		kryo.register(Image.class);
		objectBuffer = new ObjectBuffer(kryo, 1024);
	}

	public String getName () {
		return "kryo";
	}

	public MediaContent deserialize (byte[] array) throws Exception {
		return objectBuffer.readObjectData(array, MediaContent.class);
	}

	public byte[] serialize (MediaContent content) throws Exception {
		return objectBuffer.writeObjectData(content);
	}

	public final MediaContent create () throws Exception {
		Media media = new Media(null, "video/mpg4", Media.Player.JAVA, "Javaone Keynote", "http://javaone.com/keynote.mpg",
			1234567, 123, 0, 0, 0);
		media.addToPerson("Bill Gates");
		media.addToPerson("Steve Jobs");
		MediaContent content = new MediaContent(media);
		content.addImage(new Image(0, "Javaone Keynote", "http://javaone.com/keynote_large.jpg", 0, Image.Size.LARGE));
		content.addImage(new Image(0, "Javaone Keynote", "http://javaone.com/keynote_thumbnail.jpg", 0, Image.Size.SMALL));
		return content;
	}
}
