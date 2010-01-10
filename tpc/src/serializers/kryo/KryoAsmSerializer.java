
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
import com.esotericsoftware.kryo.serialize.AsmFieldSerializer;

/**
 * This is basic Kryo usage using AsmFieldSerializer instead of FieldSerializer. AsmFieldSerializer does bytecode generation to
 * read and write public fields.
 */
public class KryoAsmSerializer extends StdMediaSerializer {
	protected Kryo kryo;
	protected ObjectBuffer objectBuffer;

	public KryoAsmSerializer () {
		this("kryo-asm");
	}

	public KryoAsmSerializer (String name) {
		super(name);
		kryo = new Kryo();
		kryo.register(ArrayList.class);
		AsmFieldSerializer asmSerializer = new AsmFieldSerializer(kryo);
		kryo.register(MediaContent.class, asmSerializer);
		kryo.register(Media.class, asmSerializer);
		kryo.register(Media.Player.class);
		kryo.register(Image.class, asmSerializer);
		kryo.register(Image.Size.class);
		objectBuffer = new ObjectBuffer(kryo);
	}

	public MediaContent deserialize (byte[] array) throws Exception {
		return objectBuffer.readObjectData(array, MediaContent.class);
	}

	public byte[] serialize (MediaContent content) throws Exception {
		return objectBuffer.writeObjectData(content);
	}
}
