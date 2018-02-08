package serializers.jackson;

import data.media.*;
import static data.media.FieldMapping.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.io.SerializedString;

import serializers.*;

/**
 * "Hand-written" version of Jackson-based codec. Not optimized for compactness,
 * but code is relatively simple monkey code even if bit verbose.
 */
public class JacksonJsonManual extends BaseJacksonDriver<MediaContent>
{
    protected final static SerializedString FIELD_IMAGES = new SerializedString(FULL_FIELD_NAME_IMAGES);

    protected final static SerializedString FIELD_MEDIA = new SerializedString(FULL_FIELD_NAME_MEDIA);
    protected final static SerializedString FIELD_PLAYER = new SerializedString(FULL_FIELD_NAME_PLAYER);

    protected final static SerializedString FIELD_URI = new SerializedString(FULL_FIELD_NAME_URI);
    protected final static SerializedString FIELD_TITLE = new SerializedString(FULL_FIELD_NAME_TITLE);
    protected final static SerializedString FIELD_WIDTH = new SerializedString(FULL_FIELD_NAME_WIDTH);
    protected final static SerializedString FIELD_HEIGHT = new SerializedString(FULL_FIELD_NAME_HEIGHT);
    protected final static SerializedString FIELD_FORMAT = new SerializedString(FULL_FIELD_NAME_FORMAT);
    protected final static SerializedString FIELD_DURATION = new SerializedString(FULL_FIELD_NAME_DURATION);
    protected final static SerializedString FIELD_SIZE = new SerializedString(FULL_FIELD_NAME_SIZE);
    protected final static SerializedString FIELD_BITRATE = new SerializedString(FULL_FIELD_NAME_BITRATE);
    protected final static SerializedString FIELD_COPYRIGHT = new SerializedString(FULL_FIELD_NAME_COPYRIGHT);
    protected final static SerializedString FIELD_PERSONS = new SerializedString(FULL_FIELD_NAME_PERSONS);
    
    public static void register(TestGroups groups)
    {
        JsonFactory factory = new JsonFactory();
        groups.media.add(JavaBuiltIn.mediaTransformer, new JacksonJsonManual("json/jackson/manual",factory),
                new SerFeatures(SerFormat.JSON,
                        SerGraph.FLAT_TREE,
                        SerClass.MANUAL_OPT,
                        ""
                )
        );
    }

    private final JsonFactory _factory;

    public JacksonJsonManual(String name, JsonFactory jsonFactory)
    {
        super(name);
        _factory = jsonFactory;
    }

    @SuppressWarnings("resource")
    @Override
    public final byte[] serialize(MediaContent content) throws IOException
    {
        ByteArrayOutputStream baos = outputStream(content);
        JsonGenerator generator = constructGenerator(baos);
        writeMediaContent(generator, content);
        generator.close();
        return baos.toByteArray();
    }

    @Override
    public final MediaContent deserialize(byte[] array) throws IOException
    {
        JsonParser parser = constructParser(array);
        MediaContent mc = readMediaContent(parser);
        parser.close();
        return mc;
    }

    @Override
    public final void serializeItems(MediaContent[] items, OutputStream out) throws IOException
    {
        JsonGenerator generator = constructGenerator(out);
        // JSON allows simple sequences, so:
        for (int i = 0, len = items.length; i < len; ++i) {
            writeMediaContent(generator, items[i]);
        }
        generator.close();
    }

    @Override
    public MediaContent[] deserializeItems(InputStream in, int numberOfItems) throws IOException 
    {
        MediaContent[] result = new MediaContent[numberOfItems];
        JsonParser parser = constructParser(in);
        for (int i = 0; i < numberOfItems; ++i) {
            result[i] = readMediaContent(parser);
        }
        parser.close();
        return result;
    }
		
    // // // Internal methods

    protected JsonParser constructParser(byte[] data) throws IOException {
        return _factory.createParser(data, 0, data.length);
    }

    protected JsonParser constructParser(InputStream in) throws IOException {
        return _factory.createParser(in);
    }

    protected JsonGenerator constructGenerator(OutputStream baos) throws IOException {
        return _factory.createGenerator(baos, JsonEncoding.UTF8);
    }

