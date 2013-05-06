package serializers.javaxjson;

import static data.media.FieldMapping.FULL_FIELD_NAME_BITRATE;
import static data.media.FieldMapping.FULL_FIELD_NAME_COPYRIGHT;
import static data.media.FieldMapping.FULL_FIELD_NAME_DURATION;
import static data.media.FieldMapping.FULL_FIELD_NAME_FORMAT;
import static data.media.FieldMapping.FULL_FIELD_NAME_HEIGHT;
import static data.media.FieldMapping.FULL_FIELD_NAME_IMAGES;
import static data.media.FieldMapping.FULL_FIELD_NAME_MEDIA;
import static data.media.FieldMapping.FULL_FIELD_NAME_PERSONS;
import static data.media.FieldMapping.FULL_FIELD_NAME_PLAYER;
import static data.media.FieldMapping.FULL_FIELD_NAME_SIZE;
import static data.media.FieldMapping.FULL_FIELD_NAME_TITLE;
import static data.media.FieldMapping.FULL_FIELD_NAME_URI;
import static data.media.FieldMapping.FULL_FIELD_NAME_WIDTH;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.spi.JsonProvider;

import serializers.Serializer;
import serializers.TestGroups;
import serializers.Transformer;
import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

public class JavaxJsonDriver extends Serializer<JsonStructure> {
	private static final JsonProvider json = JsonProvider.provider();
	
	@Override
	public JsonStructure deserialize(byte[] array) throws Exception {
		return json.createReader(new ByteArrayInputStream(array)).read();
	}

	@Override
	public byte[] serialize(JsonStructure content) throws Exception {
		ByteArrayOutputStream outputStream = outputStream(content);
		JsonWriter writer = json.createWriter(outputStream);
		writer.write(content);
		writer.close();
		return outputStream.toByteArray();
	}

	@Override
	public String getName() {
		return "JavaxJson";
	}

	public static void register(TestGroups groups) {
		groups.media.add(new JavaxJsonTransformer(), new JavaxJsonDriver());
	}

	private static class JavaxJsonTransformer extends Transformer<MediaContent, JsonStructure> {

		@Override
		public JsonStructure forward(MediaContent content) {
			Media media = content.media;
			JsonObjectBuilder mediaJson = json.createObjectBuilder();
			mediaJson.add(FULL_FIELD_NAME_BITRATE, media.bitrate);
			if(media.copyright == null ){
				mediaJson.add(FULL_FIELD_NAME_COPYRIGHT, JsonValue.NULL);	
			} else{
				mediaJson.add(FULL_FIELD_NAME_COPYRIGHT, media.copyright);
			}
			
			mediaJson.add(FULL_FIELD_NAME_DURATION, media.duration);
			mediaJson.add(FULL_FIELD_NAME_FORMAT, media.format);
			mediaJson.add(FULL_FIELD_NAME_HEIGHT, media.height);
			JsonArrayBuilder personsJson = json.createArrayBuilder();
			for (String person : media.persons) {
				personsJson.add(person);
			}
			mediaJson.add(FULL_FIELD_NAME_PERSONS, personsJson);
			mediaJson.add(FULL_FIELD_NAME_PLAYER, media.player.name());
			mediaJson.add(FULL_FIELD_NAME_SIZE, media.size);
			mediaJson.add(FULL_FIELD_NAME_TITLE, media.title);
			mediaJson.add(FULL_FIELD_NAME_URI, media.uri);
			mediaJson.add(FULL_FIELD_NAME_WIDTH, media.width);
			JsonArrayBuilder imagesJson = json.createArrayBuilder();
			for (Image image : content.images) {
				JsonObjectBuilder imageJson = json.createObjectBuilder();
				imageJson.add(FULL_FIELD_NAME_TITLE, image.title);
				imageJson.add(FULL_FIELD_NAME_URI, image.uri);
				imageJson.add(FULL_FIELD_NAME_HEIGHT, image.height);
				imageJson.add(FULL_FIELD_NAME_WIDTH, image.width);
				imageJson.add(FULL_FIELD_NAME_SIZE, image.size.name());
				imagesJson.add(imageJson);
			}
			JsonObjectBuilder contentJson = json.createObjectBuilder();
			contentJson.add(FULL_FIELD_NAME_MEDIA, mediaJson);
			contentJson.add(FULL_FIELD_NAME_IMAGES, imagesJson);
			return contentJson.build();
		}

