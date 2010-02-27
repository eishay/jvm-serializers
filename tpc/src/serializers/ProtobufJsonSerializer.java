package serializers;

import serializers.protobuf.MediaContentHolder;

import java.io.IOException;
import java.nio.charset.Charset;

import com.google.protobuf.JsonFormat;

public class ProtobufJsonSerializer implements ObjectSerializer<MediaContentHolder.MediaContent>
{
  private final Charset _charset = Charset.forName("UTF-8");

  public MediaContentHolder.MediaContent deserialize (byte[] array) throws Exception
  {
    MediaContentHolder.MediaContent.Builder builder = MediaContentHolder.MediaContent.newBuilder();
    JsonFormat.merge(new String(array, _charset.name()), builder);
    return builder.build();
  }

  public byte[] serialize(MediaContentHolder.MediaContent content) throws IOException
  {
    return JsonFormat.printToString(content).getBytes(_charset.name());
  }

  public MediaContentHolder.MediaContent create()
  {
    MediaContentHolder.MediaContent content = MediaContentHolder.MediaContent.newBuilder().
    setMedia(
      MediaContentHolder.Media.newBuilder().setUri("http://javaone.com/keynote.mpg").setFormat("video/mpg4").setTitle("Javaone Keynote").setDuration(1234567).
        setBitrate(123).addPerson("Bill Gates").addPerson("Steve Jobs").setPlayer(MediaContentHolder.Media.Player.JAVA).build()).
    addImage(
      MediaContentHolder.Image.newBuilder().setUri("http://javaone.com/keynote_large.jpg").setSize(MediaContentHolder.Image.Size.LARGE).setTitle("Javaone Keynote").build()).
    addImage(
      MediaContentHolder.Image.newBuilder().setUri("http://javaone.com/keynote_thumbnail.jpg").setSize(MediaContentHolder.Image.Size.SMALL).setTitle("Javaone Keynote").build()).
    build();
    return content;
  }

  public String getName ()
  {
    return "protobuf-json";
  }
}
