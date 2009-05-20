package serializers.avro.specific;

import java.nio.ByteBuffer;
import java.util.Map;
import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Protocol;
import org.apache.avro.util.Utf8;
import org.apache.avro.ipc.AvroRemoteException;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificRecord;


public class MediaContent implements SpecificRecord {
  private static final Schema _SCHEMA = Schema.parse("{\"type\": \"record\", \"name\": \"MediaContent\", \"namespace\": \"serializers.avro.specific\", \"fields\": [{\"name\": \"image\", \"type\": {\"type\": \"array\", \"items\": {\"type\": \"record\", \"name\": \"Image\", \"fields\": [{\"name\": \"uri\", \"type\": \"string\"}, {\"name\": \"title\", \"type\": \"string\"}, {\"name\": \"width\", \"type\": \"int\"}, {\"name\": \"height\", \"type\": \"int\"}, {\"name\": \"size\", \"type\": \"int\"}]}}}, {\"name\": \"media\", \"type\": {\"type\": \"record\", \"name\": \"Media\", \"fields\": [{\"name\": \"uri\", \"type\": \"string\"}, {\"name\": \"title\", \"type\": \"string\"}, {\"name\": \"width\", \"type\": \"int\"}, {\"name\": \"height\", \"type\": \"int\"}, {\"name\": \"format\", \"type\": \"string\"}, {\"name\": \"duration\", \"type\": \"long\"}, {\"name\": \"size\", \"type\": \"long\"}, {\"name\": \"bitrate\", \"type\": \"int\"}, {\"name\": \"person\", \"type\": {\"type\": \"array\", \"items\": \"string\"}}, {\"name\": \"player\", \"type\": \"int\"}, {\"name\": \"copyright\", \"type\": \"string\"}]}}]}");
  public GenericArray<Image> image;
  public Media media;
  public Schema schema() { return _SCHEMA; }
  public Object get(int _field) {
    switch (_field) {
    case 0: return image;
    case 1: return media;
    default: throw new AvroRuntimeException("Bad index");
    }
  }
  @SuppressWarnings(value="unchecked")
  public void set(int _field, Object _value) {
    switch (_field) {
    case 0: image = (GenericArray<Image>)_value; break;
    case 1: media = (Media)_value; break;
    default: throw new AvroRuntimeException("Bad index");
    }
  }
}

  class Image implements SpecificRecord {
    private static final Schema _SCHEMA = Schema.parse("{\"type\": \"record\", \"name\": \"Image\", \"fields\": [{\"name\": \"uri\", \"type\": \"string\"}, {\"name\": \"title\", \"type\": \"string\"}, {\"name\": \"width\", \"type\": \"int\"}, {\"name\": \"height\", \"type\": \"int\"}, {\"name\": \"size\", \"type\": \"int\"}]}");
    public Utf8 uri;
    public Utf8 title;
    public Integer width;
    public Integer height;
    public Integer size;
    public Schema schema() { return _SCHEMA; }
    public Object get(int _field) {
      switch (_field) {
      case 0: return uri;
      case 1: return title;
      case 2: return width;
      case 3: return height;
      case 4: return size;
      default: throw new AvroRuntimeException("Bad index");
      }
    }
    @SuppressWarnings(value="unchecked")
    public void set(int _field, Object _value) {
      switch (_field) {
      case 0: uri = (Utf8)_value; break;
      case 1: title = (Utf8)_value; break;
      case 2: width = (Integer)_value; break;
      case 3: height = (Integer)_value; break;
      case 4: size = (Integer)_value; break;
      default: throw new AvroRuntimeException("Bad index");
      }
    }
  }

  class Media implements SpecificRecord {
    private static final Schema _SCHEMA = Schema.parse("{\"type\": \"record\", \"name\": \"Media\", \"fields\": [{\"name\": \"uri\", \"type\": \"string\"}, {\"name\": \"title\", \"type\": \"string\"}, {\"name\": \"width\", \"type\": \"int\"}, {\"name\": \"height\", \"type\": \"int\"}, {\"name\": \"format\", \"type\": \"string\"}, {\"name\": \"duration\", \"type\": \"long\"}, {\"name\": \"size\", \"type\": \"long\"}, {\"name\": \"bitrate\", \"type\": \"int\"}, {\"name\": \"person\", \"type\": {\"type\": \"array\", \"items\": \"string\"}}, {\"name\": \"player\", \"type\": \"int\"}, {\"name\": \"copyright\", \"type\": \"string\"}]}");
    public Utf8 uri;
    public Utf8 title;
    public Integer width;
    public Integer height;
    public Utf8 format;
    public Long duration;
    public Long size;
    public Integer bitrate;
    public GenericArray<Utf8> person;
    public Integer player;
    public Utf8 copyright;
    public Schema schema() { return _SCHEMA; }
    public Object get(int _field) {
      switch (_field) {
      case 0: return uri;
      case 1: return title;
      case 2: return width;
      case 3: return height;
      case 4: return format;
      case 5: return duration;
      case 6: return size;
      case 7: return bitrate;
      case 8: return person;
      case 9: return player;
      case 10: return copyright;
      default: throw new AvroRuntimeException("Bad index");
      }
    }
    @SuppressWarnings(value="unchecked")
    public void set(int _field, Object _value) {
      switch (_field) {
      case 0: uri = (Utf8)_value; break;
      case 1: title = (Utf8)_value; break;
      case 2: width = (Integer)_value; break;
      case 3: height = (Integer)_value; break;
      case 4: format = (Utf8)_value; break;
      case 5: duration = (Long)_value; break;
      case 6: size = (Long)_value; break;
      case 7: bitrate = (Integer)_value; break;
      case 8: person = (GenericArray<Utf8>)_value; break;
      case 9: player = (Integer)_value; break;
      case 10: copyright = (Utf8)_value; break;
      default: throw new AvroRuntimeException("Bad index");
      }
    }
  }
