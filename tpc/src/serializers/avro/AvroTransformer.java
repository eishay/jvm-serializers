package serializers.avro;

import data.media.MediaTransformer;
import serializers.avro.media.Image;
import serializers.avro.media.Media;
import serializers.avro.media.MediaContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Transformer is needed when we use Avro-generated Java classes, to convert
 * between these and POJOs for verification purposes. This transformation
 * is NOT done during performance testing, as it could be viewed as
 * unfair overhead for use cases where generated class is used instead
 * of equivalent POJO.
 */
public class AvroTransformer extends MediaTransformer<MediaContent>
{
    @Override
    public MediaContent[] resultArray(int size) { return new MediaContent[size]; }

    // ----------------------------------------------------------
    // Forward

    public MediaContent forward(data.media.MediaContent mc)
    {
        return new MediaContent(forwardImages(mc.images), forwardMedia(mc.media));
    }

    private List<Image> forwardImages(List<data.media.Image> images) {
        List<Image> result = new ArrayList<Image>(images.size());
        for (int i = 0; i < images.size(); i++) {
            result.add(i, forwardImage(images.get(i)));
        }
        return result;
    }


    private Media forwardMedia(data.media.Media media)
    {
        Integer bitRate = media.hasBitrate ? media.bitrate : null;
        List<CharSequence> persons = new ArrayList<>(media.persons.size());
        for (int i = 0; i < media.persons.size(); i++) {
            persons.add(i, media.persons.get(i));
        }
        return new Media(media.uri, media.title, media.width, media.height, media.format, media.duration,
                media.size, bitRate, persons, AvroGeneric.forwardPlayer(media.player), media.copyright);

    }

    private Image forwardImage(data.media.Image image)
    {
            return new Image(image.uri, image.title, image.width, image.height, AvroGeneric.forwardSize(image.size));
    }

    // ----------------------------------------------------------
    // Reverse

    public data.media.MediaContent reverse(MediaContent mc)
    {
            List<data.media.Image> images = new ArrayList<data.media.Image>(mc.getImages().size());

            for (Image image : mc.getImages()) {
                images.add(reverseImage(image));
            }

            return new data.media.MediaContent(reverseMedia(mc.getMedia()), images);
    }

    private data.media.Media reverseMedia(Media media)
    {
            // Media
            List<String> persons = new ArrayList<String>();
            for (CharSequence p : media.getPersons()) {
                    persons.add(p.toString());
            }

            return new data.media.Media(
                    media.getUri().toString(),
                    media.getTitle() != null ? media.getTitle().toString() : null,
                    media.getWidth(),
                    media.getHeight(),
                    media.getFormat().toString(),
                    media.getDuration(),
                    media.getSize(),
                    media.getBitrate() != null ? media.getBitrate() : 0,
                    media.getBitrate() != null,
                    persons,
                    reversePlayer(media.getPlayer()),
                    media.getCopyright() != null ? media.getCopyright().toString() : null
            );
    }

    public data.media.Media.Player reversePlayer(int p)
    {
        switch (p) {
        case 1: return data.media.Media.Player.JAVA;
        case 2: return data.media.Media.Player.FLASH;
        default: throw new AssertionError("invalid case: " + p);
        }
    }

    private data.media.Image reverseImage(Image image)
    {
        return new data.media.Image(
                image.getUri().toString(),
                image.getTitle() != null ? image.getTitle().toString() : null,
                image.getWidth(),
                image.getHeight(),
                reverseSize(image.getSize()));
    }

    public data.media.Image.Size reverseSize(int s)
    {
        switch (s) {
        case 1: return data.media.Image.Size.SMALL;
        case 2: return data.media.Image.Size.LARGE;
        default: throw new AssertionError("invalid case: " + s);
        }
    }

    public data.media.MediaContent shallowReverse(MediaContent mc)
    {
        return new data.media.MediaContent(reverseMedia(mc.getMedia()),
                Collections.<data.media.Image>emptyList());
    }
}
