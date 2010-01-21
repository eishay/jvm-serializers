
package serializers.kryo;

import java.util.ArrayList;

import serializers.java.Image;
import serializers.java.Media;
import serializers.java.MediaContent;

import com.esotericsoftware.kryo.serialize.FieldSerializer;
import com.esotericsoftware.kryo.serialize.CollectionSerializer;
import com.esotericsoftware.kryo.serialize.FieldSerializer.CachedField;

/**
 * This test does all possible Kryo serializer optimizations.
 */
public class KryoOptimizedSerializer extends KryoSerializer {
	public KryoOptimizedSerializer () {
		this("kryo-optimized");
	}

	public KryoOptimizedSerializer (String name) {
		super(name);

		FieldSerializer imageSerializer = (FieldSerializer)kryo.getSerializer(Image.class);
		imageSerializer.setFieldsCanBeNull(false);

		FieldSerializer mediaContentSerializer = (FieldSerializer)kryo.getSerializer(MediaContent.class);
		mediaContentSerializer.setFieldsCanBeNull(false);

		CachedField imagesField = mediaContentSerializer.getField("_images");
		CollectionSerializer imagesSerializer = new CollectionSerializer(kryo);
		imagesSerializer.setElementClass(Image.class);
		imagesSerializer.setElementsCanBeNull(false);
		imagesSerializer.setLength(2);
		imagesField.setClass(ArrayList.class, imagesSerializer);

		CachedField mediaField = mediaContentSerializer.getField("_media");
		FieldSerializer mediaSerializer = new FieldSerializer(kryo, Media.class);
		mediaSerializer.setFieldsCanBeNull(false);
		mediaField.setClass(Media.class, mediaSerializer);

		CachedField personsField = mediaSerializer.getField("_persons");
		CollectionSerializer personsSerializer = new CollectionSerializer(kryo);
		personsSerializer.setElementClass(String.class);
		personsSerializer.setElementsCanBeNull(false);
		personsSerializer.setLength(2);
		personsField.setClass(ArrayList.class, personsSerializer);

		mediaSerializer.getField("_copyright").setCanBeNull(true);
	}
}
