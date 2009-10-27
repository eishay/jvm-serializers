package serializers;

import java.io.ByteArrayOutputStream;

import org.codehaus.jackson.JsonParser;

import serializers.protobuf.MediaContentHolder.Image;
import serializers.protobuf.MediaContentHolder.Media;
import serializers.protobuf.MediaContentHolder.MediaContent;
import serializers.protobuf.MediaContentHolder.Image.Size;
import serializers.protobuf.MediaContentHolder.Media.Player;
import serializers.protostuff.MediaContentHolderJSON;

/**
 * @author David Yu
 * @created Oct 26, 2009
 */

public class ProtostuffJsonSerializer implements ObjectSerializer<MediaContent>
{
    
    private static final MediaContentHolderJSON json = new MediaContentHolderJSON();

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

    public String getName()
    {
        return "protostuff-json";
    }
    
    public MediaContent deserialize(byte[] array) throws Exception
    {
        MediaContent.Builder builder = MediaContent.newBuilder();
        JsonParser parser = json.getJsonFactory().createJsonParser(array);
        json.mergeFrom(parser, builder);
        parser.close();
        return builder.build();
    }

    public byte[] serialize(MediaContent content) throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream(512);
        json.writeTo(out, content);
        return out.toByteArray();
    }

}
