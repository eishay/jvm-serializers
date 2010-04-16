package serializers.avro.specific;

@SuppressWarnings("all")
public class Image extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = org.apache.avro.Schema.parse("{\"type\":\"record\",\"name\":\"Image\",\"namespace\":\"serializers.avro.specific\",\"fields\":[{\"name\":\"uri\",\"type\":\"string\"},{\"name\":\"title\",\"type\":\"string\"},{\"name\":\"width\",\"type\":\"int\"},{\"name\":\"height\",\"type\":\"int\"},{\"name\":\"size\",\"type\":\"int\"}]}");
  public org.apache.avro.util.Utf8 uri;
  public org.apache.avro.util.Utf8 title;
  public int width;
  public int height;
  public int size;
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return uri;
    case 1: return title;
    case 2: return width;
    case 3: return height;
    case 4: return size;
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
    case 4: size = (java.lang.Integer)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
}
