package serializers.javaxjson;

import static data.media.FieldMapping.*;

import java.io.*;
import java.util.*;

import javax.json.spi.*;
import javax.json.stream.*;
import javax.json.stream.JsonParser.Event;

import org.apache.activemq.util.buffer.ByteArrayInputStream;

import serializers.*;
import data.media.*;

/**
 * Base class for benchmark using the streaming capabilities of javax.json.
 * <p>
 * Code is based of JacksonJsonManual; first copy/pasted then modified to fit
 * javax.json classes. There was a "fast" path version, but given that there's
 * no optimization possible with the API (no nextFieldName like there is in
 * Jackson), the only overhead we have here is the mapping between field name
 * and index.
 */
public abstract class JavaxJsonStream extends Serializer<MediaContent> {
    private final JsonProvider json;

    public JavaxJsonStream(JsonProvider json) {
        this.json = json;
    }

    @Override
    public final byte[] serialize(MediaContent content) throws IOException {
        ByteArrayOutputStream baos = outputStream(content);
        JsonGenerator generator = constructGenerator(baos);
        writeMediaContent(generator, content);
        generator.close();
        return baos.toByteArray();
    }

    @Override
    public final MediaContent deserialize(byte[] array) throws IOException {
        JsonParser parser = constructParser(array);
        MediaContent mc = readMediaContent(parser);
        parser.close();
        return mc;
    }

    private JsonGenerator constructGenerator(ByteArrayOutputStream baos) {
        return json.createGenerator(baos);
    }

    private JsonParser constructParser(byte[] array) {
        return json.createParser(new ByteArrayInputStream(array));
    }

    //////////////////////////////////////////////////
    // Serialization
    //////////////////////////////////////////////////

    protected void writeMediaContent(JsonGenerator generator, MediaContent content) throws IOException {
        generator.writeStartObject();
        generator.writeStartObject(FULL_FIELD_NAME_MEDIA);
        writeMedia(generator, content.media);
        generator.writeStartArray(FULL_FIELD_NAME_IMAGES);
        for (Image i : content.images) {
            writeImage(generator, i);
        }
        generator.writeEnd();
        generator.writeEnd();
    }

    private void writeMedia(JsonGenerator generator, Media media) throws IOException {
        generator.write(FULL_FIELD_NAME_PLAYER, media.player.name());
        generator.write(FULL_FIELD_NAME_URI, media.uri);
        if (media.title != null) {
            generator.write(FULL_FIELD_NAME_TITLE, media.title);
        }
        generator.write(FULL_FIELD_NAME_WIDTH, media.width);
        generator.write(FULL_FIELD_NAME_HEIGHT, media.height);
        generator.write(FULL_FIELD_NAME_FORMAT, media.format);
        generator.write(FULL_FIELD_NAME_DURATION, media.duration);
        generator.write(FULL_FIELD_NAME_SIZE, media.size);
        if (media.hasBitrate) {
            generator.write(FULL_FIELD_NAME_BITRATE, media.bitrate);
        }
        if (media.copyright != null) {
            generator.write(FULL_FIELD_NAME_COPYRIGHT, media.copyright);
        }
        generator.writeStartArray(FULL_FIELD_NAME_PERSONS);
        for (String person : media.persons) {
            generator.write(person);
        }
        generator.writeEnd();
        generator.writeEnd();
    }

    private void writeImage(JsonGenerator generator, Image image) throws IOException {
        generator.writeStartObject();
        generator.write(FULL_FIELD_NAME_URI, image.uri);
        if (image.title != null) {
            generator.write(FULL_FIELD_NAME_TITLE, image.title);
        }
        generator.write(FULL_FIELD_NAME_WIDTH, image.width);
        generator.write(FULL_FIELD_NAME_HEIGHT, image.height);
        generator.write(FULL_FIELD_NAME_SIZE, image.size.name());
        generator.writeEnd();
    }

    //////////////////////////////////////////////////
    // Deserialization
    //////////////////////////////////////////////////

    protected MediaContent readMediaContent(JsonParser parser) throws IOException {
        MediaContent mc = new MediaContent();
        Event current;
        if ((current = parser.next()) != Event.START_OBJECT) {
            reportIllegal(parser, current, Event.START_OBJECT);
        }
        while ((current = parser.next()) == Event.KEY_NAME) {
            String field = parser.getString();
            Integer I = fullFieldToIndex.get(field);
            if (I != null) {
                switch (I) {
                    case FIELD_IX_MEDIA:
                        mc.media = readMedia(parser);
                        continue;
                    case FIELD_IX_IMAGES:
                        mc.images = readImages(parser);
                        continue;
                }
            }
            throw new IllegalStateException("Unexpected field '" + field + "'");
        }
        verifyCurrent(parser, current, Event.END_OBJECT);

        if (mc.media == null) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_MEDIA);
        if (mc.images == null) mc.images = new ArrayList<Image>();

