package serializers.avro.specific;

@SuppressWarnings("all")
public class Media extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = org.apache.avro.Schema.parse("{\"type\":\"record\",\"name\":\"Media\",\"namespace\":\"serializers.avro.specific\",\"fields\":[{\"name\":\"uri\",\"type\":\"string\"},{\"name\":\"title\",\"type\":\"string\"},{\"name\":\"width\",\"type\":\"int\"},{\"name\":\"height\",\"type\":\"int\"},{\"name\":\"format\",\"type\":\"string\"},{\"name\":\"duration\",\"type\":\"long\"},{\"name\":\"size\",\"type\":\"long\"},{\"name\":\"bitrate\",\"type\":\"int\"},{\"name\":\"person\",\"type\":{\"type\":\"array\",\"items\":\"string\"}},{\"name\":\"player\",\"type\":\"int\"},{\"name\":\"copyright\",\"type\":\"string\"}]}");
  public org.apache.avro.util.Utf8 uri;
  public org.apache.avro.util.Utf8 title;
  public int width;
  public int height;
  public org.apache.avro.util.Utf8 format;
  public long duration;
  public long size;
  public int bitrate;
  public org.apache.avro.generic.GenericArray<org.apache.avro.util.Utf8> person;
  public int player;
  public org.apache.avro.util.Utf8 copyright;
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  public java.lang.Object get(int field$) {
    switch (field$) {
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
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: uri = (org.apache.avro.util.Utf8)value$; break;
    case 1: title = (org.apache.avro.util.Utf8)value$; break;
    case 2: width = (java.lang.Integer)value$; break;
    case 3: height = (java.lang.Integer)value$; break;
    case 4: format = (org.apache.avro.util.Utf8)value$; break;
    case 5: duration = (java.lang.Long)value$; break;
    case 6: size = (java.lang.Long)value$; break;
    case 7: bitrate = (java.lang.Integer)value$; break;
    case 8: person = (org.apache.avro.generic.GenericArray<org.apache.avro.util.Utf8>)value$; break;
    case 9: player = (java.lang.Integer)value$; break;
    case 10: copyright = (org.apache.avro.util.Utf8)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
}
