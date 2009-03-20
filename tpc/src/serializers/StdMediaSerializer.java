package serializers;

import serializers.java.MediaContent;
import serializers.java.Media;
import serializers.java.Image;

/**
 * Shared base class for those serializers that operate on POJOs.
 */
public abstract class StdMediaSerializer
    implements ObjectSerializer<MediaContent>
{
    final String name;

    protected StdMediaSerializer(String n)
    {
        name = n;
    }

    public final String getName()
    {
        return name;
    }

    public final MediaContent create() throws Exception
    {
        Media media = new Media(null, "video/mpg4", Media.Player.JAVA, "Javaone Keynote", "http://javaone.com/keynote.mpg", 1234567, 123, 0, 0, 0);
        media.addToPerson("Bill Gates");
        media.addToPerson("Steve Jobs");
        
        Image image1 = new Image(0, "Javaone Keynote", "A", 0, Image.Size.LARGE);
        Image image2 = new Image(0, "Javaone Keynote", "B", 0, Image.Size.SMALL);
        
        MediaContent content = new MediaContent(media);
        content.addImage(image1);
        content.addImage(image2);
        return content;
    }
}
