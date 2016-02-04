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

import serializers.dslplatform.full.*;
import serializers.dslplatform.minified.*;
import serializers.dslplatform.shared.*;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;

import data.media.MediaTransformer;

/**
 * A test harness for DSL Platform (<a href="http://dsl-platform.com">http://dsl-platform.com</a>) generated classes.
 *
 * Uses pregenerated classes (the schema is in {@code schema/media.dsl}).
 */
public class DSLPlatform {

    public static void register(final TestGroups groups) {
        groups.media.add(new DSLPlatformFullMediaTransformer(), new DSLPlatformFullSerializer(), new SerFeatures(
                SerFormat.JSON, SerGraph.FLAT_TREE, SerClass.CLASSES_KNOWN, "Serializes all properties with exact names."));
        groups.media.add(new DSLPlatformMinifiedMediaTransformer(), new DSLPlatformMinifiedSerializer(), new SerFeatures(
                SerFormat.JSON, SerGraph.FLAT_TREE, SerClass.CLASSES_KNOWN,
                "JSON with minified property names and without default values."));
    }

    static class DSLPlatformFullSerializer extends Serializer<MediaContentFull> {
        private static JsonWriter writer = new JsonWriter();
        private final char[] tmp = new char[64];

        @Override
        public String getName() {
            return "json/dsl-platform";
        }

        @Override
        public MediaContentFull deserialize(final byte[] array) throws Exception {
            return (MediaContentFull) MediaContentFull.deserialize(new JsonReader<Object>(array, null, tmp));
        }

        @Override
        public byte[] serialize(final MediaContentFull content) throws Exception {
            writer.reset();
            content.serialize(writer, false);
            return writer.toByteArray();
        }
    }

   static class DSLPlatformMinifiedSerializer extends Serializer<MediaContentMinified> {
        private static JsonWriter writer = new JsonWriter();
        private final char[] tmp = new char[64];

        @Override
        public String getName() {
            return "minified-json/dsl-platform";
        }

        @Override
        public MediaContentMinified deserialize(final byte[] array) throws Exception {
            return (MediaContentMinified) MediaContentMinified.deserialize(new JsonReader<Object>(array, null, tmp));
        }

        @Override
        public byte[] serialize(final MediaContentMinified content) throws Exception {
            writer.reset();
            content.serialize(writer, true);
            return writer.toByteArray();
        }
    }


    static Player forward(final data.media.Media.Player player) {
        return player == data.media.Media.Player.JAVA
                ? Player.JAVA
                : Player.FLASH;
    }

    static Size forward(final data.media.Image.Size size) {
        return size == data.media.Image.Size.SMALL
                ? Size.SMALL
                : Size.LARGE;
    }

    static data.media.Media.Player reverse(final Player player) {
        return player == Player.JAVA
                ? data.media.Media.Player.JAVA
                : data.media.Media.Player.FLASH;
    }

    static data.media.Image.Size reverse(final Size size) {
        return size == Size.SMALL
                ? data.media.Image.Size.SMALL
                : data.media.Image.Size.LARGE;
    }


    static final class DSLPlatformFullMediaTransformer extends MediaTransformer<MediaContentFull> {
        @Override
        public MediaContentFull[] resultArray(final int size) {
            return new MediaContentFull[size];
        }

        @Override
        public MediaContentFull forward(final data.media.MediaContent commonMediaContent) {
            return new MediaContentFull(forward(commonMediaContent.media), forward(commonMediaContent.images));
        }

        private static MediaFull forward(final data.media.Media media) {
            return new MediaFull(
                    media.uri,
                    media.title,
                    media.width,
                    media.height,
                    media.format,
                    media.duration,
                    media.size,
                    media.bitrate,
                    media.persons,
                    DSLPlatform.forward(media.player),
                    media.copyright);
        }

        private static List<ImageFull> forward(final List<data.media.Image> images) {
            final ArrayList<ImageFull> forwardedImgs = new ArrayList<ImageFull>(images.size());
            for (final data.media.Image image : images) {
                forwardedImgs.add(forward(image));
            }
            return forwardedImgs;
        }

        private static ImageFull forward(final data.media.Image image) {
            return new ImageFull(
                    image.uri,
                    image.title,
                    image.width,
                    image.height,
                    DSLPlatform.forward(image.size));
        }