    //////////////////////////////////////////////////
    // Serialization
    //////////////////////////////////////////////////
	    
    protected void writeMediaContent(JsonGenerator generator, MediaContent content) throws IOException
    {
        generator.writeStartObject();
        generator.writeFieldName(FIELD_MEDIA);
        writeMedia(generator, content.media);
        generator.writeFieldName(FIELD_IMAGES);
        generator.writeStartArray();
        for (Image i : content.images) {
            writeImage(generator, i);
        }
        generator.writeEndArray();
        generator.writeEndObject();
    }

    private void writeMedia(JsonGenerator generator, Media media) throws IOException
    {
        generator.writeStartObject();
        generator.writeFieldName(FIELD_PLAYER);
        generator.writeString(media.player.name());
        generator.writeFieldName(FIELD_URI);
        generator.writeString(media.uri);
        if (media.title != null) {
            generator.writeFieldName(FIELD_TITLE);
            generator.writeString(media.title);
        }
        generator.writeFieldName(FIELD_WIDTH);
        generator.writeNumber(media.width);
        generator.writeFieldName(FIELD_HEIGHT);
        generator.writeNumber(media.height);
        generator.writeFieldName(FIELD_FORMAT);
        generator.writeString(media.format);
        generator.writeFieldName(FIELD_DURATION);
        generator.writeNumber(media.duration);
        generator.writeFieldName(FIELD_SIZE);
        generator.writeNumber(media.size);
        if (media.hasBitrate) {
            generator.writeFieldName(FIELD_BITRATE);
            generator.writeNumber(media.bitrate);
        }
        if (media.copyright != null) {
            generator.writeFieldName(FIELD_COPYRIGHT);
            generator.writeString(media.copyright);
        }
        generator.writeFieldName(FIELD_PERSONS);
        generator.writeStartArray();
        for (String person : media.persons) {
            generator.writeString(person);
        }
        generator.writeEndArray();
        generator.writeEndObject();
    }

    private void writeImage(JsonGenerator generator, Image image) throws IOException
    {
        generator.writeStartObject();
        generator.writeFieldName(FIELD_URI);
        generator.writeString(image.uri);
        if (image.title != null) {
            generator.writeFieldName(FIELD_TITLE);
            generator.writeString(image.title);
        }
        generator.writeFieldName(FIELD_WIDTH);
        generator.writeNumber(image.width);
        generator.writeFieldName(FIELD_HEIGHT);
        generator.writeNumber(image.height);
        generator.writeFieldName(FIELD_SIZE);
        generator.writeString(image.size.name());
        generator.writeEndObject();
    }
    
    //////////////////////////////////////////////////
    // Deserialization
    //////////////////////////////////////////////////

    protected MediaContent readMediaContent(JsonParser parser) throws IOException
    {
        MediaContent mc = new MediaContent();
        if (parser.nextToken() != JsonToken.START_OBJECT) {
            reportIllegal(parser, JsonToken.START_OBJECT);
        }
        // first fast version when field-order is as expected
        if (parser.nextFieldName(FIELD_MEDIA)) {
            mc.media = readMedia(parser);
            if (parser.nextFieldName(FIELD_IMAGES)) {
                mc.images = readImages(parser);
                parser.nextToken();
                verifyCurrent(parser, JsonToken.END_OBJECT);
                return mc;
            }
        }
        
        // and fallback if order was changed
        for (; parser.currentToken() == JsonToken.FIELD_NAME; parser.nextToken()) {
            String field = parser.getCurrentName();
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
            throw new IllegalStateException("Unexpected field '"+field+"'");
        }
        verifyCurrent(parser, JsonToken.END_OBJECT);
        
        if (mc.media == null) throw new IllegalStateException("Missing field: " + FIELD_MEDIA);
        if (mc.images == null) mc.images = new ArrayList<Image>();
        
        return mc;
    }

