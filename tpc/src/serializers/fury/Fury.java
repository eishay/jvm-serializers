
package serializers.fury;

import data.media.Image;
import data.media.Image.Size;
import data.media.Media;
import data.media.MediaContent;
import data.media.MediaTransformer;
import io.fury.config.FuryBuilder;
import serializers.JavaBuiltIn;
import serializers.SerClass;
import serializers.SerFeatures;
import serializers.SerFormat;
import serializers.SerGraph;
import serializers.Serializer;
import serializers.TestGroup;
import serializers.TestGroups;
import serializers.Transformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;

public class Fury {

	public static void register (TestGroups groups) {
		register(groups.media, JavaBuiltIn.mediaTransformer, MediaTypeHandler);
	}


	public static final MediaTransformer<MediaContent> mediaTransformer = new MediaTransformer<MediaContent>() {

		@Override
		public MediaContent[] resultArray(int size) { return new MediaContent[size]; }

		public MediaContent forward(MediaContent mc) {
			return mc;
		}

		public MediaContent reverse(MediaContent mc)
		{
			return (mc);
		}

		public MediaContent shallowReverse(MediaContent mc)
		{
			return new MediaContent((mc.media), Collections.<Image>emptyList());
		}
	};

	private static <T, S> void register (TestGroup<T> group, Transformer<T, S> transformer, TypeHandler<S> handler) {
		group.add(transformer, new BasicSerializer<>(handler, "fury-auto-flat", false, false), //
			new SerFeatures( //
				SerFormat.BINARY, //
				SerGraph.FLAT_TREE, //
				SerClass.ZERO_KNOWLEDGE, //
				"no class registration, no references"));
		group.add(transformer, new BasicSerializer<>(handler, "fury-auto", false, true), //
			new SerFeatures(SerFormat.BINARY, //
				SerGraph.FULL_GRAPH, //
				SerClass.ZERO_KNOWLEDGE, //
				"no class registration, references"));

		group.add(transformer, new BasicSerializer<>(handler, "fury", true, false), //
			new SerFeatures(SerFormat.BINARY, //
				SerGraph.FLAT_TREE, //
				SerClass.CLASSES_KNOWN, //
				"class registration, no references (typical usage)"));
		group.add(transformer, new BasicSerializer<>(handler, "fury-registered", true, true), //
			new SerFeatures(SerFormat.BINARY, //
				SerGraph.FULL_GRAPH, //
				SerClass.CLASSES_KNOWN, //
				"class registration, references (typical usage)"));
		group.add(transformer, new BasicSerializer<>(handler, "fury-fastest", true, false, false), //
			new SerFeatures(SerFormat.BINARY, //
				SerGraph.FLAT_TREE, //
				SerClass.CLASSES_KNOWN, //
				"class registration, no references, no compression (fastest)"));
	}

	// ------------------------------------------------------------
	// Serializers

	/** This is basic fury usage: register classes or not, enable references or not. */
	public static class BasicSerializer<T> extends Serializer<T> {
		private final Class<T> type;
		final io.fury.Fury fury;
		private final String name;

		public BasicSerializer (
			TypeHandler<T> handler, String name, boolean register, boolean references) {
			this(handler, name, register, references, true);
		}

		public BasicSerializer (
			TypeHandler<T> handler, String name, boolean register, boolean references, boolean compression) {
			this.type = handler.type;
			this.name = name;
			FuryBuilder builder = io.fury.Fury.builder()
				.withRefTracking(references)
				.requireClassRegistration(register);
			if (!compression) {
				builder.withNumberCompressed(false);
				builder.withStringCompressed(false);
			}
			fury = builder.build();
			if (register) {
				handler.register(fury);
			}
		}

		public T deserialize (byte[] array) {
			return fury.deserializeJavaObject(array, type);
		}

		public byte[] serialize (T content) {
			return fury.serializeJavaObject(content);
		}

		public void serializeItems (T[] items, OutputStream outStream) throws Exception {
			for (T item : items) {
				fury.serializeJavaObject(outStream, item);
			}
			outStream.flush();
		}

		@SuppressWarnings("unchecked")
		public T[] deserializeItems (InputStream inStream, int numberOfItems) throws IOException {
			MediaContent[] result = new MediaContent[numberOfItems];
			for (int i = 0; i < numberOfItems; ++i)
				result[i] = fury.deserializeJavaObject(inStream, MediaContent.class);
			return (T[])result;
		}

		public final String getName () {
			return name;
		}
	}

	// ------------------------------------------------------------

	public static abstract class TypeHandler<T> {
		public final Class<T> type;

		protected TypeHandler (Class<T> type) {
			this.type = type;
		}

		public abstract void register (io.fury.Fury fury);

		public abstract void optimize (io.fury.Fury fury);

		public abstract void registerCustom (io.fury.Fury fury);
	}

	// ------------------------------------------------------------
	// Media

	public static final TypeHandler<MediaContent> MediaTypeHandler = new TypeHandler<MediaContent>(MediaContent.class) {
		public void register (io.fury.Fury fury) {
			fury.register(MediaContent.class);
			fury.register(Media.Player.class);
			fury.register(Media.class);
			fury.register(Size.class);
			fury.register(Image.class);
		}

		public void optimize (io.fury.Fury fury) {
		}

		public void registerCustom (io.fury.Fury fury) {
		}
	};
}
