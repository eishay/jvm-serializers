package serializers.avro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericData;

import serializers.avro.media.Image;
import serializers.avro.media.Media;
import serializers.avro.media.MediaContent;
import data.media.MediaTransformer;

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
        GenericArray<Image> images = new GenericData.Array<Image>(mc.images.size(), Avro.Media.sImages);
        for (data.media.Image image : mc.images) {
            images.add(forwardImage(image));
        }

        MediaContent amc = new MediaContent();
        amc.setMedia(forwardMedia(mc.media));
        amc.setImages(images);
        return amc;
    }

    private Media forwardMedia(data.media.Media media)
    {
            Media m = new Media();

            m.setUri(media.uri);
            m.setTitle(media.title);
            m.setWidth(media.width);
            m.setHeight(media.height);
            m.setFormat(media.format);
            m.setDuration(media.duration);
            m.setSize(media.size);
            if (media.hasBitrate) {
                m.setBitrate(media.bitrate);
            }

            GenericArray<CharSequence> persons = new GenericData.Array<CharSequence>(media.persons.size(), Avro.Media.sPersons);
            for (String s : media.persons) {
              persons.add(s);
            }
            m.setPersons(persons);
            m.setPlayer(forwardPlayer(media.player));
            m.setCopyright(media.copyright);

            return m;
    }

    public int forwardPlayer(data.media.Media.Player p)
    {
            switch (p) {
                    case JAVA: return 1;
                    case FLASH: return 2;
                    default: throw new AssertionError("invalid case: " + p);
            }
    }

    private Image forwardImage(data.media.Image image)
    {
            Image i = new Image();
            i.setUri(image.uri);
            i.setTitle(image.title);
            i.setWidth(image.width);
            i.setHeight(image.height);
            i.setSize(forwardSize(image.size));
            return i;
    }

    public int forwardSize(data.media.Image.Size s)
    {
            switch (s) {
                    case SMALL: return 1;
                    case LARGE: return 2;
                    default: throw new AssertionError("invalid case: " + s);
            }
    }

    // ----------------------------------------------------------
    // Reverse

    public data.media.MediaContent reverse(MediaContent mc)
    {
            List<data.media.Image> images = new ArrayList<data.media.Image>((int) mc.getImages().size());

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
