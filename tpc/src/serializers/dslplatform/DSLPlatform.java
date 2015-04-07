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
                SerFormat.JSON, SerGraph.FLAT_TREE, SerClass.CLASSES_KNOWN, "Serializes all properties.")); // Full serialization
        groups.media.add(new DSLPlatformMediaTransformer(), new DSLPlatformSerializer(true), new SerFeatures(
                SerFormat.JSON, SerGraph.FLAT_TREE, SerClass.CLASSES_KNOWN,
                "Omits default values from the JSON output.")); // Minimal serialization
    }

    static class DSLPlatformSerializer extends Serializer<MediaContent> {
        private static JsonWriter writer = new JsonWriter();
        private final boolean minimal;
        private final char[] tmp = new char[64];

        @Override
        public String getName() {
            return "json/dsl-platform" + (this.minimal
                    ? "/omit-defaults" : "");
        }

        public DSLPlatformSerializer() {
            this(true); // minimal serialization
        }

        public DSLPlatformSerializer(final boolean minimal) {
            this.minimal = minimal;
        }

        @Override
        public MediaContent deserialize(final byte[] array) throws Exception {
            return (MediaContent) MediaContent.deserialize(new JsonReader(array, null, tmp), null);
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

        private static Media forward(final data.media.Media media) {
            return new Media(media.uri,
                    media.title,
                    media.width,
                    media.height,
                    media.format,
                    media.duration,
                    media.size,
                    media.bitrate,
                    media.persons,
                    forward(media.player),
                    media.copyright);
        }

        private static Player forward(final data.media.Media.Player player) {
            return player == data.media.Media.Player.JAVA
                    ? Player.JAVA
                    : Player.FLASH;
        }

        private static List<Image> forward(final List<data.media.Image> images) {
            final ArrayList<Image> forwardedImgs = new ArrayList<Image>(images.size());
            for (final data.media.Image image : images) {
                forwardedImgs.add(forward(image));
            }
            return forwardedImgs;
        }

        private static Image forward(final data.media.Image image) {
            return new Image(image.uri,
                    image.title,
                    image.width,
                    image.height,
                    forward(image.size));
        }

        private static Size forward(final data.media.Image.Size size) {
            return size == data.media.Image.Size.SMALL
					? Size.SMALL
					: Size.LARGE;
		}

        // ----------------------------------------------------------
        // Reverse

        @Override
        public data.media.MediaContent reverse(final MediaContent mc) {
            return new data.media.MediaContent(reverse(mc.getMedia()), reverse(mc.getImages()));
        }

        private static data.media.Media reverse(final Media media) {
            // Media
            return new data.media.Media(media.getUri(),
                    media.getTitle(),
                    media.getWidth(),
                    media.getHeight(),
                    media.getFormat(),
                    media.getDuration(),
                    media.getSize(),
                    media.getBitrate(),
                    media.getBitrate() != 0,
                    media.getPersons(),
                    reverse(media.getPlayer()),
                    media.getCopyright());
        }

        private static data.media.Media.Player reverse(final Player player) {
            return player == Player.JAVA
                    ? data.media.Media.Player.JAVA
                    : data.media.Media.Player.FLASH;
        }

        private static List<data.media.Image> reverse(final List<Image> images) {
            final ArrayList<data.media.Image> reversed = new ArrayList<data.media.Image>(images.size());
            for (final Image image : images) {
                reversed.add(reverse(image));
            }
            return reversed;
        }

        private static data.media.Image reverse(final Image image) {
            return new data.media.Image(image.getUri(),
                    image.getTitle(),
                    image.getWidth(),
                    image.getHeight(),
                    reverse(image.getSize()));
        }

        private static data.media.Image.Size reverse(final Size size) {
            return size == Size.SMALL
                    ? data.media.Image.Size.SMALL
                    : data.media.Image.Size.LARGE;
        }

        @Override
        public data.media.MediaContent shallowReverse(final MediaContent mc) {
            return new data.media.MediaContent(reverse(mc.getMedia()), Collections.<data.media.Image> emptyList());
        }
    }
}
