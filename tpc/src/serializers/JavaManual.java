package serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

import data.media.*;

public class JavaManual
{
	public static void register(TestGroups groups)
	{
		groups.media.add(JavaBuiltIn.MediaTransformer, MediaContentSerializer);
	}

	// ------------------------------------------------------------
	// Serializer (just one)

	private static final Serializer<MediaContent> MediaContentSerializer = new MediaContentSerializer();

	private static final class MediaContentSerializer extends Serializer<MediaContent>
	{
		public MediaContentSerializer()
		{
			super();
		}

		public String getName() { return "java-manual"; }

		public MediaContent deserialize(byte[] array)
			throws IOException
		{
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(array));
			return readMediaContent(in);
		}

		public byte[] serialize(MediaContent data)
			throws IOException
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream oos = new DataOutputStream(baos);
			writeMediaContent(oos, data);
			oos.flush();
			return baos.toByteArray();
		}

		// MediaContent

		private static MediaContent readMediaContent(DataInputStream in)
			throws IOException
		{
			Media media = readMedia(in);
			int numImages = in.readInt();
			ArrayList<Image> images = new ArrayList<Image>(numImages);
			for (int i = 0; i < numImages; i++) {
				images.add(readImage(in));
			}
			return new MediaContent(media, images);
		}

		private static void writeMediaContent(DataOutputStream out, MediaContent m)
			throws IOException
		{
			writeMedia(out, m.media);
			out.writeInt(m.images.size());
			for (Image im : m.images) {
				writeImage(out, im);
			}
		}

		// Media

		private static Media readMedia(DataInputStream in)
			throws IOException
		{
			String uri = in.readUTF();
			String title = readMaybeString(in);
			int width = in.readInt();
			int height = in.readInt();
			String format = in.readUTF();
			long duration = in.readLong();
			long size = in.readLong();
			boolean hasBitrate = in.readBoolean();
			int bitrate = 0;
			if (hasBitrate) bitrate = in.readInt();
			int numPersons = in.readInt();
			ArrayList<String> persons = new ArrayList<String>(numPersons);
			for (int i = 0; i < numPersons; i++) {
				persons.add(in.readUTF());
			}
			Media.Player player = Media.Player.values()[in.readByte()];
			String copyright = readMaybeString(in);

			return new Media(
				uri, title, width, height, format, duration, size,
				bitrate, hasBitrate, persons, player, copyright);
		}

		private static void writeMedia(DataOutputStream out, Media m)
			throws IOException
		{
			out.writeUTF(m.uri);
			writeMaybeString(out, m.title);
			out.writeInt(m.width);
			out.writeInt(m.height);
			out.writeUTF(m.format);
			out.writeLong(m.duration);
			out.writeLong(m.size);
			writeMaybeInt(out, m.hasBitrate, m.bitrate);
			out.writeInt(m.persons.size());
			for (String p : m.persons) {
				out.writeUTF(p);
			}
			out.writeByte(m.player.ordinal());
			writeMaybeString(out, m.copyright);
		}

		// Image

		private static Image readImage(DataInputStream in)
			throws IOException
		{
			String uri = in.readUTF();
			String title = readMaybeString(in);
			int width = in.readInt();
			int height = in.readInt();
			Image.Size size = Image.Size.values()[in.readByte()];

			return new Image(uri, title, width, height, size);
		}

		private static void writeImage(DataOutputStream out, Image im)
			throws IOException
		{
			out.writeUTF(im.uri);
			writeMaybeString(out, im.title);
			out.writeInt(im.width);
			out.writeInt(im.height);
			out.writeByte(im.size.ordinal());
		}
	}

	public static void writeMaybeString(DataOutputStream out, String s)
		throws IOException
	{
		if (s != null) {
			out.writeBoolean(true);
			out.writeUTF(s);
		} else {
			out.writeBoolean(false);
		}
	}

	public static String readMaybeString(DataInputStream in)
		throws IOException
	{
		if (in.readBoolean()) {
			return in.readUTF();
		} else {
			return null;
		}
	}

	public static void writeMaybeInt(DataOutputStream out, boolean exists, int value)
		throws IOException
	{
		if (exists) {
			out.writeBoolean(true);
			out.writeInt(value);
		} else {
			out.writeBoolean(false);
		}
	}
}
