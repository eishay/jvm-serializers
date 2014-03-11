package serializers.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import serializers.*;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

/**
 * Driver that uses JSON.simple [http://code.google.com/p/json-simple/], with manual parsing with a ContentHandler.
 */
public class JsonSimpleWithContentHandler
{
  public static void register(TestGroups groups)
  {
    groups.media.add(JavaBuiltIn.mediaTransformer,
        new SemiManualSerializer("json/json.simple/manual"),
            new SerFeatures(
                    SerFormat.JSON,
                    SerGraph.FLAT_TREE,
                    SerClass.MANUAL_OPT,
                    ""
            )
    );
  }

  static class SemiManualSerializer extends Serializer<MediaContent>
  {
    private final String name;
    private final JSONParser parser;
    private final MediaContentTransformer transformer;

    public SemiManualSerializer(String name)
    {
      this.name = name;
      this.parser = new JSONParser();
      this.transformer = new MediaContentTransformer();
    }

    public String getName()
    {
      return name;
    }

    public MediaContent deserialize(byte[] array) throws Exception
    {
      String mediaContentJsonInput = new String(array, "UTF-8");
      return readMediaContent(parser, transformer, mediaContentJsonInput);
    }

    public byte[] serialize(MediaContent mediaContent) throws Exception
    {
      StringWriter writer = new StringWriter();
      writeMediaContent(writer, mediaContent);
      writer.flush();
      return writer.toString().getBytes("UTF-8");
    }

    private static MediaContent readMediaContent(JSONParser parser, MediaContentTransformer transformer, String mediaContentJsonInput) throws Exception
    {
      parser.parse(mediaContentJsonInput, transformer);
      return transformer.getResult();
    }

    @SuppressWarnings("unchecked")
    private static JSONObject createJsonObject(Media media)
    {
      JSONObject jsonObject = new JSONObject();
      if (media.hasBitrate)
      {
        jsonObject.put("bitrate", media.bitrate);
      }
      jsonObject.put("copyright", media.copyright);
      jsonObject.put("duration", media.duration);
      jsonObject.put("format", media.format);
      jsonObject.put("height", media.height);
      int size = media.persons.size();
      JSONArray personsJsonArray = new JSONArray();
      for (int i = 0; i < size; i++)
      {
        personsJsonArray.add(media.persons.get(i));
      }
      jsonObject.put("persons", personsJsonArray);
      jsonObject.put("player", media.player.name());
      jsonObject.put("size", media.size);
      jsonObject.put("title", media.title);
      jsonObject.put("uri", media.uri);
      jsonObject.put("width", media.width);
      return jsonObject;
    }

    @SuppressWarnings("unchecked")
    private static JSONObject createJsonObject(Image image)
    {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("height", image.height);
      jsonObject.put("size", image.size.name());
      jsonObject.put("title", image.title);
      jsonObject.put("uri", image.uri);
      jsonObject.put("width", image.width);
      return jsonObject;
    }

    @SuppressWarnings("unchecked")
    private static void writeMediaContent(Writer writer, MediaContent mediaContent) throws Exception
    {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("media", createJsonObject(mediaContent.media));
      jsonObject.put("images", createJsonArray(mediaContent.images));
      jsonObject.writeJSONString(writer);
    }

    @SuppressWarnings("unchecked")
    private static JSONArray createJsonArray(List<Image> images)
    {
      JSONArray jsonArray = new JSONArray();
      for (Image image : images)
      {
        jsonArray.add(createJsonObject(image));
      }
      return jsonArray;
    }
    