        @Override
        public data.media.MediaContent reverse(final MediaContentFull mc) {
            return new data.media.MediaContent(reverse(mc.getMedia()), reverse(mc.getImages()));
        }

        private static data.media.Media reverse(final MediaFull media) {
            return new data.media.Media(
                    media.getUri(),
                    media.getTitle(),
                    media.getWidth(),
                    media.getHeight(),
                    media.getFormat(),
                    media.getDuration(),
                    media.getSize(),
                    media.getBitrate(),
                    media.getBitrate() != 0,
                    media.getPersons(),
                    DSLPlatform.reverse(media.getPlayer()),
                    media.getCopyright());
        }
        private static List<data.media.Image> reverse(final List<ImageFull> images) {
            final ArrayList<data.media.Image> reversed = new ArrayList<data.media.Image>(images.size());
            for (final ImageFull image : images) {
                reversed.add(reverse(image));
            }
            return reversed;
        }

        private static data.media.Image reverse(final ImageFull image) {
            return new data.media.Image(
                    image.getUri(),
                    image.getTitle(),
                    image.getWidth(),
                    image.getHeight(),
                    DSLPlatform.reverse(image.getSize()));
        }

        @Override
        public data.media.MediaContent shallowReverse(final MediaContentFull mc) {
            return new data.media.MediaContent(reverse(mc.getMedia()), Collections.<data.media.Image> emptyList());
        }
    }

    static final class DSLPlatformMinifiedMediaTransformer extends MediaTransformer<MediaContentMinified> {
        @Override
        public MediaContentMinified[] resultArray(final int size) {
            return new MediaContentMinified[size];
        }

        @Override
        public MediaContentMinified forward(final data.media.MediaContent commonMediaContent) {
            return new MediaContentMinified(forward(commonMediaContent.media), forward(commonMediaContent.images));
        }

        private static MediaMinified forward(final data.media.Media media) {
            return new MediaMinified(
                    media.uri,
                    media.title,
                    media.width,
                    media.height,
                    media.format,
                    media.duration,
                    media.size,
                    media.bitrate,
                    media.persons,
                    DSLPlatform.forward(media.player),
                    media.copyright);
        }

        private static List<ImageMinified> forward(final List<data.media.Image> images) {
            final ArrayList<ImageMinified> forwardedImgs = new ArrayList<ImageMinified>(images.size());
            for (final data.media.Image image : images) {
                forwardedImgs.add(forward(image));
            }
            return forwardedImgs;
        }

        private static ImageMinified forward(final data.media.Image image) {
            return new ImageMinified(
                    image.uri,
                    image.title,
                    image.width,
                    image.height,
                    DSLPlatform.forward(image.size));
        }

        @Override
        public data.media.MediaContent reverse(final MediaContentMinified mc) {
            return new data.media.MediaContent(reverse(mc.getMedia()), reverse(mc.getImages()));
        }

        private static data.media.Media reverse(final MediaMinified media) {
            return new data.media.Media(
                    media.getUri(),
                    media.getTitle(),
                    media.getWidth(),
                    media.getHeight(),
                    media.getFormat(),
                    media.getDuration(),
                    media.getSize(),
                    media.getBitrate(),
                    media.getBitrate() != 0,
                    media.getPersons(),
                    DSLPlatform.reverse(media.getPlayer()),
                    media.getCopyright());
        }
        private static List<data.media.Image> reverse(final List<ImageMinified> images) {
            final ArrayList<data.media.Image> reversed = new ArrayList<data.media.Image>(images.size());
            for (final ImageMinified image : images) {
                reversed.add(reverse(image));
            }
            return reversed;
        }

        private static data.media.Image reverse(final ImageMinified image) {
            return new data.media.Image(
                    image.getUri(),
                    image.getTitle(),
                    image.getWidth(),
                    image.getHeight(),
                    DSLPlatform.reverse(image.getSize()));
        }

        @Override
        public data.media.MediaContent shallowReverse(final MediaContentMinified mc) {
            return new data.media.MediaContent(reverse(mc.getMedia()), Collections.<data.media.Image> emptyList());
        }
    }
}
