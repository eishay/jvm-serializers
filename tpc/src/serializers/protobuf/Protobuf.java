package serializers.protobuf;

import static serializers.protobuf.media.MediaContentHolder.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import serializers.Serializer;
import serializers.TestGroups;
import serializers.Transformer;

public class Protobuf
{
	public static void register(TestGroups groups)
	{
		groups.media.add(MediaTransformer, MediaSerializer);
	}

	// ------------------------------------------------------------
	// Serializers

	public static final Serializer<MediaContent> MediaSerializer = new Serializer<MediaContent>()
	{
		public MediaContent deserialize (byte[] array) throws Exception
		{
			return MediaContent.parseFrom(array);
		}

		public byte[] serialize(MediaContent content)
		{
			return content.toByteArray();
		}

		public String getName()
		{
			return "protobuf";
		}
	};

	// ------------------------------------------------------------
	// Transformers

	public static final Transformer<data.media.MediaContent,MediaContent> MediaTransformer = new Transformer<data.media.MediaContent,MediaContent>()
	{
		// ----------------------------------------------------------
		// Forward

		public MediaContent forward(data.media.MediaContent mc)
		{
			MediaContent.Builder cb = MediaContent.newBuilder();

			cb.setMedia(forwardMedia(mc.media));
			for (data.media.Image image : mc.images) {
				cb.addImage(forwardImage(image));
			}

			return cb.build();
		}

		private Media forwardMedia(data.media.Media media)
		{
			// Media
			Media.Builder mb = Media.newBuilder();
			mb.setUri(media.uri);
			if (media.title != null) mb.setTitle(media.title);
			mb.setWidth(media.width);
			mb.setHeight(media.height);
			mb.setFormat(media.format);
			mb.setDuration(media.duration);
			mb.setSize(media.size);
			if (media.hasBitrate) mb.setBitrate(media.bitrate);
			for (String person : media.persons) {
				mb.addPerson(person);
			}
			mb.setPlayer(forwardPlayer(media.player));
			if (media.copyright != null) mb.setCopyright(media.copyright);

			return mb.build();
		}

		public Media.Player forwardPlayer(data.media.Media.Player p)
		{
			switch (p) {
				case JAVA: return Media.Player.JAVA;
				case FLASH: return Media.Player.FLASH;
				default:
					throw new AssertionError("invalid case: " + p);
			}
		}

		private Image forwardImage(data.media.Image image)
		{
			Image.Builder ib = Image.newBuilder();
			ib.setUri(image.uri);
			if (image.title != null) ib.setTitle(image.title);
			ib.setWidth(image.width);
			ib.setHeight(image.height);
			ib.setSize(forwardSize(image.size));
			return ib.build();
		}

		public Image.Size forwardSize(data.media.Image.Size s)
		{
			switch (s) {
				case SMALL: return Image.Size.SMALL;
				case LARGE: return Image.Size.LARGE;
				default:
					throw new AssertionError("invalid case: " + s);
			}
		}

		// ----------------------------------------------------------
		// Reverse

		public data.media.MediaContent reverse(MediaContent mc)
		{
			List<data.media.Image> images = new ArrayList<data.media.Image>(mc.getImageCount());

			for (Image image : mc.getImageList()) {
				images.add(reverseImage(image));
			}

			return new data.media.MediaContent(reverseMedia(mc.getMedia()), images);
		}

		private data.media.Media reverseMedia(Media media)
		{
			// Media
			return new data.media.Media(
				media.getUri(),
				media.hasTitle() ? media.getTitle() : null,
				media.getWidth(),
				media.getHeight(),
				media.getFormat(),
				media.getDuration(),
				media.getSize(),
				media.hasBitrate() ? media.getBitrate() : 0,
				media.hasBitrate(),
				new ArrayList<String>(media.getPersonList()),
				reversePlayer(media.getPlayer()),
				media.hasCopyright() ? media.getCopyright() : null
			);
		}

		public data.media.Media.Player reversePlayer(Media.Player p)
		{
			switch (p) {
				case JAVA:  return data.media.Media.Player.JAVA;
				case FLASH: return data.media.Media.Player.FLASH;
				default:
					throw new AssertionError("invalid case: " + p);
			}
		}

		private data.media.Image reverseImage(Image image)
		{
			return new data.media.Image(
				image.getUri(),
				image.hasTitle() ? image.getTitle() : null,
				image.getWidth(),
				image.getHeight(),
				reverseSize(image.getSize()));
		}

		public data.media.Image.Size reverseSize(Image.Size s)
		{
			switch (s) {
				case SMALL: return data.media.Image.Size.SMALL;
				case LARGE: return data.media.Image.Size.LARGE;
				default:
					throw new AssertionError("invalid case: " + s);
			}
		}

		public data.media.MediaContent shallowReverse(MediaContent mc)
		{
			return new data.media.MediaContent(reverseMedia(mc.getMedia()), Collections.<data.media.Image>emptyList());
		}
	};
}
