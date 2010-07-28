package serializers;

import data.media.*;
import static data.media.FieldMapping.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

public class JsonJackson
{
	public static void register(TestGroups groups)
	{
	    JsonFactory factory = new JsonFactory();
		groups.media.add(JavaBuiltIn.MediaTransformer, new GenericSerializer("json/jackson-manual", factory));
		groups.media.add(JavaBuiltIn.MediaTransformer, new AbbreviatedSerializer("json/jackson-manual-abbrev", factory));
	}

	// ------------------------------------------------------------
	// Serializers

	public static final class GenericSerializer extends Serializer<MediaContent>
	{
	    private final String name;
	    private final JsonFactory _factory;
	    
	    public GenericSerializer(String name, JsonFactory jsonFactory)
	    {
	        this.name = name;
	        _factory = jsonFactory;
	    }
	    
		public String getName() { return name; }

		public final byte[] serialize(MediaContent content) throws Exception
		{
			ByteArrayOutputStream baos = outputStream(content);
			JsonGenerator generator = constructGenerator(baos);
			writeMediaContent(generator, content);
			generator.close();
			return baos.toByteArray();
		}

		public final MediaContent deserialize(byte[] array) throws Exception
		{
			JsonParser parser = constructParser(array);
			MediaContent mc = readMediaContent(parser);
			parser.close();
			return mc;
		}

		// // // Internal methods

		protected JsonParser constructParser(byte[] data) throws IOException
		{
			return _factory.createJsonParser(data, 0, data.length);
		}

		protected JsonGenerator constructGenerator(ByteArrayOutputStream baos) throws IOException
		{
			return _factory.createJsonGenerator(baos, JsonEncoding.UTF8);
		}

		protected void writeMediaContent(JsonGenerator generator, MediaContent content) throws IOException
		{
			generator.writeStartObject();
			writeMedia(generator, content.media);
			generator.writeFieldName(FULL_FIELD_NAME_IMAGES);
			generator.writeStartArray();
			for (Image i : content.images) {
				writeImage(generator, i);
			}
			generator.writeEndArray();
			generator.writeEndObject();
		}

		private void writeMedia(JsonGenerator generator, Media media) throws IOException
		{
			generator.writeFieldName(FULL_FIELD_NAME_MEDIA);
			generator.writeStartObject();
			generator.writeStringField(FULL_FIELD_NAME_PLAYER, media.player.name());
			generator.writeStringField(FULL_FIELD_NAME_URI, media.uri);
			if (media.title != null) generator.writeStringField(FULL_FIELD_NAME_TITLE, media.title);
			generator.writeNumberField(FULL_FIELD_NAME_WIDTH, media.width);
			generator.writeNumberField(FULL_FIELD_NAME_HEIGHT, media.height);
			generator.writeStringField(FULL_FIELD_NAME_FORMAT, media.format);
			generator.writeNumberField(FULL_FIELD_NAME_DURATION, media.duration);
			generator.writeNumberField(FULL_FIELD_NAME_SIZE, media.size);
			if (media.hasBitrate) generator.writeNumberField(FULL_FIELD_NAME_BITRATE, media.bitrate);
			if (media.copyright != null) generator.writeStringField(FULL_FIELD_NAME_COPYRIGHT, media.copyright);
			generator.writeFieldName(FULL_FIELD_NAME_PERSONS);
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
			generator.writeStringField(FULL_FIELD_NAME_URI, image.uri);
			if (image.title != null) generator.writeStringField(FULL_FIELD_NAME_TITLE, image.title);
			generator.writeNumberField(FULL_FIELD_NAME_WIDTH, image.width);
			generator.writeNumberField(FULL_FIELD_NAME_HEIGHT, image.height);
			generator.writeStringField(FULL_FIELD_NAME_SIZE, image.size.name());
			generator.writeEndObject();
		}

