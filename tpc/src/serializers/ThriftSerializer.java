package serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import serializers.thrift.Image;
import serializers.thrift.Media;
import serializers.thrift.MediaContent;
import serializers.thrift.Player;
import serializers.thrift.Size;

import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.transport.TIOStreamTransport;

public class ThriftSerializer  implements ObjectSerializer<MediaContent>
{
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
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    TIOStreamTransport trans = new TIOStreamTransport(baos);
    TBinaryProtocol oprot = new TBinaryProtocol(trans);
    content.write(oprot);
    byte[] bytes = baos.toByteArray();
    return bytes;
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

    Image image2 = new Image("http://javaone.com/keynote_thumbnail.jpg", "Javaone Keynote", -1, -1, Size.SMALL);

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
