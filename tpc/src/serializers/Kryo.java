package serializers;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.serialize.CollectionSerializer;
import com.esotericsoftware.kryo.serialize.EnumSerializer;
import com.esotericsoftware.kryo.serialize.FieldSerializer;
import com.esotericsoftware.kryo.serialize.FieldSerializer.CachedField;
import com.esotericsoftware.kryo.serialize.IntSerializer;
import com.esotericsoftware.kryo.serialize.LongSerializer;
import com.esotericsoftware.kryo.serialize.SimpleSerializer;
import com.esotericsoftware.kryo.serialize.StringSerializer;

import data.media.Image;
import data.media.Image.Size;
import data.media.Media;
import data.media.MediaContent;

/**
 * This is the most basic Kryo usage. Just register the classes and go.
 */
public class Kryo
{
	public static void register(TestGroups groups)
	{
		register(groups.media, JavaBuiltIn.mediaTransformer, MediaTypeHandler);
	}

	private static <T,S> void register(TestGroup<T> group, Transformer<T,S> transformer, TypeHandler<S> handler)
	{
		group.add(transformer, new BasicSerializer<S>(handler));
		group.add(transformer, new OptimizedSerializer<S>(handler));
		group.add(transformer, new CustomSerializer<S>(handler));
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
			this.objectBuffer = new com.esotericsoftware.kryo.ObjectBuffer(kryo, 2048);
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

	public static class CustomSerializer<T> extends Serializer<T>
	{
		protected final Class<T> type;
		protected final com.esotericsoftware.kryo.Kryo kryo;
		protected final com.esotericsoftware.kryo.ObjectBuffer objectBuffer;

		public CustomSerializer(TypeHandler<T> handler)
		{
			this.type = handler.type;
			this.kryo = new com.esotericsoftware.kryo.Kryo();
			this.objectBuffer = new com.esotericsoftware.kryo.ObjectBuffer(kryo, 2048);
			handler.registerCustom(this.kryo);
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
			return "kryo-manual";
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
		public abstract void registerCustom(com.esotericsoftware.kryo.Kryo kryo);
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
		
		public void registerCustom(com.esotericsoftware.kryo.Kryo kryo)
		{
			kryo.register(ArrayList.class);
			kryo.register(Image.class, new ImageSerializer(kryo)); // register before mediacontent for use by the collectionserializer
			kryo.register(MediaContent.class, new MediaContentSerializer(kryo));
			kryo.register(Media.class, new MediaSerializer(kryo));
		}
	};
	
	static class MediaContentSerializer extends SimpleSerializer<MediaContent> {
		
		private final com.esotericsoftware.kryo.Kryo _kryo;
		private CollectionSerializer _imagesSerializer;

		public MediaContentSerializer(com.esotericsoftware.kryo.Kryo kryo) {
			_kryo = kryo;
			_imagesSerializer = new CollectionSerializer(kryo);
			_imagesSerializer.setElementClass(Image.class);
			_imagesSerializer.setElementsCanBeNull(false);
		}

		@Override
		public MediaContent read(final ByteBuffer buffer) {
			final Media media = _kryo.readObjectData(buffer, Media.class);
			@SuppressWarnings("unchecked")
			final List<Image> images = (List<Image>) _imagesSerializer.readObjectData(buffer, ArrayList.class); // _kryo.readClassAndObject(buffer);
			return new MediaContent( media, images );
		}

		@Override
		public void write(final ByteBuffer buffer, final MediaContent obj) {
			_kryo.writeObjectData(buffer, obj.media);
			//_kryo.writeClassAndObject( buffer, obj.images );
			_imagesSerializer.writeObjectData(buffer, obj.images);
		}
		
	}
	
	static class MediaSerializer extends SimpleSerializer<Media> {
		
		private final com.esotericsoftware.kryo.Kryo _kryo;
		private final CollectionSerializer _personsSerializer;

		public MediaSerializer(final com.esotericsoftware.kryo.Kryo kryo) {
			_kryo = kryo;
			_personsSerializer = new CollectionSerializer(kryo);
			_personsSerializer.setElementClass(String.class);
			_personsSerializer.setElementsCanBeNull(false);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Media read(final ByteBuffer buffer) {
			return new Media(
				StringSerializer.get(buffer),
				_kryo.readObject(buffer, String.class),
				IntSerializer.get(buffer, true),
				IntSerializer.get(buffer, true),
				StringSerializer.get(buffer),
				LongSerializer.get(buffer, true),
				LongSerializer.get(buffer, true),
				IntSerializer.get(buffer, true),
				IntSerializer.get(buffer, true) == 1,
				(List<String>)_personsSerializer.readObjectData(buffer, ArrayList.class),
				EnumSerializer.get(buffer, Media.Player.class),
				_kryo.readObject(buffer, String.class));
		}

		@Override
		public void write(final ByteBuffer buffer, final Media obj) {
			StringSerializer.put(buffer, obj.uri);
			_kryo.writeObject(buffer, obj.title);
			IntSerializer.put(buffer, obj.width, true);
			IntSerializer.put(buffer, obj.height, true);
			StringSerializer.put(buffer, obj.format);
			LongSerializer.put(buffer, obj.duration, true);
			LongSerializer.put(buffer, obj.size, true);
			IntSerializer.put(buffer, obj.bitrate, true);
			IntSerializer.put(buffer, obj.hasBitrate ? 1 : 0, true);
			_personsSerializer.writeObjectData(buffer, obj.persons);
			EnumSerializer.put(buffer, obj.player);
			_kryo.writeObject(buffer, obj.copyright);
		}
		
	}
	
	static class ImageSerializer extends SimpleSerializer<Image> {
		
		private final com.esotericsoftware.kryo.Kryo _kryo;

		public ImageSerializer(final com.esotericsoftware.kryo.Kryo kryo) {
			_kryo = kryo;
		}

		@Override
		public Image read(final ByteBuffer buffer) {
			return new Image(
				StringSerializer.get(buffer),
				_kryo.readObject(buffer, String.class),
				IntSerializer.get(buffer, true),
				IntSerializer.get(buffer, true),
				EnumSerializer.get(buffer, Size.class));
		}

		@Override
		public void write(final ByteBuffer buffer, final Image obj) {
			StringSerializer.put(buffer, obj.uri);
			_kryo.writeObject(buffer, obj.title);
			IntSerializer.put(buffer, obj.width, true);
			IntSerializer.put(buffer, obj.height, true);
			EnumSerializer.put(buffer, obj.size);
		}
		
	}
}
