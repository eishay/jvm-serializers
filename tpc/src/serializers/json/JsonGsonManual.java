package serializers.json;

import static data.media.FieldMapping.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import serializers.*;

import com.google.gson.stream.*;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

/**
 * Driver that uses the new streaming parser of GSON, with fully
 * manual data binding for optimal performance.
 */
public class JsonGsonManual
{
    public static void register(TestGroups groups)
    {
            groups.media.add(JavaBuiltIn.mediaTransformer,
                    new ManualSerializer("json/gson/manual"),
                    new SerFeatures(
                            SerFormat.JSON,
                            SerGraph.FLAT_TREE,
                            SerClass.MANUAL_OPT,
                            ""
                    )
            );
    }

    // ------------------------------------------------------------
    // Serializer (just one)

    static class ManualSerializer extends Serializer<MediaContent>
    {
        private final String name;

        public ManualSerializer(String name) {
            this.name = name;
        }

        public String getName() { return name; }
        
        public MediaContent deserialize(byte[] array) throws Exception
        {
            Reader r = new StringReader(new String(array, "UTF-8"));
            JsonReader reader = new JsonReader(r);
            MediaContent content = readMediaContent(reader);            
            r.close();
            return content;
        }

        public byte[] serialize(MediaContent data) throws IOException
        {
            StringWriter w = new StringWriter();
            JsonWriter writer = new JsonWriter(w);
            writeMediaContent(writer, data);
            writer.close();
            w.flush();
            return w.toString().getBytes("UTF-8");
        }

        // // // Read methods

        protected MediaContent readMediaContent(JsonReader parser) throws IOException
        {
                MediaContent mc = new MediaContent();
                if (parser.peek() != JsonToken.BEGIN_OBJECT) {
                    reportIllegal(parser, JsonToken.BEGIN_OBJECT);
                }
                parser.beginObject();
                
                // loop for main-level fields
                JsonToken t;

                while ((t = parser.peek()) != JsonToken.END_OBJECT) {
                    if (t != JsonToken.NAME) {
                        reportIllegal(parser, JsonToken.NAME);
                    }
                    String field = parser.nextName();
                    Integer I = fullFieldToIndex.get(field);
                    if (I != null) {
                        switch (I) {
                        case FIELD_IX_MEDIA:
                            mc.media = readMedia(parser);
                            continue;
                        case FIELD_IX_IMAGES:
                            if (parser.peek() != JsonToken.BEGIN_ARRAY) {
                                reportIllegal(parser, JsonToken.BEGIN_ARRAY);
                            }
                            parser.beginArray();
                            List<Image> images = new ArrayList<Image>();
                            while (parser.peek() == JsonToken.BEGIN_OBJECT) {
                                parser.beginObject();
                                images.add(readImage(parser));
                            }
                            parser.endArray();
                            mc.images = images;
                            continue;
                        }
                    }
                    throw new IllegalStateException("Unexpected field '"+field+"'");
                }
                parser.endObject();

                if (mc.media == null) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_MEDIA);
                if (mc.images == null) mc.images = new ArrayList<Image>();

                return mc;
        }

