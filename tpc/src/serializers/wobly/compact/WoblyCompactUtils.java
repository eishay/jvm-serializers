package serializers.wobly.compact;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

import serializers.Serializer;
import serializers.Transformer;

public class WoblyCompactUtils {
	public static WMedia forwardMedia(Media a) {
		return new WMedia(a.uri, a.title, a.width, a.height, a.format, a.duration, a.size, a.bitrate, a.persons, a.player, a.copyright);
	}
	public static Media reverseMedia(WMedia a) {
		return new Media(a.uri, a.title, a.width, a.height, a.format, a.duration, a.size, a.bitrate, true, a.persons, a.player, a.copyright);
	}
	public static WImage forwardImage(Image a) {
		return new WImage(a.uri, a.title, a.width, a.height, a.size);
	}
	public static Image reverseImage(WImage a) {
		return new Image(a.uri, a.title, a.width, a.height, a.size);
	}	
	public static List<WImage> forwardImages(List<Image> a) {
		ArrayList<WImage> images = new ArrayList<WImage>(a.size());
		for (Image image : a) {
			images.add(forwardImage(image));
		}
		return images;
	}
	public static List<Image> reverseImages(List<WImage> a) {
		ArrayList<Image> images = new ArrayList<Image>(a.size());
		for (WImage image : a) {
			images.add(reverseImage(image));
		}
		return images;
	}	
	
	public static final class WoblyTransformer extends Transformer<MediaContent, WMediaContent> {

		@Override
		public WMediaContent forward(MediaContent a) {
			return new WMediaContent(forwardImages(a.images), forwardMedia(a.media));
		}

		@Override
		public MediaContent reverse(WMediaContent a) {
			return new MediaContent(reverseMedia(a.media), reverseImages(a.images));
		}

		@Override
		public MediaContent shallowReverse(WMediaContent a) {
			return new MediaContent(reverseMedia(a.media), Collections.<Image>emptyList());
		}
	}
	public static final class WoblySerializer extends Serializer<WMediaContent>
	{
		private static final ByteBuffer buffer = ByteBuffer.allocate(2048);

		@Override
		public WMediaContent deserialize(byte[] array)
				throws Exception {
			return WMediaContent.read(array);
		}

		@Override
		public byte[] serialize(WMediaContent content)
				throws Exception {
			buffer.clear();
			content.write(buffer);
			return Arrays.copyOf(buffer.array(), buffer.position());
		}

		@Override
		public String getName() {
			return "wobly-compact";
		}
	}
}
