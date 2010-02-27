package serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import serializers.scala.Image;
import serializers.scala.Media;
import serializers.scala.MediaContent;
import serializers.scala.Player;
import serializers.scala.Size;



public class ScalaSerializer implements ObjectSerializer<MediaContent>
{
  public int expectedSize = 0;

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
    ByteArrayOutputStream baos = new ByteArrayOutputStream(expectedSize);
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(content);
    oos.close();
    byte[] array = baos.toByteArray();
    expectedSize = array.length;
    return array;
  }

  public MediaContent create()
  {
    Media media = new Media("http://javaone.com/keynote.mpg", "Javaone Keynote", 0, 0, "video/mpg4", 1234567, 123, 0, Player.JAVA());
    media.addPerson("Bill Gates");
    media.addPerson("Steve Jobs");

    Image image1 = new Image("http://javaone.com/keynote_large.jpg", "Javaone Keynote", 0, 0, Size.LARGE());

    Image image2 = new Image("http://javaone.com/keynote_thumbnail.jpg", "Javaone Keynote", 0, 0, Size.SMALL());

    MediaContent content = new MediaContent(media);
    content.addImage(image1);
    content.addImage(image2);

    return content;
  }

  public String getName ()
  {
    return "scala";
  }
}
