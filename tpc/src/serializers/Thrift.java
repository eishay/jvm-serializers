package serializers;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

import data.media.MediaTransformer;

import serializers.thrift.media.*;

public class Thrift
{
	public static void register(TestGroups groups)
	{
		groups.media.add(mediaTransformer, new MediaSerializer(ProtocolSpec.DefaultBinary),
                new SerFeatures(
                        SerFormat.BIN_CROSSLANG,
                        SerGraph.FLAT_TREE,
                        SerClass.CLASSES_KNOWN,
                        ""
                )
        );
		groups.media.add(mediaTransformer, new MediaSerializer(ProtocolSpec.CompactBinary),
                new SerFeatures(
                        SerFormat.BIN_CROSSLANG,
                        SerGraph.FLAT_TREE,
                        SerClass.CLASSES_KNOWN,
                        ""
                )
        );
	}

	// ------------------------------------------------------------
	// Serializers

	public static final class ProtocolSpec
	{
		public final TProtocolFactory factory;
		public final String suffix;

		public ProtocolSpec(TProtocolFactory factory, String suffix)
		{
			this.factory = factory;
			this.suffix = suffix;
		}

		public static final ProtocolSpec DefaultBinary = new ProtocolSpec(new TBinaryProtocol.Factory(), "");
		public static final ProtocolSpec CompactBinary = new ProtocolSpec(new TCompactProtocol.Factory(), "-compact");
	}

	public static final class MediaSerializer extends Serializer<MediaContent>
	{
		private final ProtocolSpec spec;

		public MediaSerializer(ProtocolSpec spec)
		{
			this.spec = spec;
		}

		public MediaContent deserialize(byte[] array) throws Exception
		{
			MediaContent content = new MediaContent();
			new TDeserializer(spec.factory).deserialize(content, array);
			return content;
		}

		public byte[] serialize(MediaContent content) throws Exception
		{
			return new TSerializer(spec.factory).serialize(content);
		}

		public String getName()
		{
			return "thrift" + spec.suffix;
		}

	        @Override
	        public final void serializeItems(MediaContent[] items, OutputStream out0) throws Exception
	        {
	            DataOutputStream out = new DataOutputStream(out0);
	            TSerializer ser = new TSerializer(spec.factory);
	            for (MediaContent item : items) {
	                byte[] data = ser.serialize(item);
	                out.writeInt(data.length);
	                out.write(data);
	            }
	            // should we write end marker (length of 0) or not? For now, omit it
	            out.flush();
	        }

	        @Override
	        public MediaContent[] deserializeItems(InputStream in0, int numberOfItems) throws Exception 
	        {
	            DataInputStream in = new DataInputStream(in0);
	            TDeserializer deser = new TDeserializer(spec.factory);
	            MediaContent[] result = new MediaContent[numberOfItems];
	            for (int i = 0; i < numberOfItems; ++i) {
	                int len = in.readInt();
	                byte[] data = new byte[len];
	                in.readFully(data);
                        MediaContent content = new MediaContent();
                        deser.deserialize(content, data);
	                result[i] = content;
	            }
	            return result;
	        }	
	}

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
