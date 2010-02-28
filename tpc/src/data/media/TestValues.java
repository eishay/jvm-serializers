package data.media;

import java.util.Arrays;

public class TestValues
{
	public static final MediaContent v1;

	static {
		Media media = new Media("http://javaone.com/keynote.mpg", "Javaone Keynote", 640, 480, "video/mpg4", 18000000, 123, 256000, true, Arrays.asList("Bill Gates", "Steve Jobs"), Media.Player.JAVA, null);

		Image image1 = new Image("http://javaone.com/keynote_large.jpg", "Javaone Keynote", 1024, 768, Image.Size.LARGE);
		Image image2 = new Image("http://javaone.com/keynote_thumbnail.jpg", "Javaone Keynote", 320, 240, Image.Size.SMALL);

      v1 = new MediaContent(media, Arrays.asList(image1, image2));
	}
}
