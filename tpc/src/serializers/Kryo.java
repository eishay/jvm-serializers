package serializers;

import java.util.ArrayList;

import data.media.*;

import com.esotericsoftware.kryo.serialize.CollectionSerializer;
import com.esotericsoftware.kryo.serialize.FieldSerializer;
import com.esotericsoftware.kryo.serialize.FieldSerializer.CachedField;

/**
 * This is the most basic Kryo usage. Just register the classes and go.
 */
public class Kryo
{
	public static void register(TestGroups groups)
	{
		register(groups.media, JavaBuiltIn.MediaTransformer, MediaTypeHandler);
	}

	private static <T,S> void register(TestGroup<T> group, Transformer<T,S> transformer, TypeHandler<S> handler)
	{
		group.add(transformer, new BasicSerializer<S>(handler));
		group.add(transformer, new OptimizedSerializer<S>(handler));
	}

	// ------------------------------------------------------------
	// Serializers

	public static class BasicSerializer<T> extends Serializer<T>
	{
		protected final Class<T> type;
		protected final com.esotericsoftware.kryo.Kryo kryo;
		protected final com.esotericsoftware.kryo.ObjectBuffer objectBuffer;

		public BasicSerializer(TypeHandler<T> handler)
		{
			this.type = handler.type;
			this.kryo = new com.esotericsoftware.kryo.Kryo();
			this.objectBuffer = new com.esotericsoftware.kryo.ObjectBuffer(kryo, 1024);
			handler.register(this.kryo);
		}

		public T deserialize (byte[] array)
		{
			return objectBuffer.readObjectData(array, type);
		}

		public byte[] serialize (T content)
		{
			return objectBuffer.writeObjectData(content);
		}

		public String getName()
		{
			return "kryo";
		}
	}

	public static class OptimizedSerializer<T> extends BasicSerializer<T>
	{
		public OptimizedSerializer(TypeHandler<T> handler)
		{
			super(handler);
			handler.optimize(this.kryo);
		}

		public String getName()
		{
			return "kryo-opt";
		}
	}

	// ------------------------------------------------------------

	public static abstract class TypeHandler<T>
	{
		public final Class<T> type;
		protected TypeHandler(Class<T> type)
		{
			this.type = type;
		}

		public abstract void register(com.esotericsoftware.kryo.Kryo kryo);
		public abstract void optimize(com.esotericsoftware.kryo.Kryo kryo);
	}

	// ------------------------------------------------------------
	// Media

	public static final TypeHandler<MediaContent> MediaTypeHandler = new TypeHandler<MediaContent>(MediaContent.class)
	{
		public void register(com.esotericsoftware.kryo.Kryo kryo)
		{
			kryo.register(ArrayList.class);
			kryo.register(MediaContent.class);
			kryo.register(Media.Player.class);
			kryo.register(Media.class);
			kryo.register(Image.Size.class);
			kryo.register(Image.class);
		}

		public void optimize(com.esotericsoftware.kryo.Kryo kryo)
		{
			FieldSerializer imageSerializer = (FieldSerializer)kryo.getSerializer(Image.class);
			imageSerializer.setFieldsCanBeNull(false);
			imageSerializer.getField("title").setCanBeNull(true);

			FieldSerializer mediaContentSerializer = (FieldSerializer)kryo.getSerializer(MediaContent.class);
			mediaContentSerializer.setFieldsCanBeNull(false);

			CachedField imagesField = mediaContentSerializer.getField("images");
			CollectionSerializer imagesSerializer = new CollectionSerializer(kryo);
			imagesSerializer.setElementClass(Image.class);
			imagesSerializer.setElementsCanBeNull(false);
			imagesField.setClass(ArrayList.class, imagesSerializer);

			FieldSerializer mediaSerializer = new FieldSerializer(kryo, Media.class);
			mediaSerializer.setFieldsCanBeNull(false);
			mediaSerializer.getField("title").setCanBeNull(true);
			mediaSerializer.getField("copyright").setCanBeNull(true);

			CachedField mediaField = mediaContentSerializer.getField("media");
			mediaField.setClass(Media.class, mediaSerializer);

			CachedField personsField = mediaSerializer.getField("persons");
			CollectionSerializer personsSerializer = new CollectionSerializer(kryo);
			personsSerializer.setElementClass(String.class);
			personsSerializer.setElementsCanBeNull(false);
			personsField.setClass(ArrayList.class, personsSerializer);
		}
	};
}