		@Override
		public MediaContent reverse(JsonStructure content) {
				return extractContent(content);
		}

		private MediaContent extractContent(JsonStructure content) {
			JsonObject mediaContentJson = (JsonObject) content;
			MediaContent mc = new MediaContent();
			JsonObject mediaJson = mediaContentJson.getJsonObject(FULL_FIELD_NAME_MEDIA);
			Media media = new Media();
			media.player = Media.Player.find(mediaJson.getString(FULL_FIELD_NAME_PLAYER));
			media.uri = getStringProperty(mediaJson, FULL_FIELD_NAME_URI);
			media.title = getStringProperty(mediaJson, FULL_FIELD_NAME_TITLE);
			media.width = getIntProperty(mediaJson, FULL_FIELD_NAME_WIDTH);
			media.height =  getIntProperty(mediaJson, FULL_FIELD_NAME_HEIGHT);
			media.format = getStringProperty(mediaJson, FULL_FIELD_NAME_FORMAT);
			media.duration = getIntProperty(mediaJson, FULL_FIELD_NAME_DURATION);
			media.size = getIntProperty(mediaJson, FULL_FIELD_NAME_SIZE);
			media.bitrate =  getIntProperty(mediaJson, FULL_FIELD_NAME_BITRATE);
			media.hasBitrate = true;
			media.copyright = getStringProperty(mediaJson, FULL_FIELD_NAME_COPYRIGHT);
			JsonArray personsJson = getCheckedArrayProperty(mediaJson, FULL_FIELD_NAME_PERSONS);
			List<String> persons = new ArrayList<String>();
			for (JsonValue value : personsJson) {
				persons.add(value.toString());
			}
			media.persons = persons;
			mc.media = media;
			JsonArray imagesJson = getCheckedArrayProperty(mediaContentJson, FULL_FIELD_NAME_IMAGES);
			List<Image> images = new ArrayList<Image>();
			for (JsonValue value : imagesJson) {
				Image image = new Image();
				JsonObject imageJson = (JsonObject) value;
				image.uri = getStringProperty(imageJson, FULL_FIELD_NAME_URI);
				image.title = getStringProperty(imageJson, FULL_FIELD_NAME_TITLE);
				image.width =  getIntProperty(imageJson, FULL_FIELD_NAME_WIDTH);
				image.height =  getIntProperty(imageJson, FULL_FIELD_NAME_HEIGHT);
				image.size = Image.Size.valueOf(getStringProperty(imageJson, FULL_FIELD_NAME_SIZE));
				images.add(image);
			}
			mc.images = images;
			return mc;
		}

		private JsonArray getCheckedArrayProperty(JsonObject mediaJson, String fullFieldNamePersons) {
			return mediaJson.getJsonArray(fullFieldNamePersons);
		}

		private int getIntProperty(JsonObject mediaJson, String fullFieldNameWidth) {
			return mediaJson.getInt(fullFieldNameWidth);
		}

		private String getStringProperty(JsonObject mediaJson, String fullFieldNameUri) {
			if(mediaJson.isNull(fullFieldNameUri)) return null;
			return mediaJson.getString(fullFieldNameUri);
		}

		@Override
		public MediaContent shallowReverse(JsonStructure a) {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public MediaContent[] sourceArray(int size) {
			return new MediaContent[size];
		}

		@Override
		public JsonStructure[] resultArray(int size) {
			return new JsonStructure[size];
		}
	}
}
