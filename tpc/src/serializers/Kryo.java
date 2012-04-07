
package serializers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.kryo.serializers.FieldSerializer.CachedField;

import data.media.Image;
import data.media.Image.Size;
import data.media.Media;
import data.media.MediaContent;

public class Kryo {
	public static void register (TestGroups groups) {
		register(groups.media, JavaBuiltIn.mediaTransformer, MediaTypeHandler);
	}

	private static <T, S> void register (TestGroup<T> group, Transformer<T, S> transformer, TypeHandler<S> handler) {
		group.add(transformer, new BasicSerializer<S>(handler));
		group.add(transformer, new OptimizedSerializer<S>(handler));
		group.add(transformer, new CustomSerializer<S>(handler));
	}

	// ------------------------------------------------------------
	// Serializers

	/** This is the most basic Kryo usage. Just register the classes and go. */
	public static class BasicSerializer<T> extends Serializer<T> {
		protected final Class<T> type;
		protected final com.esotericsoftware.kryo.Kryo kryo;
		protected final Input input;
		protected final Output output;

		public BasicSerializer (TypeHandler<T> handler) {
			this.type = handler.type;
			this.kryo = new com.esotericsoftware.kryo.Kryo();
			kryo.setReferences(false);
			kryo.setRegistrationRequired(true);
			this.input = new Input(4096);
			this.output = new Output(4096);
			handler.register(this.kryo);
		}

		public T deserialize (byte[] array) {
			input.setBuffer(array);
			return kryo.readObject(input, type);
		}

		public byte[] serialize (T content) {
			output.clear();
			kryo.writeObject(output, content);
			return output.toBytes();
		}

		public void serializeItems (T[] items, OutputStream outStream) throws Exception {
			output.setOutputStream(outStream);
			for (int i = 0, n = items.length; i < n; ++i) {
				kryo.writeObject(output, items[i]);
			}
			output.flush();
		}

		public T[] deserializeItems (InputStream inStream, int numberOfItems) throws IOException {
			input.setInputStream(inStream);
			MediaContent[] result = new MediaContent[numberOfItems];
			for (int i = 0; i < numberOfItems; ++i) {
				result[i] = kryo.readObject(input, MediaContent.class);
			}
			return (T[])result;
		}

		public String getName () {
			return "kryo";
		}
	}

	/** This shows how to configure individual Kryo serializers. This generally reduces the serialized bytes and increases speed a
	 * tiny amount. */
	public static class OptimizedSerializer<T> extends BasicSerializer<T> {
		public OptimizedSerializer (TypeHandler<T> handler) {
			super(handler);
			handler.optimize(this.kryo);
		}

		public String getName () {
			return "kryo-opt";
		}
	}

	/** This shows how to use hand written serialization code with Kryo, while still leveraging Kryo for most of the work. A
	 * serializer for each class can be implemented, as it is here, or the classes to be serialized can implement an interface and
	 * host their own serialization code (similar to java.io.Externalizable). */
	public static class CustomSerializer<T> extends BasicSerializer<T> {
		public CustomSerializer (TypeHandler<T> handler) {
			super(handler);
			handler.registerCustom(this.kryo);
		}

		public String getName () {
			return "kryo-manual";
		}
	}

	// ------------------------------------------------------------

	public static abstract class TypeHandler<T> {
		public final Class<T> type;

		protected TypeHandler (Class<T> type) {
			this.type = type;
		}

		public abstract void register (com.esotericsoftware.kryo.Kryo kryo);

		public abstract void optimize (com.esotericsoftware.kryo.Kryo kryo);

		public abstract void registerCustom (com.esotericsoftware.kryo.Kryo kryo);
	}

	// ------------------------------------------------------------
	// Media

