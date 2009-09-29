
package serializers.kryo;

import serializers.java.MediaContent;

import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.compress.DeflateCompressor;

public class KryoCompressedSerializer extends KryoOptimizedSerializer {
	public KryoCompressedSerializer () {
		this("kryo-compressed");
	}

	public KryoCompressedSerializer (String name) {
		super(name);
		Serializer mediaContentSerializer = kryo.getRegisteredClass(MediaContent.class).serializer;
		kryo.register(MediaContent.class, new DeflateCompressor(mediaContentSerializer));
	}
}
