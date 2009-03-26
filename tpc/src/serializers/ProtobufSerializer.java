package serializers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import serializers.protobuf.MediaContentHolder.Image;
import serializers.protobuf.MediaContentHolder.Media;
import serializers.protobuf.MediaContentHolder.MediaContent;
import serializers.protobuf.MediaContentHolder.Image.Size;
import serializers.protobuf.MediaContentHolder.Media.Player;

public class ProtobufSerializer implements ObjectSerializer<MediaContent>
{

  public MediaContent deserialize (byte[] array) throws Exception
  {
    return MediaContent.parseFrom(array);
  }

    public byte[] serialize(MediaContent content) throws IOException
    {
        return content.toByteArray();
    }

  public MediaContent create()
  {
    MediaContent content = MediaContent.newBuilder().
    setMedia(
      Media.newBuilder().setUri("http://javaone.com/keynote.mpg").setFormat("video/mpg4").setTitle("Javaone Keynote").setDuration(1234567).
        setBitrate(123).addPerson("Bill Gates").addPerson("Steve Jobs").setPlayer(Player.JAVA).build()).
    addImage(
      Image.newBuilder().setUri("http://javaone.com/keynote_large.jpg").setSize(Size.LARGE).setTitle("Javaone Keynote").build()).
    addImage(
      Image.newBuilder().setUri("http://javaone.com/keynote_thumbnail.jpg").setSize(Size.SMALL).setTitle("Javaone Keynote").build()).
    build();
    return content;
  }

  public String getName ()
  {
    return "protobuf";
  }
}
