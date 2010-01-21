
package serializers.kryo;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import serializers.StdMediaSerializer;
import serializers.java.Image;
import serializers.java.Media;
import serializers.java.MediaContent;
import serializers.protobuf.MediaContentHolder.Image.Size;
import serializers.protobuf.MediaContentHolder.Media.Player;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.ObjectBuffer;

/**
 * This is the most basic Kryo usage. Just register the classes and go.
 */
public class KryoSerializer extends StdMediaSerializer {
	protected Kryo kryo;
	protected ObjectBuffer objectBuffer;

	public KryoSerializer () {
		this("kryo");
	}

	public KryoSerializer (String name) {
		super(name);
		kryo = new Kryo();
		kryo.register(ArrayList.class);
		kryo.register(MediaContent.class);
		kryo.register(Media.Player.class);
		kryo.register(Media.class);
		kryo.register(Image.Size.class);
		kryo.register(Image.class);
		objectBuffer = new ObjectBuffer(kryo, 64, 1024);
	}

	public MediaContent deserialize (byte[] array) throws Exception {
		return objectBuffer.readObjectData(array, MediaContent.class);
	}

	public byte[] serialize (MediaContent content) throws Exception {
		return objectBuffer.writeObjectData(content);
	}
}
