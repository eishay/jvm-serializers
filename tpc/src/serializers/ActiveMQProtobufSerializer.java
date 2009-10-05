package serializers;

import java.io.IOException;

import serializers.activemq.protobuf.MediaContentHolder.Image.ImageBean;
import serializers.activemq.protobuf.MediaContentHolder.Image.Size;
import serializers.activemq.protobuf.MediaContentHolder.Media.MediaBean;
import serializers.activemq.protobuf.MediaContentHolder.Media.Player;
import serializers.activemq.protobuf.MediaContentHolder.MediaContent;
import serializers.activemq.protobuf.MediaContentHolder.MediaContent.MediaContentBean;
import serializers.activemq.protobuf.MediaContentHolder.MediaContent.MediaContentBuffer;

public class ActiveMQProtobufSerializer implements ObjectSerializer<MediaContent> {

    public MediaContent deserialize(byte[] array) throws Exception {
        return MediaContentBuffer.parseUnframed(array);
    }

    public byte[] serialize(MediaContent content) throws IOException {
        return content.freeze().toUnframedByteArray();
    }

    public MediaContent create() {
        MediaContentBean content = new MediaContentBean()
        .setMedia(
            new MediaBean()
            .setUri("http://javaone.com/keynote.mpg")
            .setFormat("video/mpg4")
            .setTitle("Javaone Keynote")
            .setDuration(1234567)
            .setSize(123)
            .setBitrate(0)
            .addPerson("Bill Gates")
            .addPerson("Steve Jobs")
            .setPlayer(Player.JAVA)
            .setWidth(0)
            .setHeight(0)
        )
        .addImage(
            new ImageBean()
            .setUri("http://javaone.com/keynote_large.jpg")
            .setSize(Size.LARGE)
            .setTitle("Javaone Keynote")
            .setWidth(0)
            .setHeight(0)
        )
        .addImage(
            new ImageBean()
            .setUri("http://javaone.com/keynote_thumbnail.jpg")
            .setSize(Size.SMALL)
            .setTitle("Javaone Keynote")
            .setWidth(0)
            .setHeight(0)
        );
        return content.freeze();
    }

    public String getName() {
        return "activemq protobuf";
    }
}