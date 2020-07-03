
package serializers.kryo;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.kryo.serializers.FieldSerializer.CachedField;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import data.media.Image;
import data.media.Image.Size;
import data.media.Media;
import data.media.MediaContent;
import serializers.JavaBuiltIn;
import serializers.SerClass;
import serializers.SerFeatures;
import serializers.SerFormat;
import serializers.SerGraph;
import serializers.Serializer;
import serializers.TestGroup;
import serializers.TestGroups;
import serializers.Transformer;

public class Kryo {

	public static void register (TestGroups groups) {
		register(groups.media, JavaBuiltIn.mediaTransformer, MediaTypeHandler);
	}

	private static <T, S> void register (TestGroup<T> group, Transformer<T, S> transformer, TypeHandler<S> handler) {
		Output output = new Output(Serializer.BUFFER_SIZE);
		Input input = new Input(output.getBuffer());

		group.add(transformer, new BasicSerializer(handler, "kryo-auto-flat", false, false, output, input), //
			new SerFeatures( //
				SerFormat.BINARY, //
				SerGraph.FLAT_TREE, //
				SerClass.ZERO_KNOWLEDGE, //
				"no class registration, no references"));
		group.add(transformer, new BasicSerializer(handler, "kryo-auto", false, true, output, input), //
			new SerFeatures(SerFormat.BINARY, //
				SerGraph.FULL_GRAPH, //
				SerClass.ZERO_KNOWLEDGE, //
				"no class registration, references"));

		group.add(transformer, new BasicSerializer(handler, "kryo-registered-flat", true, false, output, input), //
			new SerFeatures(SerFormat.BINARY, //
				SerGraph.FLAT_TREE, //
				SerClass.CLASSES_KNOWN, //
				"class registration, no references (typical usage)"));
		group.add(transformer, new BasicSerializer(handler, "kryo-registered", true, true, output, input), //
			new SerFeatures(SerFormat.BINARY, //
				SerGraph.FULL_GRAPH, //
				SerClass.CLASSES_KNOWN, //
				"class registration, references (typical usage)"));

		group.add(transformer, new OptimizedSerializer(handler, "kryo-opt", output, input), //
			new SerFeatures(SerFormat.BINARY, //
				SerGraph.FLAT_TREE, //
				SerClass.MANUAL_OPT, //
				"manual optimization"));

		group.add(transformer, new CustomSerializer(handler, "kryo-manual", output, input), //
			new SerFeatures(SerFormat.BINARY, //
				SerGraph.FLAT_TREE, //
				SerClass.MANUAL_OPT, //
				"complete manual optimization"));

		// Unsfe buffers can be much faster with some data, eg large primitive arrays with variable length encoding disabled.
		// UnsafeOutput unsafeOutput = new UnsafeOutput(Serializer.BUFFER_SIZE);
		// UnsafeInput unsafeInput = new UnsafeInput(unsafeOutput.getBuffer());
		// unsafeOutput.setVariableLengthEncoding(false);
		// unsafeInput.setVariableLengthEncoding(false);
		// group.add(transformer, new CustomSerializer(handler, "kryo-manual-unsafe", unsafeOutput, unsafeInput), //
		// new SerFeatures(SerFormat.BINARY, //
		// SerGraph.FLAT_TREE, //
		// SerClass.MANUAL_OPT, //
		// "manually optimized, unsafe buffers"));
	}

	// ------------------------------------------------------------
	// Serializers

	/** This is basic Kryo usage: register classes or not, enable references or not. */
	public static class BasicSerializer<T> extends Serializer<T> {
		private final Class<T> type;
		final com.esotericsoftware.kryo.Kryo kryo;

		private final String name;
		private final Output output;
		private final Input input;

		public BasicSerializer (TypeHandler<T> handler, String name, boolean register, boolean references, Output output,
			Input input) {
			this.type = handler.type;
			this.name = name;
			this.output = output;
			this.input = input;
			kryo = new com.esotericsoftware.kryo.Kryo();
			kryo.setReferences(references);
			kryo.setRegistrationRequired(register);
			if (register) handler.register(this.kryo);
		}

		public T deserialize (byte[] array) {
			input.setBuffer(array);
			return kryo.readObject(input, type);
		}

		public byte[] serialize (T content) {
			output.setPosition(0);
			kryo.writeObject(output, content);
			return output.toBytes();
		}

		public void serializeItems (T[] items, OutputStream outStream) throws Exception {
			output.setOutputStream(outStream);
			for (int i = 0, n = items.length; i < n; ++i)
				kryo.writeObject(output, items[i]);
			output.flush();
		}

		public T[] deserializeItems (InputStream inStream, int numberOfItems) throws IOException {
			input.setInputStream(inStream);
			MediaContent[] result = new MediaContent[numberOfItems];
			for (int i = 0; i < numberOfItems; ++i)
				result[i] = kryo.readObject(input, MediaContent.class);
			return (T[])result;
		}

		public final String getName () {
			return name;
		}
	}

	/** This shows how to configure individual Kryo serializers to reduce the serialized bytes. */
	public static class OptimizedSerializer<T> extends BasicSerializer<T> {
		public OptimizedSerializer (TypeHandler<T> handler, String name, Output output, Input input) {
			super(handler, name, true, false, output, input);
			handler.optimize(this.kryo);
		}
	}

