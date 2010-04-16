package serializers.avro.specific;

@SuppressWarnings("all")
public class MediaContent extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = org.apache.avro.Schema.parse("{\"type\":\"record\",\"name\":\"MediaContent\",\"namespace\":\"serializers.avro.specific\",\"fields\":[{\"name\":\"image\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"Image\",\"fields\":[{\"name\":\"uri\",\"type\":\"string\"},{\"name\":\"title\",\"type\":\"string\"},{\"name\":\"width\",\"type\":\"int\"},{\"name\":\"height\",\"type\":\"int\"},{\"name\":\"size\",\"type\":\"int\"}]}}},{\"name\":\"media\",\"type\":{\"type\":\"record\",\"name\":\"Media\",\"fields\":[{\"name\":\"uri\",\"type\":\"string\"},{\"name\":\"title\",\"type\":\"string\"},{\"name\":\"width\",\"type\":\"int\"},{\"name\":\"height\",\"type\":\"int\"},{\"name\":\"format\",\"type\":\"string\"},{\"name\":\"duration\",\"type\":\"long\"},{\"name\":\"size\",\"type\":\"long\"},{\"name\":\"bitrate\",\"type\":\"int\"},{\"name\":\"person\",\"type\":{\"type\":\"array\",\"items\":\"string\"}},{\"name\":\"player\",\"type\":\"int\"},{\"name\":\"copyright\",\"type\":\"string\"}]}}]}");
  public org.apache.avro.generic.GenericArray<serializers.avro.specific.Image> image;
  public serializers.avro.specific.Media media;
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return image;
    case 1: return media;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: image = (org.apache.avro.generic.GenericArray<serializers.avro.specific.Image>)value$; break;
    case 1: media = (serializers.avro.specific.Media)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
}