    private static class MediaContentTransformer implements ContentHandler
    {
      private static enum State
      {
        INIT, READING_MEDIA_CONTENT, FINAL, 
        READING_MEDIA, 
          READING_MEDIA_URI, READING_MEDIA_TITLE, READING_MEDIA_WIDTH, READING_MEDIA_HEIGHT, READING_MEDIA_FORMAT, 
          READING_MEDIA_DURATION, READING_MEDIA_SIZE, READING_MEDIA_BITRATE, 
          READING_MEDIA_PERSONS, READING_MEDIA_PERSON, 
          READING_MEDIA_PLAYER, READING_MEDIA_COPYRIGHT, 
        FINISHED_MEDIA, 
        READING_IMAGES, FINISHED_IMAGES, 
        READING_IMAGE, 
          READING_IMAGE_URI, READING_IMAGE_TITLE, READING_IMAGE_WIDTH, READING_IMAGE_HEIGHT, READING_IMAGE_SIZE, 
        FINISHED_IMAGE
      }
      
      private State state;
      private final MediaContent mediaContent = new MediaContent();
      private Image image;

      public MediaContent getResult()
      {
        return mediaContent;
      }

      @Override
      public void startJSON() throws ParseException, IOException
      {
        state = State.INIT;
      }

      @Override
      public void endJSON() throws ParseException, IOException
      {
        throw new RuntimeException("unexpected state transition");
      }

      @Override
      public boolean startObject() throws ParseException, IOException
      {
        switch (state)
        {
          case INIT: state = State.READING_MEDIA_CONTENT; return true;
          case READING_MEDIA: mediaContent.media = new Media(); return true;
          case READING_IMAGE: 
          case FINISHED_IMAGE: image = new Image(); mediaContent.images.add(image); state = State.READING_IMAGE; return true;
          default: throw new RuntimeException("unexpected state transition");
        }
      }

      @Override
      public boolean endObject() throws ParseException, IOException
      {
        switch (state)
        {
          case READING_MEDIA: state = State.FINISHED_MEDIA; return true;
          case READING_IMAGE: image = null; state = State.FINISHED_IMAGE; return true;
          case FINISHED_MEDIA: 
          case FINISHED_IMAGES: state = State.FINAL; return false;
          default: throw new RuntimeException("unexpected state transition");
        }
      }

      @Override
      public boolean startObjectEntry(String key) throws ParseException, IOException
      {
        switch (state)
        {
          case READING_MEDIA_CONTENT: 
          case FINISHED_IMAGES: 
            if ("media".equals(key)) {state = State.READING_MEDIA; return true;}
          case FINISHED_MEDIA: 
            if ("images".equals(key)) {state = State.READING_IMAGES; return true;}
            throw new RuntimeException("unexpected state transition");
          case READING_MEDIA: 
            if ("uri".equals(key)) {state = State.READING_MEDIA_URI; return true;}
            if ("title".equals(key)) {state = State.READING_MEDIA_TITLE; return true;}
            if ("width".equals(key)) {state = State.READING_MEDIA_WIDTH; return true;}
            if ("height".equals(key)) {state = State.READING_MEDIA_HEIGHT; return true;}
            if ("format".equals(key)) {state = State.READING_MEDIA_FORMAT; return true;}
            if ("duration".equals(key)) {state = State.READING_MEDIA_DURATION; return true;}
            if ("size".equals(key)) {state = State.READING_MEDIA_SIZE; return true;}
            if ("bitrate".equals(key)) {state = State.READING_MEDIA_BITRATE; return true;}
            if ("persons".equals(key)) {state = State.READING_MEDIA_PERSONS; return true;}
            if ("player".equals(key)) {state = State.READING_MEDIA_PLAYER; return true;}
            if ("copyright".equals(key)) {state = State.READING_MEDIA_COPYRIGHT; return true;}
            throw new RuntimeException("unexpected state transition");
          case READING_IMAGE: 
            if ("uri".equals(key)) {state = State.READING_IMAGE_URI; return true;}
            if ("title".equals(key)) {state = State.READING_IMAGE_TITLE; return true;}
            if ("width".equals(key)) {state = State.READING_IMAGE_WIDTH; return true;}
            if ("height".equals(key)) {state = State.READING_IMAGE_HEIGHT; return true;}
            if ("size".equals(key)) {state = State.READING_IMAGE_SIZE; return true;}
          default: throw new RuntimeException("unexpected state transition");
        }
      }

