package serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;
import data.media.MediaTransformer;

public class JavaBuiltIn
{
	public static void register(TestGroups groups)
	{
		groups.media.add(mediaTransformer, new GenericSerializer<MediaContent>("java-built-in"));
	}

	// ------------------------------------------------------------
	// Serializer (just one)

	public static class GenericSerializer<T> extends Serializer<T>
	{
		private final String name;
		public GenericSerializer(String name)
		{
			this.name = name;
		}

		public T deserialize(byte[] array) throws Exception
		{
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(array));
			@SuppressWarnings("unchecked")
			T v = (T) ois.readObject();
			return v;
		}

		public byte[] serialize(T data) throws IOException
		{
			ByteArrayOutputStream baos = outputStream(data);
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(data);
			return baos.toByteArray();
		}

		public String getName()
		{
			return name;
		}
	}

	// ------------------------------------------------------------
	// MediaTransformer

	public static final MediaTransformer<MediaContent> mediaTransformer = new MediaTransformer<MediaContent>()
	{
		@Override
		public MediaContent[] resultArray(int size) { return new MediaContent[size]; }

		public MediaContent forward(MediaContent mc)
		{
			return copy(mc);
		}

		public MediaContent reverse(MediaContent mc)
		{
			return copy(mc);
		}

		private MediaContent copy(MediaContent mc)
		{
			ArrayList<Image> images = new ArrayList<Image>(mc.images.size());
			for (Image i : mc.images) {
				images.add(new Image(i.uri, i.title, i.width, i.height, i.size));
			}
			return new MediaContent(copy(mc.media), images);
		}

		private Media copy(Media m)
		{
			return new Media(m.uri, m.title, m.width, m.height, m.format, m.duration, m.size, m.bitrate, m.hasBitrate, new ArrayList<String>(m.persons), m.player, m.copyright);
		}

		public MediaContent shallowReverse(MediaContent mc)
		{
			return new MediaContent(copy(mc.media), Collections.<Image>emptyList());
		}
	};
}