    private Media readMedia(JsonParser parser) throws IOException
    {
        if (parser.nextToken() != JsonToken.START_OBJECT) {
            reportIllegal(parser, JsonToken.START_OBJECT);
        }
        Media media = new Media();
        boolean haveWidth = false;
        boolean haveHeight = false;
        boolean haveDuration = false;
        boolean haveSize = false;

        // As with above, first fast path
        if (parser.nextFieldName(FIELD_PLAYER)) {
            media.player = Media.Player.find(parser.nextTextValue());
            if (parser.nextFieldName(FIELD_URI)) {
                media.uri = parser.nextTextValue();
                if (parser.nextFieldName(FIELD_TITLE)) {
                    media.title = parser.nextTextValue();
                    if (parser.nextFieldName(FIELD_WIDTH)) {
                        haveWidth = true;
                        media.width = parser.nextIntValue(-1);
                        if (parser.nextFieldName(FIELD_HEIGHT)) {
                            haveHeight = true;
                            media.height = parser.nextIntValue(-1);
                            if (parser.nextFieldName(FIELD_FORMAT)) {
                                media.format = parser.nextTextValue();
                                if (parser.nextFieldName(FIELD_DURATION)) {
                                    haveDuration = true;
                                    media.duration = parser.nextLongValue(-1L);
                                    if (parser.nextFieldName(FIELD_SIZE)) {
                                        haveSize = true;
                                        media.size = parser.nextLongValue(-1L);
                                        if (parser.nextFieldName(FIELD_BITRATE)) {
                                            media.bitrate = parser.nextIntValue(-1);
                                            media.hasBitrate = true;
                                            if (parser.nextFieldName(FIELD_COPYRIGHT)) {
                                                media.copyright = parser.nextTextValue();
                                                if (parser.nextFieldName(FIELD_PERSONS)) {
                                                    media.persons = readPersons(parser);
                                                    parser.nextToken();
                                                    verifyCurrent(parser, JsonToken.END_OBJECT);
                                                    return media;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // and if something reorder or missing, general loop:
        
        for (; parser.currentToken() == JsonToken.FIELD_NAME; parser.nextToken()) {
            String field = parser.getCurrentName();
            Integer I = fullFieldToIndex.get(field);
            if (I != null) {
                switch (I) {
                case FIELD_IX_PLAYER:
                    media.player = Media.Player.find(parser.nextTextValue());
                    continue;
                case FIELD_IX_URI:
                    media.uri = parser.nextTextValue();
                    continue;
                case FIELD_IX_TITLE:
                    media.title = parser.nextTextValue();
                    continue;
                case FIELD_IX_WIDTH:
                    media.width = parser.nextIntValue(-1);
                    haveWidth = true;
                    continue;
                case FIELD_IX_HEIGHT:
                    media.height = parser.nextIntValue(-1);
                    haveHeight = true;
                    continue;
                case FIELD_IX_FORMAT:
                    media.format = parser.nextTextValue();
                    continue;
                case FIELD_IX_DURATION:
                    media.duration = parser.nextLongValue(-1L);
                    haveDuration = true;
                    continue;
                case FIELD_IX_SIZE:
                    media.size = parser.nextLongValue(-1L);
                    haveSize = true;
                    continue;
                case FIELD_IX_BITRATE:
                    media.bitrate = parser.nextIntValue(-1);
                    media.hasBitrate = true;
                    continue;
                case FIELD_IX_PERSONS:
                    media.persons = readPersons(parser);
                    continue;
                case FIELD_IX_COPYRIGHT:
                    media.copyright = parser.nextTextValue();
                    continue;
                }
            }
            throw new IllegalStateException("Unexpected field '"+field+"'");
        }
        verifyCurrent(parser, JsonToken.END_OBJECT);
        
        if (media.uri == null) throw new IllegalStateException("Missing field: " + FIELD_URI);
        if (!haveWidth) throw new IllegalStateException("Missing field: " + FIELD_WIDTH);
        if (!haveHeight) throw new IllegalStateException("Missing field: " + FIELD_HEIGHT);
        if (media.format == null) throw new IllegalStateException("Missing field: " + FIELD_FORMAT);
        if (!haveDuration) throw new IllegalStateException("Missing field: " + FIELD_DURATION);
        if (!haveSize) throw new IllegalStateException("Missing field: " + FIELD_SIZE);
        if (media.persons == null) media.persons = new ArrayList<String>();
        if (media.player == null) throw new IllegalStateException("Missing field: " + FIELD_PLAYER);
        
        return media;
    }

    private List<Image> readImages(JsonParser parser) throws IOException
    {
        if (parser.nextToken() != JsonToken.START_ARRAY) {
            reportIllegal(parser, JsonToken.START_ARRAY);
        }
        List<Image> images = new ArrayList<Image>();
        while (parser.nextToken() == JsonToken.START_OBJECT) {
            images.add(readImage(parser));
        }
        verifyCurrent(parser, JsonToken.END_ARRAY);
        return images;
    }

    private List<String> readPersons(JsonParser parser) throws IOException
    {
        if (parser.nextToken() != JsonToken.START_ARRAY) {
            reportIllegal(parser, JsonToken.START_ARRAY);
        }
        List<String> persons = new ArrayList<String>();
        String str;
        while ((str = parser.nextTextValue()) != null) {
            persons.add(str);
        }
        verifyCurrent(parser, JsonToken.END_ARRAY);
        return persons;
    }                
    
    private Image readImage(JsonParser parser) throws IOException
    {
        boolean haveWidth = false;
        boolean haveHeight = false;
        Image image = new Image();
        if (parser.nextFieldName(FIELD_URI)) {
            image.uri = parser.nextTextValue();
            if (parser.nextFieldName(FIELD_TITLE)) {
                image.title = parser.nextTextValue();
                if (parser.nextFieldName(FIELD_WIDTH)) {
                    image.width = parser.nextIntValue(-1);
                    haveWidth = true;
                    if (parser.nextFieldName(FIELD_HEIGHT)) {
                        image.height = parser.nextIntValue(-1);
                        haveHeight = true;
                        if (parser.nextFieldName(FIELD_SIZE)) {
                            image.size = Image.Size.valueOf(parser.nextTextValue());
                            parser.nextToken();
                            verifyCurrent(parser, JsonToken.END_OBJECT);
                            return image;
                        }
                    }
                }
            }
        }
        
        for (; parser.currentToken() == JsonToken.FIELD_NAME; parser.nextToken()) {
            String field = parser.getCurrentName();
            // read value token (or START_ARRAY)
            parser.nextToken();
            Integer I = fullFieldToIndex.get(field);
            if (I != null) {
                switch (I) {
                case FIELD_IX_URI:
                    image.uri = parser.getText();
                    continue;
                case FIELD_IX_TITLE:
                    image.title = parser.getText();
                    continue;
                case FIELD_IX_WIDTH:
                    image.width = parser.getIntValue();
                    haveWidth = true;
                    continue;
                case FIELD_IX_HEIGHT:
                    image.height = parser.getIntValue();
                    haveHeight = true;
                    continue;
                case FIELD_IX_SIZE:
                    image.size = Image.Size.valueOf(parser.getText());
                    continue;
                }
            }
            throw new IllegalStateException("Unexpected field '"+field+"'");
        }
        
        if (image.uri == null) throw new IllegalStateException("Missing field: " + FIELD_URI);
        if (!haveWidth) throw new IllegalStateException("Missing field: " + FIELD_WIDTH);
        if (!haveHeight) throw new IllegalStateException("Missing field: " + FIELD_HEIGHT);
        if (image.size == null) throw new IllegalStateException("Missing field: " + FIELD_SIZE);

        verifyCurrent(parser, JsonToken.END_OBJECT);
        
        return image;
    }
    
    private final void verifyCurrent(JsonParser parser, JsonToken expToken) throws IOException
    {   
        if (parser.currentToken() != expToken) {
            reportIllegal(parser, expToken);
        }
    }

    private void reportIllegal(JsonParser parser, JsonToken expToken) throws IOException
    {
        JsonToken curr = parser.currentToken();
        String msg = "Expected token "+expToken+"; got "+curr;
        if (curr == JsonToken.FIELD_NAME) {
            msg += " (current field name '"+parser.getCurrentName()+"')";
        }
        msg += ", location: "+parser.getTokenLocation();
        throw new IllegalStateException(msg);
    }
}
