
package serializers.kryo;

import java.util.ArrayList;

import serializers.java.Image;
import serializers.java.Media;
import serializers.java.MediaContent;

import com.esotericsoftware.kryo.serialize.AsmFieldSerializer;
import com.esotericsoftware.kryo.serialize.CollectionSerializer;
import com.esotericsoftware.kryo.serialize.AsmFieldSerializer.CachedField;

/**
 * This test does all possible Kryo serializer optimizations.
 */
public class KryoOptimizedSerializer extends KryoSerializer {
	public KryoOptimizedSerializer () {
		this("kryo-optimized");
	}

	public KryoOptimizedSerializer (String name) {
		super(name);
		AsmFieldSerializer imageSerializer = new AsmFieldSerializer(kryo);
		imageSerializer.setFieldsCanBeNull(false);
		kryo.register(Image.class, imageSerializer);

		AsmFieldSerializer mediaContentSerializer = new AsmFieldSerializer(kryo);
		mediaContentSerializer.setFieldsCanBeNull(false);
		kryo.register(MediaContent.class, mediaContentSerializer);

		CachedField imagesField = mediaContentSerializer.getField(MediaContent.class, "_images");
		CollectionSerializer imagesSerializer = new CollectionSerializer(kryo);
		imagesSerializer.setElementClass(Image.class);
		imagesSerializer.setElementsCanBeNull(false);
		imagesSerializer.setLength(2);
		imagesField.setClass(ArrayList.class, imagesSerializer);

		CachedField mediaField = mediaContentSerializer.getField(MediaContent.class, "_media");
		AsmFieldSerializer mediaSerializer = new AsmFieldSerializer(kryo);
		mediaSerializer.setFieldsCanBeNull(false);
		mediaField.setClass(Media.class, mediaSerializer);

		CachedField personsField = mediaSerializer.getField(Media.class, "_persons");
		CollectionSerializer personsSerializer = new CollectionSerializer(kryo);
		personsSerializer.setElementClass(String.class);
		personsSerializer.setElementsCanBeNull(false);
		personsSerializer.setLength(2);
		personsField.setClass(ArrayList.class, personsSerializer);

		mediaSerializer.getField(Media.class, "_copyright").setCanBeNull(true);
	}
}