		protected MediaContent readMediaContent(JsonParser parser) throws IOException
		{
			MediaContent mc = new MediaContent();
			if (parser.nextToken() != JsonToken.START_OBJECT) {
				reportIllegal(parser, JsonToken.START_OBJECT);
			}
			// loop for main-level fields
			JsonToken t;

			while ((t = parser.nextToken()) != JsonToken.END_OBJECT) {
				if (t != JsonToken.FIELD_NAME) {
					reportIllegal(parser, JsonToken.FIELD_NAME);
				}
				String field = parser.getCurrentName();
				Integer I = fullFieldToIndex.get(field);
				if (I != null) {
					switch (I) {
						case FIELD_IX_MEDIA:
							mc.media = readMedia(parser);
							continue;
						case FIELD_IX_IMAGES:
							if (parser.nextToken() != JsonToken.START_ARRAY) {
								reportIllegal(parser, JsonToken.START_ARRAY);
							}
							List<Image> images = new ArrayList<Image>();
							while (parser.nextToken() == JsonToken.START_OBJECT) {
								images.add(readImage(parser));
							}
							mc.images = images;
							continue;
					}
				}
				throw new IllegalStateException("Unexpected field '"+field+"'");
			}

			if (mc.media == null) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_MEDIA);
			if (mc.images == null) mc.images = new ArrayList<Image>();

			return mc;
		}

		private Media readMedia(JsonParser parser) throws IOException
		{
			if (parser.nextToken() != JsonToken.START_OBJECT) {
				reportIllegal(parser, JsonToken.START_OBJECT);
			}
			Media media = new Media();
			JsonToken t;

			boolean haveWidth = false;
			boolean haveHeight = false;
			boolean haveDuration = false;
			boolean haveSize = false;

			while ((t = parser.nextToken()) != JsonToken.END_OBJECT) {
				if (t != JsonToken.FIELD_NAME) {
					reportIllegal(parser, JsonToken.FIELD_NAME);
				}
				// read value token (or START_ARRAY)
				String field = parser.getCurrentName();
				t = parser.nextToken();
				Integer I = fullFieldToIndex.get(field);
				if (I != null) {
					switch (I) {
						case FIELD_IX_PLAYER:
							media.player = Media.Player.valueOf(parser.getText());
							continue;
						case FIELD_IX_URI:
							media.uri = parser.getText();
							continue;
						case FIELD_IX_TITLE:
							media.title = parser.getText();
							continue;
						case FIELD_IX_WIDTH:
							media.width = parser.getIntValue();
							haveWidth = true;
							continue;
						case FIELD_IX_HEIGHT:
							media.height = parser.getIntValue();
							haveHeight = true;
							continue;
						case FIELD_IX_FORMAT:
							media.format = parser.getText();
							continue;
						case FIELD_IX_DURATION:
							media.duration = parser.getLongValue();
							haveDuration = true;
							continue;
						case FIELD_IX_SIZE:
							media.size = parser.getLongValue();
							haveSize = true;
							continue;
						case FIELD_IX_BITRATE:
							media.bitrate = parser.getIntValue();
							media.hasBitrate = true;
							continue;
						case FIELD_IX_PERSONS:
							if (t != JsonToken.START_ARRAY) {
								reportIllegal(parser, JsonToken.START_ARRAY);
							}
							List<String> persons = new ArrayList<String>();
							while (parser.nextToken() != JsonToken.END_ARRAY) {
								persons.add(parser.getText());
							}
							media.persons = persons;
							continue;
						case FIELD_IX_COPYRIGHT:
							media.copyright = parser.getText();
							continue;
					}
				}
				throw new IllegalStateException("Unexpected field '"+field+"'");
			}

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

		private Image readImage(JsonParser parser) throws IOException
		{
			JsonToken t;
			Image image = new Image();

			boolean haveWidth = false;
			boolean haveHeight = false;

			while ((t = parser.nextToken()) != JsonToken.END_OBJECT) {
				if (t != JsonToken.FIELD_NAME) {
					reportIllegal(parser, JsonToken.FIELD_NAME);
				}
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

			if (image.uri == null) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_URI);
			if (!haveWidth) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_WIDTH);
			if (!haveHeight) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_HEIGHT);
			if (image.size == null) throw new IllegalStateException("Missing field: " + FULL_FIELD_NAME_SIZE);

