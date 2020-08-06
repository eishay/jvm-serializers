package data.media;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ArrayList;

// I _think_ this is for "obser" package -- should be renamed to reflect that

public class MediaContentCustom {
	private MediaContent content;

	public MediaContentCustom(MediaContent content) {
		this.content = content;
	}
	
	public MediaContent getContent() {
		return content;
	}

	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		writeMediaContent(s, content);
	}

	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		content = readMediaContent(s);
	}
	
    public static MediaContent readMediaContent(ObjectInputStream in) throws IOException
    {
        Media media = readMedia(in);
        int numImages = in.readInt();
        ArrayList<Image> images = new ArrayList<Image>(numImages);
        for (int i = 0; i < numImages; i++) {
            images.add(readImage(in));
        }
        return new MediaContent(media, images);
    }


	public static void writeMediaContent(ObjectOutputStream out, MediaContent m) throws IOException {
		writeMedia(out, m.getMedia());
		out.writeInt(m.getImages().size());
		for (Image im : m.getImages()) {
			writeImage(out, im);
		}
	}

	// Media

	private static Media readMedia(ObjectInputStream in) throws IOException {
		String uri = in.readUTF();
		String title = readMaybeString(in);
		int width = in.readInt();
		int height = in.readInt();
		String format = in.readUTF();
		long duration = in.readLong();
		long size = in.readLong();
		boolean hasBitrate = in.readBoolean();
		int bitrate = 0;
		if (hasBitrate)
			bitrate = in.readInt();
		int numPersons = in.readInt();
		ArrayList<String> persons = new ArrayList<String>(numPersons);
		for (int i = 0; i < numPersons; i++) {
			persons.add(in.readUTF());
		}
		Media.Player player = Media.Player.values()[in.readByte()];
		String copyright = readMaybeString(in);

		return new Media(uri, title, width, height, format, duration, size, bitrate, hasBitrate, persons, player, copyright);
	}

	private static void writeMedia(ObjectOutputStream out, Media m) throws IOException {
		out.writeUTF(m.getUri());
		writeMaybeString(out, m.getTitle());
		out.writeInt(m.getWidth());
		out.writeInt(m.getHeight());
		out.writeUTF(m.getFormat());
		out.writeLong(m.getDuration());
		out.writeLong(m.getSize());
		writeMaybeInt(out, m.hasBitrate, m.getBitrate());
		out.writeInt(m.getPersons().size());
		for (String p : m.getPersons()) {
			out.writeUTF(p);
		}
		out.writeByte(m.getPlayer().ordinal());
		writeMaybeString(out, m.getCopyright());
	}

	// Image

	private static Image readImage(ObjectInputStream in) throws IOException {
		String uri = in.readUTF();
		String title = readMaybeString(in);
		int width = in.readInt();
		int height = in.readInt();
		Image.Size size = Image.Size.values()[in.readByte()];

		return new Image(uri, title, width, height, size);
	}

	private static void writeImage(ObjectOutputStream out, Image im) throws IOException {
		out.writeUTF(im.getUri());
		writeMaybeString(out, im.getTitle());
		out.writeInt(im.getWidth());
		out.writeInt(im.getHeight());
		out.writeByte(im.getSize().ordinal());
	}

	public static void writeMaybeString(ObjectOutputStream out, String s) throws IOException {
		if (s != null) {
			out.writeBoolean(true);
			out.writeUTF(s);
		} else {
			out.writeBoolean(false);
		}
	}

	public static String readMaybeString(ObjectInputStream in) throws IOException {
		if (in.readBoolean()) {
			return in.readUTF();
		} else {
			return null;
		}
	}

	public static void writeMaybeInt(ObjectOutputStream out, boolean exists, int value) throws IOException {
		if (exists) {
			out.writeBoolean(true);
			out.writeInt(value);
		} else {
			out.writeBoolean(false);
		}
	}

}
