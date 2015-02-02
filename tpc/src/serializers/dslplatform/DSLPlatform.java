package serializers.dslplatform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import serializers.SerClass;
import serializers.SerFeatures;
import serializers.SerFormat;
import serializers.SerGraph;
import serializers.Serializer;
import serializers.TestGroups;
import serializers.dslplatform.media.Image;
import serializers.dslplatform.media.Media;
import serializers.dslplatform.media.MediaContent;
import serializers.dslplatform.media.Player;
import serializers.dslplatform.media.Size;

import com.dslplatform.client.json.JsonReader;
import com.dslplatform.client.json.JsonWriter;

import data.media.MediaTransformer;

/**
 * A test harness for DSL Platform (<a href="http://dsl-platform.com">http://dsl-platform.com</a>) generated classes.
 *
 * Uses pregenerated classes (the schema is in {@code schema/media.dsl}).
 */
public class DSLPlatform {
    public static void register(final TestGroups groups) {
        groups.media.add(new DSLPlatformMediaTransformer(), new DSLPlatformSerializer(false), new SerFeatures(
                SerFormat.JSON, SerGraph.FLAT_TREE, SerClass.CLASSES_KNOWN, "")); // Full serialization
        groups.media.add(new DSLPlatformMediaTransformer(), new DSLPlatformSerializer(true), new SerFeatures(
                SerFormat.JSON, SerGraph.FLAT_TREE, SerClass.CLASSES_KNOWN, "")); // Minimal serialization
    }

    static class DSLPlatformSerializer extends Serializer<MediaContent> {
        private static JsonWriter writer = new JsonWriter();
        private final boolean minimal;

        @Override
        public String getName() {
            return "json/dsl-platform" + (this.minimal ? "/minimal" : "/full");
        }
        
        public DSLPlatformSerializer(){
            this(true); // minimal serialization
        }	

        public DSLPlatformSerializer(boolean minimal){
            this.minimal = minimal;
        }

        @Override
        public MediaContent deserialize(final byte[] array) throws Exception {
            return (MediaContent) MediaContent.deserialize(new JsonReader(array, null), null);
        }

        @Override
        public byte[] serialize(final MediaContent content) throws Exception {
            writer.reset();
            content.serialize(writer, this.minimal);
            return writer.toByteArray();
        }
    }

    static final class DSLPlatformMediaTransformer extends MediaTransformer<MediaContent> {
        @Override
        public MediaContent[] resultArray(final int size) {
            return new MediaContent[size];
        }

        @Override
        public MediaContent forward(final data.media.MediaContent commonMediaContent) {
            return new MediaContent(forward(commonMediaContent.media), forward(commonMediaContent.images));
        }

        private Media forward(final data.media.Media media) {
            return new Media(media.uri, media.title, media.width, media.height, media.format,
                    media.duration, media.size, media.bitrate, media.persons, this.forward(media.player),
                    media.copyright);
        }

        private Player forward(final data.media.Media.Player player) {
            switch (player) {
                case JAVA:
                    return Player.JAVA;
                case FLASH:
                    return Player.FLASH;
                default:
                    throw new AssertionError("invalid case: " + player);
            }
        }

        private List<Image> forward(final List<data.media.Image> images) {
            final ArrayList<Image> forwardedImgs = new ArrayList<Image>(images.size());
            for (final data.media.Image image : images) {
                forwardedImgs.add(forward(image));
            }
            return forwardedImgs;
        }

        private Image forward(final data.media.Image image) {
            return new Image(image.uri, image.title, image.width, image.height,
                    this.forward(image.size));
        }

        private Size forward(final data.media.Image.Size size) {
            switch (size) {
                case SMALL:
                    return Size.SMALL;
                case LARGE:
                    return Size.LARGE;
                default:
                    throw new AssertionError("invalid case: " + size);
            }
        }

        // ----------------------------------------------------------
        // Reverse

        @Override
        public data.media.MediaContent reverse(final MediaContent mc) {
            return new data.media.MediaContent(reverse(mc.getMedia()), reverse(mc.getImages()));
        }

        private data.media.Media reverse(final Media media) {
            // Media
            return new data.media.Media(media.getUri(), media.getTitle(), media.getWidth(), media.getHeight(),
                    media.getFormat(), media.getDuration(), media.getSize(), media.getBitrate(),
                    media.getBitrate() != 0, media.getPersons(), reverse(media.getPlayer()), media.getCopyright());
        }

        private data.media.Media.Player reverse(final Player player) {
            switch (player) {
                case JAVA:
                    return data.media.Media.Player.JAVA;
                case FLASH:
                    return data.media.Media.Player.FLASH;
                default:
                    throw new AssertionError("invalid case: " + player);
            }
        }

        private List<data.media.Image> reverse(final List<Image> images) {
            final ArrayList<data.media.Image> reversed = new ArrayList<data.media.Image>(images.size());
            for (final Image image : images) {
                reversed.add(reverse(image));
            }
            return reversed;
        }

        private data.media.Image reverse(final Image image) {
            return new data.media.Image(image.getUri(), image.getTitle(), image.getWidth(), image.getHeight(),
                    reverse(image.getSize()));
        }

        private data.media.Image.Size reverse(final Size size) {
            switch (size) {
                case SMALL:
                    return data.media.Image.Size.SMALL;
                case LARGE:
                    return data.media.Image.Size.LARGE;
                default:
                    throw new AssertionError("invalid case: " + size);
            }
        }

        @Override
        public data.media.MediaContent shallowReverse(final MediaContent mc) {
            return new data.media.MediaContent(reverse(mc.getMedia()), Collections.<data.media.Image> emptyList());
        }
    }
}
