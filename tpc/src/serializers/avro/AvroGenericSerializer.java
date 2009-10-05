package serializers.avro;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.ValueReader;
import org.apache.avro.io.ValueWriter;
import org.apache.avro.util.Utf8;

import serializers.ObjectSerializer;

public class AvroGenericSerializer  implements ObjectSerializer<GenericRecord>
{
  private static final Schema MEDIA_SCHEMA = Schema.parse(
      "{\"type\": \"record\", \"name\": \"Media\", \"fields\": [{\"name\": " +
      "\"uri\", \"type\": \"string\"}, {\"name\": \"title\", \"type\": " +
      "\"string\"}, {\"name\": \"width\", \"type\": \"int\"}, {\"name\": " +
      "\"height\", \"type\": \"int\"}, {\"name\": \"format\", \"type\": " +
      "\"string\"}, {\"name\": \"duration\", \"type\": \"long\"}, " +
      "{\"name\": \"size\", \"type\": \"long\"}, {\"name\": \"bitrate\", " +
      "\"type\": \"int\"}, {\"name\": \"person\", \"type\": {\"type\": " +
      "\"array\", \"items\": \"string\"}}, {\"name\": \"player\", " +
      "\"type\": \"int\"}, {\"name\": \"copyright\", \"type\": \"string\"}]}");

  private static final Schema IMAGE_SCHEMA = Schema.parse(
      "{\"type\": \"record\", \"name\": \"Image\", \"fields\": [{\"name\": " +
      "\"uri\", \"type\": \"string\"}, {\"name\": \"title\", \"type\": " +
      "\"string\"}, {\"name\": \"width\", \"type\": \"int\"}, {\"name\": " +
      "\"height\", \"type\": \"int\"}, {\"name\": \"size\", \"type\": " +
      "\"int\"}]}");

  private static final Schema MEDIA_CONTENT_SCHEMA = Schema.parse(
      "{\"type\": \"record\", \"name\": \"MediaContent\", \"fields\": " +
      "[{\"name\": \"image\", \"type\": {\"type\": \"array\", \"items\": " +
      IMAGE_SCHEMA +
      "}}, {\"name\": \"media\", \"type\": " +
      MEDIA_SCHEMA +
      "}]}");

  private static final GenericDatumWriter<GenericRecord> WRITER = 
    new GenericDatumWriter<GenericRecord>(MEDIA_CONTENT_SCHEMA);

  private static final GenericDatumReader<GenericRecord> READER = 
    new GenericDatumReader<GenericRecord>(MEDIA_CONTENT_SCHEMA);

  public String getName() {
    return "avro-generic";
  }

  public GenericRecord create() throws Exception {
    GenericRecord media = new GenericData.Record(MEDIA_SCHEMA);
    media.put("uri", new Utf8("http://javaone.com/keynote.mpg"));
    media.put("format", new Utf8("video/mpg4"));
    media.put("title", new Utf8("Javaone Keynote"));
    media.put("duration", 1234567L);
    media.put("bitrate", 0);
    GenericData.Array<Utf8> person =  new GenericData.Array<Utf8>(2);
    person.add(new Utf8("Bill Gates"));
    person.add(new Utf8("Steve Jobs"));
    media.put("person", person);
    media.put("player", 1);
    media.put("height", 0);
    media.put("width", 0);
    media.put("size", 123L);
    media.put("copyright", new Utf8());

    GenericRecord image1 = new GenericData.Record(IMAGE_SCHEMA);
    image1.put("uri", new Utf8("http://javaone.com/keynote_large.jpg"));
    image1.put("width", 0);
    image1.put("height", 0);
    image1.put("size", 2);
    image1.put("title", new Utf8("Javaone Keynote"));

    GenericRecord image2 = new GenericData.Record(IMAGE_SCHEMA);
    image2.put("uri", new Utf8("http://javaone.com/keynote_thumbnail.jpg"));
    image2.put("width", 0);
    image2.put("height", 0);
    image2.put("size", 1);
    image2.put("title", new Utf8("Javaone Keynote"));
    
    GenericData.Array<GenericRecord> images = 
      new GenericData.Array<GenericRecord>(2);
    images.add(image1);
    images.add(image2);
    
    GenericRecord content = new GenericData.Record(MEDIA_CONTENT_SCHEMA);
    content.put("media", media);
    content.put("image", images);
    
    return content;
  }

  public GenericRecord deserialize(byte[] array) throws Exception {
    return READER.read(null, new ValueReader(new ByteArrayInputStream(array)));
  }

  public byte[] serialize(GenericRecord content) throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    WRITER.write(content, new ValueWriter(out));
    return out.toByteArray();
  }

}
