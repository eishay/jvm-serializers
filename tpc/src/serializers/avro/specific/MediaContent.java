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

	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + ((media == null) ? 0 : media.hashCode());
		return result;
	}

	public boolean equals (Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		MediaContent other = (MediaContent)obj;
		if (image == null) {
			if (other.image != null) return false;
		} else if (!image.equals(other.image)) return false;
		if (media == null) {
			if (other.media != null) return false;
		} else if (!media.equals(other.media)) return false;
		return true;
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

	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((height == null) ? 0 : height.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		result = prime * result + ((width == null) ? 0 : width.hashCode());
		return result;
	}

	public boolean equals (Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Image other = (Image)obj;
		if (height == null) {
			if (other.height != null) return false;
		} else if (!height.equals(other.height)) return false;
		if (size == null) {
			if (other.size != null) return false;
		} else if (!size.equals(other.size)) return false;
		if (title == null) {
			if (other.title != null) return false;
		} else if (!title.equals(other.title)) return false;
		if (uri == null) {
			if (other.uri != null) return false;
		} else if (!uri.equals(other.uri)) return false;
		if (width == null) {
			if (other.width != null) return false;
		} else if (!width.equals(other.width)) return false;
		return true;
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

	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bitrate == null) ? 0 : bitrate.hashCode());
		result = prime * result + ((copyright == null) ? 0 : copyright.hashCode());
		result = prime * result + ((duration == null) ? 0 : duration.hashCode());
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		result = prime * result + ((height == null) ? 0 : height.hashCode());
		result = prime * result + ((person == null) ? 0 : person.hashCode());
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		result = prime * result + ((width == null) ? 0 : width.hashCode());
		return result;
	}

	public boolean equals (Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Media other = (Media)obj;
		if (bitrate == null) {
			if (other.bitrate != null) return false;
		} else if (!bitrate.equals(other.bitrate)) return false;
		if (copyright == null) {
			if (other.copyright != null) return false;
		} else if (!copyright.equals(other.copyright)) return false;
		if (duration == null) {
			if (other.duration != null) return false;
		} else if (!duration.equals(other.duration)) return false;
		if (format == null) {
			if (other.format != null) return false;
		} else if (!format.equals(other.format)) return false;
		if (height == null) {
			if (other.height != null) return false;
		} else if (!height.equals(other.height)) return false;
		if (person == null) {
			if (other.person != null) return false;
		} else if (!person.equals(other.person)) return false;
		if (player == null) {
			if (other.player != null) return false;
		} else if (!player.equals(other.player)) return false;
		if (size == null) {
			if (other.size != null) return false;
		} else if (!size.equals(other.size)) return false;
		if (title == null) {
			if (other.title != null) return false;
		} else if (!title.equals(other.title)) return false;
		if (uri == null) {
			if (other.uri != null) return false;
		} else if (!uri.equals(other.uri)) return false;
		if (width == null) {
			if (other.width != null) return false;
		} else if (!width.equals(other.width)) return false;
		return true;
	}
  }
