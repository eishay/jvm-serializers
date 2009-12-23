package serializers;

import java.io.IOException;
import java.util.List;

import serializers.activemq.protobuf.MediaContentHolder.Image;
import serializers.activemq.protobuf.MediaContentHolder.Media;
import serializers.activemq.protobuf.MediaContentHolder.MediaContent;
import serializers.activemq.protobuf.MediaContentHolder.Image.ImageBean;
import serializers.activemq.protobuf.MediaContentHolder.Image.Size;
import serializers.activemq.protobuf.MediaContentHolder.Media.MediaBean;
import serializers.activemq.protobuf.MediaContentHolder.Media.Player;
import serializers.activemq.protobuf.MediaContentHolder.MediaContent.MediaContentBean;
import serializers.activemq.protobuf.MediaContentHolder.MediaContent.MediaContentBuffer;

public class ActiveMQProtobufSerializer implements CheckingObjectSerializer<MediaContent> {

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

    @Override
    public void checkAllFields(MediaContent content) {
        checkMediaField(content);
        List<Image> list = content.getImageList();
        assetEquals(2, list.size());
        
        Image image = list.get(0).copy();
        assetEquals(image.getUri(), "http://javaone.com/keynote_large.jpg");
        assetEquals(image.getSize(), Size.LARGE);
        assetEquals(image.getTitle(), "Javaone Keynote");
        assetEquals(image.getWidth(), 0);
        assetEquals(image.getHeight(), 0);
        
        image = list.get(1).copy();
        assetEquals(image.getUri(), "http://javaone.com/keynote_thumbnail.jpg");
        assetEquals(image.getSize(), Size.SMALL);
        assetEquals(image.getTitle(), "Javaone Keynote");
        assetEquals(image.getWidth(), 0);
        assetEquals(image.getHeight(), 0);
    }

    @Override
    public void checkMediaField(MediaContent content) {
        Media media = content.getMedia().copy();
        assetEquals(media.getUri(), "http://javaone.com/keynote.mpg");
        assetEquals(media.getFormat(), "video/mpg4");
        assetEquals(media.getTitle(), "Javaone Keynote");
        assetEquals(media.getDuration(), 1234567L);
        assetEquals(media.getSize(), 123L);
        assetEquals(media.getBitrate(), 0);
        assetEquals(media.getPlayer(), Player.JAVA);
        assetEquals(media.getWidth(), 0);
        assetEquals(media.getHeight(), 0);
    }

    static private void assetEquals(Object expected, Object actual) {
        if( !expected.equals(actual) ) {
            throw new RuntimeException(""+expected+"!="+actual);
        }
    }

}