        return mc;
    }

    private Media readMedia(JsonParser parser) throws IOException {
        Event current;
        if ((current = parser.next()) != Event.START_OBJECT) {
            reportIllegal(parser, current, Event.START_OBJECT);
        }
        Media media = new Media();
        boolean haveWidth = false;
        boolean haveHeight = false;
        boolean haveDuration = false;
        boolean haveSize = false;

        while ((current = parser.next()) == Event.KEY_NAME) {
            String field = parser.getString();
            Integer I = fullFieldToIndex.get(field);
            if (I != null) {
                switch (I) {
                    case FIELD_IX_PLAYER:
                        current = parser.next();
                        media.player = Media.Player.find(parser.getString());
                        continue;
                    case FIELD_IX_URI:
                        current = parser.next();
                        media.uri = parser.getString();
                        continue;
                    case FIELD_IX_TITLE:
                        current = parser.next();
                        media.title = parser.getString();
                        continue;
                    case FIELD_IX_WIDTH:
                        current = parser.next();
                        media.width = parser.getInt();
                        haveWidth = true;
                        continue;
                    case FIELD_IX_HEIGHT:
                        current = parser.next();
                        media.height = parser.getInt();
                        haveHeight = true;
                        continue;
                    case FIELD_IX_FORMAT:
                        current = parser.next();
                        media.format = parser.getString();
                        continue;
                    case FIELD_IX_DURATION:
                        current = parser.next();
                        media.duration = parser.getInt();
                        haveDuration = true;
                        continue;
                    case FIELD_IX_SIZE:
                        current = parser.next();
                        media.size = parser.getInt();
                        haveSize = true;
                        continue;
                    case FIELD_IX_BITRATE:
                        current = parser.next();
                        media.bitrate = parser.getInt();
                        media.hasBitrate = true;
                        continue;
                    case FIELD_IX_PERSONS:
                        media.persons = readPersons(parser);
                        continue;
                    case FIELD_IX_COPYRIGHT:
                        current = parser.next();
                        media.copyright = parser.getString();
                        continue;
                }
            }
            throw new IllegalStateException("Unexpected field '" + field + "'");
        }
        verifyCurrent(parser, current, Event.END_OBJECT);

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

    private List<Image> readImages(JsonParser parser) throws IOException {
        Event current;
        if ((current = parser.next()) != Event.START_ARRAY) {
            reportIllegal(parser, current, Event.START_ARRAY);
        }
        List<Image> images = new ArrayList<Image>();
        while ((current = parser.next()) == Event.START_OBJECT) {
            images.add(readImage(parser));
        }
        verifyCurrent(parser, current, Event.END_ARRAY);
        return images;
    }

    private List<String> readPersons(JsonParser parser) throws IOException {
        Event current;
        if ((current = parser.next()) != Event.START_ARRAY) {
            reportIllegal(parser, current, Event.START_ARRAY);
        }
        List<String> persons = new ArrayList<String>();
        while ((current = parser.next()) == Event.VALUE_STRING) {
            persons.add(parser.getString());
        }
        verifyCurrent(parser, current, Event.END_ARRAY);
        return persons;
    }

    private Image readImage(JsonParser parser) throws IOException {
        Event current;
        boolean haveWidth = false;
        boolean haveHeight = false;
        Image image = new Image();

        while ((current = parser.next()) == Event.KEY_NAME) {
            String field = parser.getString();
            // read value token (or START_ARRAY)
            current = parser.next();
            Integer I = fullFieldToIndex.get(field);
            if (I != null) {
                switch (I) {
                    case FIELD_IX_URI:
                        image.uri = parser.getString();
                        continue;
                    case FIELD_IX_TITLE:
                        image.title = parser.getString();
                        continue;
                    case FIELD_IX_WIDTH:
                        image.width = parser.getInt();
                        haveWidth = true;
                        continue;
                    case FIELD_IX_HEIGHT:
                        image.height = parser.getInt();
                        haveHeight = true;
                        continue;
                    case FIELD_IX_SIZE:
                        image.size = Image.Size.valueOf(parser.getString());
                        continue;
                }
            }
            throw new IllegalStateException("Unexpected field '" + field + "'");
        }

        if (image.uri == null) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_URI);
        if (!haveWidth) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_WIDTH);
        if (!haveHeight) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_HEIGHT);
        if (image.size == null) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_SIZE);

        verifyCurrent(parser, current, Event.END_OBJECT);

        return image;
    }

    private final void verifyCurrent(JsonParser parser, Event current, Event expToken) throws IOException {
        if (current != expToken) {
            reportIllegal(parser, current, expToken);
        }
    }

    private void reportIllegal(JsonParser parser, Event current, Event expToken) throws IOException {
        String msg = "Expected token " + expToken + "; got " + current;
        if (current == Event.KEY_NAME) {
            msg += " (current field name '" + parser.getString() + "')";
        }
        msg += ", location: " + parser.getLocation();
        throw new IllegalStateException(msg);
    }
}