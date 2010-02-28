package serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import serializers.thrift.media.*;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TIOStreamTransport;

public class Thrift
{
	public static void register(TestGroups groups)
	{
		groups.media.add(MediaTransformer, MediaSerializer);
	}

	// ------------------------------------------------------------
	// Serializers

	public static final Serializer<MediaContent> MediaSerializer = new Serializer<MediaContent>()
	{
		public MediaContent deserialize(byte[] array) throws Exception
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(array);
			TIOStreamTransport trans = new TIOStreamTransport(bais);
			TBinaryProtocol oprot = new TBinaryProtocol(trans);
			MediaContent content = new MediaContent();
			content.read(oprot);
			return content;
		}

		public byte[] serialize(MediaContent content) throws Exception
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			TIOStreamTransport trans = new TIOStreamTransport(baos);
			TBinaryProtocol oprot = new TBinaryProtocol(trans);
			content.write(oprot);
			return baos.toByteArray();
		}

		public String getName()
		{
			return "thrift";
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
			MediaContent cb = new MediaContent();

			cb.setMedia(forwardMedia(mc.media));
			for (data.media.Image image : mc.images) {
				cb.addToImage(forwardImage(image));
			}

			return cb;
		}

		private Media forwardMedia(data.media.Media media)
		{
			// Media
			Media mb = new Media();
			mb.setUri(media.uri);
			if (media.title != null) mb.setTitle(media.title);
			mb.setWidth(media.width);
			mb.setHeight(media.height);
			mb.setFormat(media.format);
			mb.setDuration(media.duration);
			mb.setSize(media.size);
			if (media.hasBitrate) mb.setBitrate(media.bitrate);
			for (String person : media.persons) {
				mb.addToPerson(person);
			}
			mb.setPlayer(forwardPlayer(media.player));

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
			Image ib = new Image();
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
			List<data.media.Image> images = new ArrayList<data.media.Image>(mc.getImageSize());

			for (Image image : mc.getImage()) {
				images.add(reverseImage(image));
			}

			return new data.media.MediaContent(reverseMedia(mc.getMedia()), images);
		}

		private data.media.Media reverseMedia(Media media)
		{
			// Media
			return new data.media.Media(
				media.getUri(),
				media.isSetTitle() ? media.getTitle() : null,
				media.getWidth(),
				media.getHeight(),
				media.getFormat(),
				media.getDuration(),
				media.getSize(),
				media.isSetBitrate() ? media.getBitrate() : 0,
				media.isSetBitrate(),
				new ArrayList<String>(media.getPerson()),
				reversePlayer(media.getPlayer()),
				media.isSetCopyright() ? media.getCopyright() : null
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
				image.isSetTitle() ? image.getTitle() : null,
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
