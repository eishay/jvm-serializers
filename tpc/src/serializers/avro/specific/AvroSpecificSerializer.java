package serializers.avro.specific;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.util.Utf8;

import serializers.ObjectSerializer;

public class AvroSpecificSerializer implements ObjectSerializer<MediaContent> 

{
  private static final Schema SCHEMA = new MediaContent().getSchema();

  private static final SpecificDatumReader READER = 
    new SpecificDatumReader(SCHEMA);

  private static final SpecificDatumWriter WRITER =
    new SpecificDatumWriter(SCHEMA);

  public String getName() {
    return "avro-specific";
  }

  public MediaContent create() throws Exception {
    Media media = new Media();
    media.uri = new Utf8("http://javaone.com/keynote.mpg");
    media.format = new Utf8("video/mpg4");
    media.title = new Utf8("Javaone Keynote");
    media.duration = 1234567L;
    media.bitrate = 0;
    media.person = new GenericData.Array<Utf8>(2, null);
    media.person.add(new Utf8("Bill Gates"));
    media.person.add(new Utf8("Steve Jobs"));
    media.player = 0;
    media.height = 0;
    media.width = 0;
    media.size = 123L;
    media.copyright = new Utf8();

    Image image1 = new Image();
    image1.uri = new Utf8("http://javaone.com/keynote_large.jpg");
    image1.width = 0;
    image1.height = 0;
    image1.size = 2;
    image1.title = new Utf8("Javaone Keynote");

    Image image2 = new Image();
    image2.uri = new Utf8("http://javaone.com/keynote_thumbnail.jpg");
    image2.width = 0;
    image2.height = 0;
    image2.size = 1;
    image2.title = new Utf8("Javaone Keynote");

    MediaContent content = new MediaContent();
    content.media = media;
    content.image = new GenericData.Array<Image>(2, null);
    content.image.add(image1);
    content.image.add(image2);
    return content;
  }

  public MediaContent deserialize(byte[] array) throws Exception {
    return (MediaContent) 
      READER.read(null, new BinaryDecoder(new ByteArrayInputStream(array)));
  }

  public byte[] serialize(MediaContent content) throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    WRITER.write(content, new BinaryEncoder(out));
    return out.toByteArray();
  }

}
