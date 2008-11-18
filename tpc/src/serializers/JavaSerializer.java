package serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import serializers.java.Image;
import serializers.java.Media;
import serializers.java.MediaContent;
import serializers.java.Image.Size;
import serializers.java.Media.Player;


public class JavaSerializer implements ObjectSerializer<MediaContent>
{

  public MediaContent deserialize (byte[] array) throws Exception
  {
    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(array));
    try
    {
      return (MediaContent)ois.readObject();
    }
    finally
    {
      ois.close();
    }
  }

  public byte[] serialize(MediaContent content) throws IOException, Exception
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(content);
    oos.close();
    return baos.toByteArray();
  }

  public MediaContent create()
  {
    Media media = new Media();
    media.setUri("http://javaone.com/keynote.mpg");
    media.setFormat("video/mpg4");
    media.setTitle("Javaone Keynote");
    media.setDuration(1234567);
    media.setBitrate(123);
    media.addToPerson("Bill Gates");
    media.addToPerson("Steve Jobs");
    media.setPlayer(Player.JAVA);

    Image image1 = new Image();
    image1.setUri("http://javaone.com/keynote_large.jpg");
    image1.setSize(Size.LARGE);
    image1.setTitle("Javaone Keynote");

    Image image2 = new Image();
    image2.setUri("http://javaone.com/keynote_thumbnail.jpg");
    image2.setSize(Size.SMALL);
    image2.setTitle("Javaone Keynote");

    MediaContent content = new MediaContent();
    content.setMedia(media);
    content.addImage(image1);
    content.addImage(image2);
    return content;
  }

  public String getName ()
  {
    return "java";
  }
}
