package serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import serializers.thrift.Image;
import serializers.thrift.Media;
import serializers.thrift.MediaContent;
import serializers.thrift.Player;
import serializers.thrift.Size;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TIOStreamTransport;

public class ThriftSerializer  implements ObjectSerializer<MediaContent>
{
  public int expectedSize = 0;
  public final static int ITERATIONS = 100000;

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
      ByteArrayOutputStream baos = new ByteArrayOutputStream(expectedSize);
    TIOStreamTransport trans = new TIOStreamTransport(baos);
    TBinaryProtocol oprot = new TBinaryProtocol(trans);
    content.write(oprot);
    byte[] array = baos.toByteArray();
    expectedSize = array.length;
    return array;
  }

  public MediaContent create()
  {
    Media media = new Media();
    media.setUri("http://javaone.com/keynote.mpg");
    media.setFormat("video/mpg4");
    media.setTitle("Javaone Keynote");
    media.setDuration(1234567);
    media.setBitrate(0);
    media.setSize(123);
    media.setWidth(0);
    media.setHeight(0);
    media.addToPerson("Bill Gates");
    media.addToPerson("Steve Jobs");
    media.setPlayer(Player.JAVA);

    Image image1 = new Image("http://javaone.com/keynote_large.jpg");
    image1.setTitle("Javaone Keynote");
    image1.setHeight(0);
    image1.setWidth(0);
    image1.setSize(Size.LARGE);
    Image image2 = new Image("http://javaone.com/keynote_thumbnail.jpg");
    image2.setTitle("Javaone Keynote");
    image2.setHeight(0);
    image2.setWidth(0);
    image2.setSize(Size.SMALL);

    MediaContent content = new MediaContent();
    content.setMedia(media);
    content.addToImage(image1);
    content.addToImage(image2);
    return content;
  }

  public String getName ()
  {
    return "thrift";
  }

}
