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
    	MediaContent contentProto = MediaContent
    	.newBuilder().setMedia(
    		Media.newBuilder()
    			.clearCopyright()
    			.setFormat("video/mpg4")
    			.setPlayer(Player.JAVA)
    			.setTitle("Javaone Keynote")
    			.setUri("http://javaone.com/keynote.mpg")
    			.setDuration(1234567)
    			.setSize(123)
    			.setHeight(0)
    			.setWidth(0)
    			.setBitrate(123)
    			.addPerson("Bill Gates")
    			.addPerson("Steve Jobs")
    			.build()
    		).addImage(
    			Image.newBuilder()
    			.setHeight(0)
    			.setTitle("Javaone Keynote")
    			.setUri("http://javaone.com/keynote_large.jpg")
    			.setWidth(0)
    			.setSize(Size.LARGE)
    			.build()
    		).addImage(
    			Image.newBuilder()
    			.setHeight(0)
    			.setTitle("Javaone Keynote")
    			.setUri("http://javaone.com/keynote_thumbnail.jpg")
    			.setWidth(0)
    			.setSize(Size.SMALL)
    			.build()
    		)
    		.build();
    	return contentProto;
    }

  public String getName ()
  {
    return "protobuf";
  }
}