	public static final TypeHandler<MediaContent> MediaTypeHandler = new TypeHandler<MediaContent>(MediaContent.class) {
		public void register (com.esotericsoftware.kryo.Kryo kryo) {
			kryo.register(ArrayList.class);
			kryo.register(MediaContent.class);
			kryo.register(Media.Player.class);
			kryo.register(Media.class);
			kryo.register(Image.Size.class);
			kryo.register(Image.class);
		}

		public void optimize (com.esotericsoftware.kryo.Kryo kryo) {
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

		public void registerCustom (com.esotericsoftware.kryo.Kryo kryo) {
			kryo.register(ArrayList.class);
			kryo.register(Image.class, new ImageSerializer());
			kryo.register(MediaContent.class, new MediaContentSerializer(kryo));
			kryo.register(Media.class, new MediaSerializer(kryo));
			kryo.register(Media.Player.class);
			kryo.register(Image.Size.class);
		}
	};

	static class MediaContentSerializer implements com.esotericsoftware.kryo.Serializer<MediaContent> {
		private CollectionSerializer _imagesSerializer;

		public MediaContentSerializer (com.esotericsoftware.kryo.Kryo kryo) {
			_imagesSerializer = new CollectionSerializer(kryo);
			_imagesSerializer.setElementClass(Image.class);
			_imagesSerializer.setElementsCanBeNull(false);
		}

		public MediaContent read (com.esotericsoftware.kryo.Kryo kryo, Input input, Class<MediaContent> type) {
			final Media media = kryo.readObject(input, Media.class);
			@SuppressWarnings("unchecked")
			final List<Image> images = (List<Image>)kryo.readObject(input, ArrayList.class, _imagesSerializer);
			return new MediaContent(media, images);
		}

		public void write (com.esotericsoftware.kryo.Kryo kryo, Output output, MediaContent obj) {
			kryo.writeObject(output, obj.media);
			kryo.writeObject(output, obj.images, _imagesSerializer);
		}
	}

	static class MediaSerializer implements com.esotericsoftware.kryo.Serializer<Media> {
		private final CollectionSerializer _personsSerializer;

		public MediaSerializer (final com.esotericsoftware.kryo.Kryo kryo) {
			_personsSerializer = new CollectionSerializer(kryo);
			_personsSerializer.setElementClass(String.class);
			_personsSerializer.setElementsCanBeNull(false);
		}

		@SuppressWarnings("unchecked")
		public Media read (com.esotericsoftware.kryo.Kryo kryo, Input input, Class<Media> type) {
			return new Media(input.readString(), input.readString(), input.readInt(true), input.readInt(true), input.readString(),
				input.readLong(true), input.readLong(true), input.readInt(true), input.readBoolean(), (List<String>)kryo.readObject(
					input, ArrayList.class, _personsSerializer), kryo.readObject(input, Media.Player.class), input.readString());
		}

		public void write (com.esotericsoftware.kryo.Kryo kryo, Output output, Media obj) {
			output.writeString(obj.uri);
			output.writeString(obj.title);
			output.writeInt(obj.width, true);
			output.writeInt(obj.height, true);
			output.writeString(obj.format);
			output.writeLong(obj.duration, true);
			output.writeLong(obj.size, true);
			output.writeInt(obj.bitrate, true);
			output.writeBoolean(obj.hasBitrate);
			kryo.writeObject(output, obj.persons, _personsSerializer);
			kryo.writeObject(output, obj.player);
			output.writeString(obj.copyright);
		}
	}

	static class ImageSerializer implements com.esotericsoftware.kryo.Serializer<Image> {
		public Image read (com.esotericsoftware.kryo.Kryo kryo, Input input, Class<Image> type) {
			return new Image(input.readString(), input.readString(), input.readInt(true), input.readInt(true), kryo.readObject(
				input, Size.class));
		}

		public void write (com.esotericsoftware.kryo.Kryo kryo, Output output, Image obj) {
			output.writeString(obj.uri);
			output.writeString(obj.title);
			output.writeInt(obj.width, true);
			output.writeInt(obj.height, true);
			kryo.writeObject(output, obj.size);
		}
	}
}