        private Media readMedia(JsonReader parser) throws IOException
        {
                if (parser.peek() != JsonToken.BEGIN_OBJECT) {
                    reportIllegal(parser, JsonToken.BEGIN_OBJECT);
                }
                parser.beginObject();
                Media media = new Media();
                JsonToken t;

                boolean haveWidth = false;
                boolean haveHeight = false;
                boolean haveDuration = false;
                boolean haveSize = false;

                while ((t = parser.peek()) != JsonToken.END_OBJECT) {
                    if (t != JsonToken.NAME) {
                        reportIllegal(parser, JsonToken.NAME);
                    }
                    // read value token (or BEGIN_ARRAY)
                    String field = parser.nextName();
                    Integer I = fullFieldToIndex.get(field);
                    if (I != null) {
                        switch (I) {
                        case FIELD_IX_PLAYER:
                            media.player = Media.Player.valueOf(parser.nextString());
                            continue;
                        case FIELD_IX_URI:
                            media.uri = parser.nextString();
                            continue;
                        case FIELD_IX_TITLE:
                            media.title = parser.nextString();
                            continue;
                        case FIELD_IX_WIDTH:
                            media.width = parser.nextInt();
                            haveWidth = true;
                            continue;
                        case FIELD_IX_HEIGHT:
                            media.height = parser.nextInt();
                            haveHeight = true;
                            continue;
                        case FIELD_IX_FORMAT:
                            media.format = parser.nextString();
                            continue;
                        case FIELD_IX_DURATION:
                            media.duration = parser.nextLong();
                            haveDuration = true;
                            continue;
                        case FIELD_IX_SIZE:
                            media.size = parser.nextLong();
                            haveSize = true;
                            continue;
                        case FIELD_IX_BITRATE:
                            media.bitrate = parser.nextInt();
                            media.hasBitrate = true;
                            continue;
                        case FIELD_IX_PERSONS:
                            if (parser.peek() != JsonToken.BEGIN_ARRAY) {
                                reportIllegal(parser, JsonToken.BEGIN_ARRAY);
                            }
                            parser.beginArray();
                            List<String> persons = new ArrayList<String>();
                            while (parser.peek() != JsonToken.END_ARRAY) {
                                persons.add(parser.nextString());
                            }
                            parser.endArray();
                            media.persons = persons;
                            continue;
                        case FIELD_IX_COPYRIGHT:
                            media.copyright = parser.nextString();
                            continue;
                        }
                    }
                    throw new IllegalStateException("Unexpected field '"+field+"'");
                }

                parser.endObject();
                
                if (media.uri == null) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_URI);
                if (!haveWidth) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_WIDTH);
                if (!haveHeight) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_HEIGHT);
                if (media.format == null) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_FORMAT);
                if (!haveDuration) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_DURATION);
                if (!haveSize) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_SIZE);
                if (media.persons == null) media.persons = new ArrayList<String>();
                if (media.player == null) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_PLAYER);

                return media;
        }

        private Image readImage(JsonReader parser) throws IOException
        {
            JsonToken t;
            Image image = new Image();

            boolean haveWidth = false;
            boolean haveHeight = false;

            while ((t = parser.peek()) != JsonToken.END_OBJECT) {
                if (t != JsonToken.NAME) {
                    reportIllegal(parser, JsonToken.NAME);
                }
                String field = parser.nextName();
                Integer I = fullFieldToIndex.get(field);
                if (I != null) {
                    switch (I) {
                    case FIELD_IX_URI:
                            image.uri = parser.nextString();
                            continue;
                    case FIELD_IX_TITLE:
                            image.title = parser.nextString();
                            continue;
                    case FIELD_IX_WIDTH:
                            image.width = parser.nextInt();
                            haveWidth = true;
                            continue;
                    case FIELD_IX_HEIGHT:
                            image.height = parser.nextInt();
                            haveHeight = true;
                            continue;
                    case FIELD_IX_SIZE:
                            image.size = Image.Size.valueOf(parser.nextString());
                            continue;
                    }
                }
                throw new IllegalStateException("Unexpected field '"+field+"'");
            }
            parser.endObject();

            if (image.uri == null) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_URI);
            if (!haveWidth) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_WIDTH);
            if (!haveHeight) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_HEIGHT);
            if (image.size == null) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_SIZE);

            return image;
        }

        private void reportIllegal(JsonReader parser, JsonToken expToken)
                throws IOException
        {
                JsonToken curr = parser.peek();
                String msg = "Expected token "+expToken+"; got "+curr;
                if (curr == JsonToken.NAME) {
                    msg += " (current field name '"+parser.nextName()+"')";
                }
                throw new IllegalStateException(msg);
        }
        
        // // // Write methods

        protected void writeMediaContent(JsonWriter writer, MediaContent content) throws IOException
        {
            writer.beginObject();
            writeMedia(writer, content.media);
            writer.name(FULL_FIELD_NAME_IMAGES);
            writer.beginArray();
            for (Image i : content.images) {
                writeImage(writer, i);
            }
            writer.endArray();
            writer.endObject();
        }

        private void writeMedia(JsonWriter writer, Media media) throws IOException
        {
            writer.name(FULL_FIELD_NAME_MEDIA);
            writer.beginObject();
            writeStringField(writer, FULL_FIELD_NAME_PLAYER, media.player.name());
            writeStringField(writer, FULL_FIELD_NAME_URI, media.uri);
            if (media.title != null) {
                writeStringField(writer, FULL_FIELD_NAME_TITLE, media.title);
            }
            writeIntField(writer, FULL_FIELD_NAME_WIDTH, media.width);
            writeIntField(writer, FULL_FIELD_NAME_HEIGHT, media.height);
            writeStringField(writer, FULL_FIELD_NAME_FORMAT, media.format);
            writeLongField(writer, FULL_FIELD_NAME_DURATION, media.duration);
            writeLongField(writer, FULL_FIELD_NAME_SIZE, media.size);
            if (media.hasBitrate) {
                writeIntField(writer, FULL_FIELD_NAME_BITRATE, media.bitrate);
            }
            if (media.copyright != null) {
                writeStringField(writer, FULL_FIELD_NAME_COPYRIGHT, media.copyright);
            }
            writer.name(FULL_FIELD_NAME_PERSONS);
            writer.beginArray();
            for (String person : media.persons) {
                    writer.value(person);
            }
            writer.endArray();
            writer.endObject();
        }

        private void writeStringField(JsonWriter writer, String field, String value) throws IOException {
            writer.name(field);
            writer.value(value);
        }

        private void writeIntField(JsonWriter writer, String field, int value) throws IOException {
            writer.name(field);
            writer.value(value);
        }

        private void writeLongField(JsonWriter writer, String field, long value) throws IOException {
            writer.name(field);
            writer.value(value);
        }
        
        private void writeImage(JsonWriter writer, Image image) throws IOException
        {
            writer.beginObject();
            writeStringField(writer, FULL_FIELD_NAME_URI, image.uri);
            if (image.title != null) {
                writeStringField(writer, FULL_FIELD_NAME_TITLE, image.title);
            }
            writeIntField(writer, FULL_FIELD_NAME_WIDTH, image.width);
            writeIntField(writer, FULL_FIELD_NAME_HEIGHT, image.height);
            writeStringField(writer, FULL_FIELD_NAME_SIZE, image.size.name());
            writer.endObject();
        }
    
    }
}