	/** This shows how to use hand written serialization code with Kryo, while still leveraging Kryo for most of the work. A
	 * serializer for each class can be implemented, as it is here, or the classes to be serialized can implement KryoSerializable
	 * and host their own serialization code (similar to java.io.Externalizable). */
	public static class CustomSerializer<T> extends BasicSerializer<T> {
		public CustomSerializer (TypeHandler<T> handler, String name, Output output, Input input) {
			super(handler, name, true, false, output, input);
			handler.registerCustom(this.kryo);
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
			imageSerializer.setAcceptsNull(false);
			imageSerializer.getField("title").setCanBeNull(true);

			FieldSerializer mediaContentSerializer = (FieldSerializer)kryo.getSerializer(MediaContent.class);
			mediaContentSerializer.setAcceptsNull(false);

			CachedField imagesField = mediaContentSerializer.getField("images");
			CollectionSerializer imagesSerializer = new CollectionSerializer();
			imagesSerializer.setElementsCanBeNull(false);
			imagesField.setValueClass(ArrayList.class, imagesSerializer);

			FieldSerializer mediaSerializer = new FieldSerializer(kryo, Media.class);
			mediaSerializer.setAcceptsNull(false);
			mediaSerializer.getField("title").setCanBeNull(true);
			mediaSerializer.getField("copyright").setCanBeNull(true);

			CachedField mediaField = mediaContentSerializer.getField("media");
			mediaField.setValueClass(Media.class, mediaSerializer);

			CachedField personsField = mediaSerializer.getField("persons");
			CollectionSerializer personsSerializer = new CollectionSerializer();
			personsSerializer.setElementsCanBeNull(false);
			personsField.setValueClass(ArrayList.class, personsSerializer);
		}

		public void registerCustom (com.esotericsoftware.kryo.Kryo kryo) {
			MediaSerializer mediaSerializer = new MediaSerializer(kryo);
			ImageSerializer imageSerializer = new ImageSerializer();
			kryo.register(Image.class, imageSerializer);
			kryo.register(Media.class, mediaSerializer);
			kryo.register(MediaContent.class, new MediaContentSerializer(kryo, mediaSerializer, imageSerializer));
		}
	};

	static class MediaContentSerializer extends com.esotericsoftware.kryo.Serializer<MediaContent> {
		private final MediaSerializer mediaSerializer;
		private final CollectionSerializer imagesSerializer;

		public MediaContentSerializer (com.esotericsoftware.kryo.Kryo kryo, MediaSerializer mediaSerializer,
			ImageSerializer imageSerializer) {
			this.mediaSerializer = mediaSerializer;
			imagesSerializer = new CollectionSerializer();
			imagesSerializer.setElementsCanBeNull(false);
			imagesSerializer.setElementClass(Image.class, imageSerializer);
		}

		public MediaContent read (com.esotericsoftware.kryo.Kryo kryo, Input input, Class<? extends MediaContent> type) {
			return new MediaContent(kryo.readObject(input, Media.class), kryo.readObject(input, ArrayList.class, imagesSerializer));
		}

		public void write (com.esotericsoftware.kryo.Kryo kryo, Output output, MediaContent obj) {
			kryo.writeObject(output, obj.media, mediaSerializer);
			kryo.writeObject(output, obj.images, imagesSerializer);
		}
	}

	static class MediaSerializer extends com.esotericsoftware.kryo.Serializer<Media> {
		static private final Media.Player[] players = Media.Player.values();

		private final CollectionSerializer personsSerializer;

		public MediaSerializer (final com.esotericsoftware.kryo.Kryo kryo) {
			personsSerializer = new CollectionSerializer();
			personsSerializer.setElementsCanBeNull(false);
			personsSerializer.setElementClass(String.class, kryo.getSerializer(String.class));
		}

		public Media read (com.esotericsoftware.kryo.Kryo kryo, Input input, Class<? extends Media> type) {
			return new Media(input.readString(), input.readString(), input.readInt(true), input.readInt(true), input.readString(),
				input.readLong(true), input.readLong(true), input.readInt(true), input.readBoolean(),
				kryo.readObject(input, ArrayList.class, personsSerializer), players[input.readInt(true)], input.readString());
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
			kryo.writeObject(output, obj.persons, personsSerializer);
			output.writeInt(obj.player.ordinal(), true);
			output.writeString(obj.copyright);
		}
	}

	static class ImageSerializer extends com.esotericsoftware.kryo.Serializer<Image> {
		static private final Size[] sizes = Size.values();

		public Image read (com.esotericsoftware.kryo.Kryo kryo, Input input, Class<? extends Image> type) {
			return new Image(input.readString(), input.readString(), input.readInt(true), input.readInt(true),
				sizes[input.readInt(true)]);
		}

		public void write (com.esotericsoftware.kryo.Kryo kryo, Output output, Image obj) {
			output.writeString(obj.uri);
			output.writeString(obj.title);
			output.writeInt(obj.width, true);
			output.writeInt(obj.height, true);
			output.writeInt(obj.size.ordinal(), true);
		}
	}
}
