package serializers.colfer;

import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import data.media.*;
import serializers.SerClass;
import serializers.SerFeatures;
import serializers.SerFormat;
import serializers.SerGraph;
import serializers.Serializer;
import serializers.TestGroups;
import serializers.colfer.media.Image;
import serializers.colfer.media.Media;
import serializers.colfer.media.MediaContent;

import org.apache.commons.lang.StringUtils;


public class Colfer {

	public static void register(TestGroups groups) {
		groups.media.add(new Transformer(), new ColferSerializer(),
			new SerFeatures(
				SerFormat.BIN_CROSSLANG,
				SerGraph.FLAT_TREE,
				SerClass.CLASSES_KNOWN,
				"generated code"
			)
		);
	}

	static final class ColferSerializer extends Serializer<MediaContent> {

		private byte[] buffer = new byte[1024];


		@Override
		public MediaContent deserialize(byte[] array) throws Exception {
			MediaContent mc = new MediaContent();
			mc.unmarshal(array, 0);
			return mc;
		}

		@Override
		public byte[] serialize(MediaContent content) {
			while (true) {
				try {
					int n = content.marshal(buffer, 0);
					return Arrays.copyOf(buffer, n);
				} catch (BufferOverflowException e) {
					buffer = new byte[buffer.length * 2];
				}
			}
		}

		public String getName() {
			return "colfer";
		}

	}

	static final class Transformer extends MediaTransformer<MediaContent> {

		@Override
		public MediaContent[] resultArray(int size) {
			return new MediaContent[size];
		}

		@Override
		public data.media.MediaContent shallowReverse(MediaContent mc) {
			return new data.media.MediaContent(reverseMedia(mc.getMedia()), Collections.<data.media.Image>emptyList());
		}

		@Override
		public MediaContent forward(data.media.MediaContent src) {
			int i = src.images.size();
			Image[] images = new Image[i];
			while (--i >= 0) {
				images[i] = forwardImage(src.images.get(i));
			}

			MediaContent dst = new MediaContent();
			dst.images = images;
			dst.media = forwardMedia(src.media);
			return dst;
		}

		@Override
		public data.media.MediaContent reverse(MediaContent src) {
			List<data.media.Image> images = new ArrayList<>(src.images.length);
			for (Image image : src.images) {
				images.add(reverseImage(image));
			}

			return new data.media.MediaContent(reverseMedia(src.media), images);
		}

		private static Media forwardMedia(data.media.Media src) {
			Media dst = new Media();
			dst.uri = src.uri;
			dst.title = src.title;
			dst.width = src.width;
			dst.height = src.height;
			dst.format = src.format;
			dst.duration = src.duration;
			dst.size = src.size;
			dst.bitrate = src.bitrate;
			dst.hasBitrate = src.hasBitrate;
			dst.persons = StringUtils.join(src.persons, '\n');
			switch (src.player) {
				case FLASH:
					dst.flashPlay = true;
					break;
				case JAVA:
					dst.javaPlay = true;
					break;
			}
			if (src.copyright != null)
				dst.copyright = src.copyright;
			return dst;
		}

		private static data.media.Media reverseMedia(Media src) {
			data.media.Media dst = new data.media.Media();
			dst.uri = src.uri;
			dst.title = src.title;
			dst.width = src.width;
			dst.height = src.height;
			dst.format = src.format;
			dst.duration = src.duration;
			dst.size = src.size;
			dst.bitrate = src.bitrate;
			dst.hasBitrate = src.hasBitrate;
			dst.persons = Arrays.asList(StringUtils.split(src.persons, '\n'));
			if (src.flashPlay) {
				dst.player = data.media.Media.Player.FLASH;
			}
			if (src.javaPlay) {
				dst.player = data.media.Media.Player.JAVA;
			}
			if (src.copyright.length() != 0) {
				dst.copyright = src.copyright;
			}
			return dst;
		}

		private static Image forwardImage(data.media.Image src) {
			Image dst = new Image();
			dst.uri = src.uri;
			dst.title = src.title;
			dst.width = src.width;
			dst.height = src.height;
			switch (src.size) {
				case SMALL:
					dst.small = true;
					break;
				case LARGE:
					dst.large = true;
					break;
			}
			return dst;
		}

		private static data.media.Image reverseImage(Image src) {
			data.media.Image dst = new data.media.Image();
			dst.uri = src.uri;
			dst.title = src.title;
			dst.width = src.width;
			dst.height = src.height;
			if (src.small) {
				dst.size = data.media.Image.Size.SMALL;
			}
			if (src.large) {
				dst.size = data.media.Image.Size.LARGE;
			}
			return dst;
		}

	}
}
