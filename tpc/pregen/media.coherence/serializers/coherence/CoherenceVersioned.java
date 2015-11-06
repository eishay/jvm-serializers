package serializers.coherence;


import data.media.MediaTransformer;
import serializers.*;
import serializers.coherence.media.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CoherenceVersioned {


    public static void register(TestGroups groups) {
        groups.media.add(mediaTransformer,new CoherencePofSerializer<MediaContent>(MediaContent.class),
                new SerFeatures(
                        SerFormat.BIN_CROSSLANG,
                        SerGraph.FULL_GRAPH,
                        SerClass.MANUAL_OPT, "coherence-pof with version support"
                )
        );
    }

    // ------------------------------------------------------------
    // Serializer (just one)

    public final static class CoherencePofSerializer<T> extends AbstractCoherencePofSerializer<T> {
        
        public CoherencePofSerializer(Class<T> c) {
            super(c, "pregen/media.coherence/serializers/coherence/versioned-pof-config.xml");

        }
        public String getName() {
            return "coherence-pof-versioned";
        }

    };

    // ------------------------------------------------------------
    // Transformers

    public static final MediaTransformer<MediaContent> mediaTransformer = new MediaTransformer<MediaContent>()
    {
        @Override
        public MediaContent[] resultArray(int size) { return new MediaContent[size]; }

        // ----------------------------------------------------------
        // Forward

        public MediaContent forward(data.media.MediaContent mc)
        {
            MediaContent cb = new MediaContent();

            cb.setMedia(forwardMedia(mc.media));
            for (data.media.Image image : mc.images) {
                cb.getImages().add(forwardImage(image));
            }

            return cb;
        }

        private Media forwardMedia(data.media.Media media)
        {
            // Media
            Media mb = new Media();
            mb.setUri(media.uri);
            if (media.title != null) mb.setTitle(media.title);
            mb.setWidth(media.width);
            mb.setHeight(media.height);
            mb.setFormat(media.format);
            mb.setDuration(media.duration);
            mb.setSize(media.size);
            if (media.hasBitrate) mb.setBitrate(media.bitrate);
            for (String person : media.persons) {
                mb.getPersons().add(person);
            }
            mb.setPlayer(forwardPlayer(media.player));
            if (media.copyright != null) mb.setCopyright(media.copyright);

            return mb;
        }

        public Player forwardPlayer(data.media.Media.Player p)
        {
            switch (p) {
                case JAVA: return Player.JAVA;
                case FLASH: return Player.FLASH;
                default:
                    throw new AssertionError("invalid case: " + p);
            }
        }

        private Image forwardImage(data.media.Image image)
        {
            Image ib = new Image();
            ib.setUri(image.uri);
            if (image.title != null) ib.setTitle(image.title);
            ib.setWidth(image.width);
            ib.setHeight(image.height);
            ib.setSize(forwardSize(image.size));
            return ib;
        }

        public Size forwardSize(data.media.Image.Size s)
        {
            switch (s) {
                case SMALL: return Size.SMALL;
                case LARGE: return Size.LARGE;
                default:
                    throw new AssertionError("invalid case: " + s);
            }
        }

        // ----------------------------------------------------------
        // Reverse

        public data.media.MediaContent reverse(MediaContent mc)
        {
            List<data.media.Image> images = new ArrayList<data.media.Image>(mc.getImageSize());

            for (Image image : mc.getImages()) {
                images.add(reverseImage(image));
            }

            return new data.media.MediaContent(reverseMedia(mc.getMedia()), images);
        }

        private data.media.Media reverseMedia(Media media)
        {
            // Media
            return new data.media.Media(
                    media.getUri(),
                     media.getTitle(),
                    media.getWidth(),
                    media.getHeight(),
                    media.getFormat(),
                    media.getDuration(),
                    media.getSize(),
                   media.getBitrate(),
                    media.hasBitrate,
                    new ArrayList<String>(media.getPersons()),
                    reversePlayer(media.getPlayer()),
                    media.getCopyright()
            );
        }

        public data.media.Media.Player reversePlayer(Player p)
        {
            switch (p) {
                case JAVA:
                    return data.media.Media.Player.JAVA;
                case FLASH:
                    return data.media.Media.Player.FLASH;
                default:
                    throw new AssertionError("invalid case: " + p);
            }
        }

        private data.media.Image reverseImage(Image image)
        {
            return new data.media.Image(
                    image.getUri(),
                    image.getTitle(),
                    image.getWidth(),
                    image.getHeight(),
                    reverseSize(image.getSize()));
        }

        public data.media.Image.Size reverseSize(Size s)
        {
            switch (s) {
                case SMALL: return data.media.Image.Size.SMALL;
                case LARGE: return data.media.Image.Size.LARGE;
                default:
                    throw new AssertionError("invalid case: " + s);
            }
        }

        public data.media.MediaContent shallowReverse(MediaContent mc)
        {
            return new data.media.MediaContent(reverseMedia(mc.getMedia()), Collections.<data.media.Image>emptyList());
        }
    };
}
