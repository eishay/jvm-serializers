package serializers.datakernel;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;
import data.media.MediaTransformer;
import io.datakernel.serializer.BufferSerializer;
import io.datakernel.serializer.SerializationInputBuffer;
import io.datakernel.serializer.SerializationOutputBuffer;
import io.datakernel.serializer.SerializerBuilder;
import io.datakernel.serializer.annotations.Serialize;
import io.datakernel.serializer.annotations.SerializeNullable;
import io.datakernel.serializer.annotations.SerializeVarLength;
import serializers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataKernelSerializer {

	public static void register(TestGroups groups) {
		register(groups.media, DataKernelSerializer.mediaTransformer);
	}

	private static <T, S> void register(TestGroup<T> group, Transformer<T, S> transformer) {
		group.add(transformer, new DefaultSerializer<S>(),
				new SerFeatures(SerFormat.BINARY,
						SerGraph.FLAT_TREE,
						SerClass.MANUAL_OPT,
						"manually optimized"));
	}

	public static class DefaultSerializer<T> extends Serializer<T> {
		private BufferSerializer<T> serializer;
		private byte[] array = new byte[20000];
		SerializationOutputBuffer output = new SerializationOutputBuffer(array);

		public DefaultSerializer() {
			serializer = SerializerBuilder
					.newDefaultInstance(ClassLoader.getSystemClassLoader())
					.create(DMediaContent.class);
		}

		@Override
		public T deserialize(byte[] array) throws Exception {
			return serializer.deserialize(new SerializationInputBuffer(array, 0));
		}

		@Override
		public byte[] serialize(T content) throws Exception {
			output.position(0);
			serializer.serialize(output, content);

			byte[] bytes = new byte[output.position()];
			System.arraycopy(array, 0, bytes, 0, output.position());
			return bytes;
		}

		@Override
		public String getName() {
			return "datakernel";
		}
	}

	public static class DImage {
		public enum Size {
			SMALL, LARGE
		}

		@Serialize(order = 1)
		public String uri;

		@Serialize(order = 2)
		@SerializeNullable
		public String title; // can be null;

		@Serialize(order = 3)
		@SerializeVarLength
		public int width;

		@Serialize(order = 4)
		@SerializeVarLength
		public int height;

		@Serialize(order = 5)
		public Size size;

		public DImage(String uri, String title, int width, int height, Size size) {
			this.uri = uri;
			this.title = title;
			this.width = width;
			this.height = height;
			this.size = size;
		}

		public DImage() {}
	}

	public static class DMedia {
		public enum Player {
			JAVA, FLASH
		}

		@Serialize(order = 1)
		public String uri;

		@Serialize(order = 2)
		@SerializeNullable
		public String title; // can be null

		@Serialize(order = 3)
		@SerializeVarLength
		public int width;

		@Serialize(order = 4)
		@SerializeVarLength
		public int height;

		@Serialize(order = 5)
		public String format;

		@Serialize(order = 6)
		public long duration;

		@Serialize(order = 7)
		public long size;

		@Serialize(order = 8)
		public int bitrate; // can be unset

		@Serialize(order = 9)
		public boolean hasBitrate;

		@Serialize(order = 10)
		public List<String> persons;

		@Serialize(order = 11)
		public Player player;

		@Serialize(order = 12)
		@SerializeNullable
		public String copyright; // can be null

		public DMedia(String uri, String title, int width, int height,
		              String format, long duration, long size, int bitrate,
		              boolean hasBitrate, List<String> persons, Player player,
		              String copyright) {
			this.uri = uri;
			this.title = title;
			this.width = width;
			this.height = height;
			this.format = format;
			this.duration = duration;
			this.size = size;
			this.bitrate = bitrate;
			this.hasBitrate = hasBitrate;
			this.persons = persons;
			this.player = player;
			this.copyright = copyright;
		}

		public DMedia() {}
	}

	public static class DMediaContent {
		@Serialize(order = 1)
		public List<DImage> DImages;

		@Serialize(order = 2)
		public DMedia DMedia;

		public DMediaContent(List<DImage> list, DMedia DMedia) {
			this.DImages = list;
			this.DMedia = DMedia;
		}

		public DMediaContent() {}
	}

	public static MediaTransformer<DMediaContent> mediaTransformer = new MediaTransformer<DMediaContent>() {
		@Override
		public DMediaContent forward(MediaContent a) {
			return new DMediaContent(forwardImages(a.getImages()), forwardMedia(a.getMedia()));
		}

		@Override
		public MediaContent reverse(DMediaContent a) {
			return new MediaContent(reverseMedia(a.DMedia), reverseImages(a.DImages));
		}

		@Override
		public MediaContent shallowReverse(DMediaContent a) {
			return new MediaContent(reverseMedia(a.DMedia), Collections.<Image>emptyList());
		}
	};

	private static List<DImage> forwardImages(List<Image> images) {
		List<DImage> list = new ArrayList<>(images.size());
		for (Image image : images) {
			list.add(forwardImage(image));
		}
		return list;
	}

	private static DMedia forwardMedia(Media m) {
		return new DMedia(m.uri, m.title, m.width, m.height,
				m.format, m.duration, m.size, m.bitrate,
				m.hasBitrate, m.persons, forwardPlayer(m.player),
				m.copyright);
	}

	private static DImage forwardImage(Image i) {
		return new DImage(i.uri, i.title, i.width, i.height, forwardSize(i.size));
	}

	private static List<Image> reverseImages(List<DImage> DImages) {
		List<Image> list = new ArrayList<>(DImages.size());
		for (DImage DImage : DImages) {
			list.add(reverseImage(DImage));
		}
		return list;
	}

	private static Media reverseMedia(DMedia m) {
		return new Media(m.uri, m.title, m.width, m.height,
				m.format, m.duration, m.size, m.bitrate,
				m.hasBitrate, m.persons, reversePlayer(m.player),
				m.copyright);
	}

	private static Image reverseImage(DImage i) {
		return new Image(i.uri, i.title, i.width, i.height, reverseSize(i.size));
	}

	private static Image.Size reverseSize(DImage.Size oldSize) {
		return Image.Size.values()[(oldSize.ordinal())];
	}

	private static Media.Player reversePlayer(DMedia.Player oldPlayer) {
		return Media.Player.values()[(oldPlayer.ordinal())];
	}

	private static DImage.Size forwardSize(Image.Size oldSize) {
		return DImage.Size.valueOf(oldSize.name());
	}

	private static DMedia.Player forwardPlayer(Media.Player oldPlayer) {
		return DMedia.Player.valueOf(oldPlayer.name());
	}
}
