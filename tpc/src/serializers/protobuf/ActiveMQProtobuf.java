package serializers.protobuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import data.media.MediaTransformer;

import serializers.*;
import serializers.activemq.media.MediaContentHolder.Image;
import serializers.activemq.media.MediaContentHolder.Media;
import serializers.activemq.media.MediaContentHolder.MediaContent;
import serializers.activemq.media.MediaContentHolder.Image.ImageBean;
import serializers.activemq.media.MediaContentHolder.Image.Size;
import serializers.activemq.media.MediaContentHolder.Media.MediaBean;
import serializers.activemq.media.MediaContentHolder.Media.Player;
import serializers.activemq.media.MediaContentHolder.MediaContent.MediaContentBean;
import serializers.activemq.media.MediaContentHolder.MediaContent.MediaContentBuffer;

public class ActiveMQProtobuf
{
	public static void register(TestGroups groups)
	{
		groups.media.add(mediaTransformer, MediaSerializer,
                new SerFeatures(
                        SerFormat.BINARY_CROSSLANG,
                        SerGraph.FLAT_TREE,
                        SerClass.CLASSES_KNOWN,
                        ""
                )
        );
	}

	// ------------------------------------------------------------
	// Serializers

	public static final Serializer<MediaContent> MediaSerializer = new Serializer<MediaContent>()
	{
		public MediaContent deserialize(byte[] array) throws Exception
		{
			return MediaContentBuffer.parseUnframed(array);
		}

		public byte[] serialize(MediaContent content)
		{
			return content.freeze().toUnframedByteArray();
		}

		public String getName()
		{
			return "protobuf/activemq+alt";
		}
	};

	// ------------------------------------------------------------
	// Transformers

	public static final MediaTransformer<MediaContent> mediaTransformer = new MediaTransformer<MediaContent>()
	{
                @Override
                public MediaContent[] resultArray(int size) { return new MediaContent[size]; }
	    
		// ----------------------------------------------------------
		// Forward

		public MediaContent forward(data.media.MediaContent mc)
		{
			MediaContentBean cb = new MediaContentBean();

			cb.setMedia(forwardMedia(mc.media));
			for (data.media.Image image : mc.images) {
				cb.addImage(forwardImage(image));
			}

			return cb;
		}

		private Media forwardMedia(data.media.Media media)
		{
			// Media
			MediaBean mb = new MediaBean();
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

			return mb;
		}

		public Player forwardPlayer(data.media.Media.Player p)
		{
			switch (p) {
				case JAVA: return Player.JAVA;
				case FLASH: return Player.FLASH;
				default:
					throw new AssertionError("invalid case: " + p);
			}
		}

		private Image forwardImage(data.media.Image image)
		{
			ImageBean ib = new ImageBean();
			ib.setUri(image.uri);
			if (image.title != null) ib.setTitle(image.title);
			ib.setWidth(image.width);
			ib.setHeight(image.height);
			ib.setSize(forwardSize(image.size));
			return ib;
		}

		public Size forwardSize(data.media.Image.Size s)
		{
			switch (s) {
				case SMALL: return Size.SMALL;
				case LARGE: return Size.LARGE;
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

		public data.media.Media.Player reversePlayer(Player p)
		{
			if (p == Player.JAVA) return data.media.Media.Player.JAVA;
			if (p == Player.FLASH) return data.media.Media.Player.FLASH;
			throw new AssertionError("invalid case: " + p);
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

		public data.media.Image.Size reverseSize(Size s)
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