      @Override
      public boolean endObjectEntry() throws ParseException, IOException
      {
        switch (state)
        {
          case READING_MEDIA: return true;
          case FINISHED_MEDIA: return true;
          case READING_IMAGE: return true;
          case FINISHED_IMAGES: return true;
          default: throw new RuntimeException("unexpected state transition");
        }
      }

      @Override
      public boolean startArray() throws ParseException, IOException
      {
        switch (state)
        {
          case READING_MEDIA_PERSONS: mediaContent.media.persons = new ArrayList<String>(); state = State.READING_MEDIA_PERSON; return true;
          case READING_IMAGES: mediaContent.images = new ArrayList<Image>(); state = State.READING_IMAGE; return true;
          default: throw new RuntimeException("unexpected state transition");
        }
      }

      @Override
      public boolean endArray() throws ParseException, IOException
      {
        switch (state)
        {
          case READING_MEDIA_PERSON: state = State.READING_MEDIA; return true;
          case FINISHED_IMAGE: state = State.FINISHED_IMAGES; return true;
          default: throw new RuntimeException("unexpected state transition");
        }
      }

      @Override
      public boolean primitive(Object value) throws ParseException, IOException
      {
        switch (state)
        {
          case READING_MEDIA_URI: mediaContent.media.uri = readString(value); state = State.READING_MEDIA; return true;
          case READING_MEDIA_TITLE: mediaContent.media.title = readString(value); state = State.READING_MEDIA; return true;
          case READING_MEDIA_WIDTH: mediaContent.media.width = readInt(value); state = State.READING_MEDIA; return true;
          case READING_MEDIA_HEIGHT: mediaContent.media.height = readInt(value); state = State.READING_MEDIA; return true;
          case READING_MEDIA_FORMAT: mediaContent.media.format = readString(value); state = State.READING_MEDIA; return true;
          case READING_MEDIA_DURATION: mediaContent.media.duration = readLong(value); state = State.READING_MEDIA; return true;
          case READING_MEDIA_SIZE: mediaContent.media.size = readLong(value); state = State.READING_MEDIA; return true;
          case READING_MEDIA_BITRATE: mediaContent.media.bitrate = readInt(value); mediaContent.media.hasBitrate = value != null; state = State.READING_MEDIA; return true;
          case READING_MEDIA_PERSON: mediaContent.media.persons.add(readString(value)); return true;
          case READING_MEDIA_PLAYER: mediaContent.media.player = Media.Player.valueOf(readString(value)); state = State.READING_MEDIA; return true;
          case READING_MEDIA_COPYRIGHT: mediaContent.media.copyright = readString(value); state = State.READING_MEDIA; return true;
          case READING_IMAGE_URI: image.uri = readString(value); state = State.READING_IMAGE; return true;
          case READING_IMAGE_TITLE: image.title = readString(value); state = State.READING_IMAGE; return true;
          case READING_IMAGE_WIDTH: image.width = readInt(value); state = State.READING_IMAGE; return true;
          case READING_IMAGE_HEIGHT: image.height = readInt(value); state = State.READING_IMAGE; return true;
          case READING_IMAGE_SIZE: image.size = Image.Size.valueOf(readString(value)); state = State.READING_IMAGE; return true;
          default: throw new RuntimeException("unexpected state transition");
        }
      }
      
      private static int readInt(Object value)
      {
        if (value == null) return 0;
        return ((Long) value).intValue();
      }
      
      private static long readLong(Object value)
      {
        if (value == null) return 0;
        return ((Long) value).longValue();
      }
      
      private static String readString(Object value)
      {
        if (value == null) return null;
        return (String) value;
      }
    }
  }
}