			return image;
		}

		private void reportIllegal(JsonParser parser, JsonToken expToken)
			throws IOException
		{
			JsonToken curr = parser.getCurrentToken();
			String msg = "Expected token "+expToken+"; got "+curr;
			if (curr == JsonToken.FIELD_NAME) {
				msg += " (current field name '"+parser.getCurrentName()+"')";
			}
			throw new IllegalStateException(msg);
		}
	};

	public static final class AbbreviatedSerializer extends Serializer<MediaContent>
	{
            private final String name;
            private final JsonFactory _factory;

            public AbbreviatedSerializer(String name, JsonFactory jsonFactory)
            {
                this.name = name;
                _factory = jsonFactory;
            }
            
            public String getName() { return name; }

		public final byte[] serialize(MediaContent content) throws Exception
		{
			ByteArrayOutputStream baos = outputStream(content);
			JsonGenerator generator = constructGenerator(baos);
			writeMediaContent(generator, content);
			generator.close();
			return baos.toByteArray();
		}

		public final MediaContent deserialize(byte[] array) throws Exception
		{
			JsonParser parser = constructParser(array);
			MediaContent mc = readMediaContent(parser);
			parser.close();
			return mc;
		}

		// // // Internal methods

		protected JsonParser constructParser(byte[] data) throws IOException
		{
			return _factory.createJsonParser(data, 0, data.length);
		}

		protected JsonGenerator constructGenerator(ByteArrayOutputStream baos) throws IOException
		{
			return _factory.createJsonGenerator(baos, JsonEncoding.UTF8);
		}

		protected void writeMediaContent(JsonGenerator generator, MediaContent content) throws IOException
		{
			generator.writeStartObject();
			writeMedia(generator, content.media);
			generator.writeFieldName(FIELD_NAME_IMAGES);
			generator.writeStartArray();
			for (Image i : content.images) {
				writeImage(generator, i);
			}
			generator.writeEndArray();
			generator.writeEndObject();
		}

		private void writeMedia(JsonGenerator generator, Media media) throws IOException
		{
			generator.writeFieldName(FIELD_NAME_MEDIA);
			generator.writeStartObject();
			generator.writeStringField(FIELD_NAME_PLAYER, media.player.name());
			generator.writeStringField(FIELD_NAME_URI, media.uri);
			if (media.title != null) generator.writeStringField(FIELD_NAME_TITLE, media.title);
			generator.writeNumberField(FIELD_NAME_WIDTH, media.width);
			generator.writeNumberField(FIELD_NAME_HEIGHT, media.height);
			generator.writeStringField(FIELD_NAME_FORMAT, media.format);
			generator.writeNumberField(FIELD_NAME_DURATION, media.duration);
			generator.writeNumberField(FIELD_NAME_SIZE, media.size);
			if (media.hasBitrate) generator.writeNumberField(FIELD_NAME_BITRATE, media.bitrate);
			if (media.copyright != null) generator.writeStringField(FIELD_NAME_COPYRIGHT, media.copyright);
			generator.writeFieldName(FIELD_NAME_PERSONS);
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
			generator.writeStringField(FIELD_NAME_URI, image.uri);
			if (image.title != null) generator.writeStringField(FIELD_NAME_TITLE, image.title);
			generator.writeNumberField(FIELD_NAME_WIDTH, image.width);
			generator.writeNumberField(FIELD_NAME_HEIGHT, image.height);
			generator.writeStringField(FIELD_NAME_SIZE, image.size.name());
			generator.writeEndObject();
		}

		protected MediaContent readMediaContent(JsonParser parser) throws IOException
		{
			MediaContent mc = new MediaContent();
			if (parser.nextToken() != JsonToken.START_OBJECT) {
				reportIllegal(parser, JsonToken.START_OBJECT);
			}
			// loop for main-level fields
			JsonToken t;

			while ((t = parser.nextToken()) != JsonToken.END_OBJECT) {
				if (t != JsonToken.FIELD_NAME) {
					reportIllegal(parser, JsonToken.FIELD_NAME);
				}
				String field = parser.getCurrentName();
				Integer I = fieldToIndex.get(field);
				if (I != null) {
				switch (I) {
					case FIELD_IX_MEDIA:
						mc.media = readMedia(parser);
						continue;
					case FIELD_IX_IMAGES:
						if (parser.nextToken() != JsonToken.START_ARRAY) {
							reportIllegal(parser, JsonToken.START_ARRAY);
						}
						List<Image> images = new ArrayList<Image>();
						while (parser.nextToken() == JsonToken.START_OBJECT) {
							images.add(readImage(parser));
						}
						mc.images = images;
						continue;
					}
				}
				throw new IllegalStateException("Unexpected field '"+field+"'");
			}

			if (mc.media == null) throw new IllegalStateException("Missing field: " + FIELD_NAME_MEDIA);
			if (mc.images == null) mc.images = new ArrayList<Image>();

			return mc;
		}

		private Media readMedia(JsonParser parser) throws IOException
		{
			if (parser.nextToken() != JsonToken.START_OBJECT) {
				reportIllegal(parser, JsonToken.START_OBJECT);
			}
			Media media = new Media();
			JsonToken t;

			boolean haveWidth = false;
			boolean haveHeight = false;
			boolean haveDuration = false;
			boolean haveSize = false;

			while ((t = parser.nextToken()) != JsonToken.END_OBJECT) {
				if (t != JsonToken.FIELD_NAME) {
					reportIllegal(parser, JsonToken.FIELD_NAME);
				}
				// read value token (or START_ARRAY)
				String field = parser.getCurrentName();
				t = parser.nextToken();
				Integer I = fieldToIndex.get(field);
				if (I != null) {
					switch (I) {
						case FIELD_IX_PLAYER:
							media.player = Media.Player.valueOf(parser.getText());
							continue;
						case FIELD_IX_URI:
							media.uri = parser.getText();
							continue;
						case FIELD_IX_TITLE:
							media.title = parser.getText();
							continue;
						case FIELD_IX_WIDTH:
							media.width = parser.getIntValue();
							haveWidth = true;
							continue;
						case FIELD_IX_HEIGHT:
							media.height = parser.getIntValue();
							haveHeight = true;
							continue;
						case FIELD_IX_FORMAT:
							media.format = parser.getText();
							continue;
						case FIELD_IX_DURATION:
							media.duration = parser.getLongValue();
							haveDuration = true;
							continue;
						case FIELD_IX_SIZE:
							media.size = parser.getLongValue();
							haveSize = true;
							continue;
						case FIELD_IX_BITRATE:
							media.bitrate = parser.getIntValue();
							media.hasBitrate = true;
							continue;
						case FIELD_IX_PERSONS:
							if (t != JsonToken.START_ARRAY) {
								reportIllegal(parser, JsonToken.START_ARRAY);
							}
							List<String> persons = new ArrayList<String>();
							while (parser.nextToken() != JsonToken.END_ARRAY) {
								persons.add(parser.getText());
							}
							media.persons = persons;
							continue;
						case FIELD_IX_COPYRIGHT:
							media.copyright = parser.getText();
							continue;
					}
				}
				throw new IllegalStateException("Unexpected field '"+field+"'");
			}

			if (media.uri == null) throw new IllegalStateException("Missing field: " + FIELD_NAME_URI);
			if (!haveWidth) throw new IllegalStateException("Missing field: " + FIELD_NAME_WIDTH);
			if (!haveHeight) throw new IllegalStateException("Missing field: " + FIELD_NAME_HEIGHT);
			if (media.format == null) throw new IllegalStateException("Missing field: " + FIELD_NAME_FORMAT);
			if (!haveDuration) throw new IllegalStateException("Missing field: " + FIELD_NAME_DURATION);
			if (!haveSize) throw new IllegalStateException("Missing field: " + FIELD_NAME_SIZE);
			if (media.persons == null) media.persons = new ArrayList<String>();
			if (media.player == null) throw new IllegalStateException("Missing field: " + FIELD_NAME_PLAYER);

			return media;
		}

		private Image readImage(JsonParser parser) throws IOException
		{
			JsonToken t;
			Image image = new Image();

			boolean haveWidth = false;
			boolean haveHeight = false;

			while ((t = parser.nextToken()) != JsonToken.END_OBJECT) {
				if (t != JsonToken.FIELD_NAME) {
					reportIllegal(parser, JsonToken.FIELD_NAME);
				}
				String field = parser.getCurrentName();
				// read value token (or START_ARRAY)
				parser.nextToken();
				Integer I = fieldToIndex.get(field);
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

			if (image.uri == null) throw new IllegalStateException("Missing field: " + FIELD_NAME_URI);
			if (!haveWidth) throw new IllegalStateException("Missing field: " + FIELD_NAME_WIDTH);
			if (!haveHeight) throw new IllegalStateException("Missing field: " + FIELD_NAME_HEIGHT);
			if (image.size == null) throw new IllegalStateException("Missing field: " + FIELD_NAME_SIZE);

			return image;
		}

		private void reportIllegal(JsonParser parser, JsonToken expToken)
			throws IOException
		{
			JsonToken curr = parser.getCurrentToken();
			String msg = "Expected token "+expToken+"; got "+curr;
			if (curr == JsonToken.FIELD_NAME) {
				msg += " (current field name '"+parser.getCurrentName()+"')";
			}
			throw new IllegalStateException(msg);
		}
	};
}
