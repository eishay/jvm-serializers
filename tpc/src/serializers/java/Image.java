package serializers.java;

import java.io.Serializable;

import com.twolattes.json.Entity;
import com.twolattes.json.Value;

@Entity
public class Image implements Serializable
{
  private static final long serialVersionUID = 1L;

  public enum Size
  {
    SMALL, LARGE
  }

    // Note: field names must match FIELD_NAME_xxx
  private @Value(name = "ul") String _uri;
  private @Value(name = "tl") String _title;
  private @Value(name = "wd") int _width;
  private @Value(name = "hg") int _height;
  private @Value(name = "sz", ordinal = true) Size _size;

  public Image(){}

  
  public Image (int height, String title, String uri, int width, Size size)
  {
    super();
    _height = height;
    _title = title;
    _uri = uri;
    _width = width;
    _size = size;
  }

  public String getUri ()
  {
    return _uri;
  }

  public void setUri (String uri)
  {
    _uri = uri;
  }

  public String getTitle ()
  {
    return _title;
  }

  public void setTitle (String title)
  {
    _title = title;
  }

  public int getWidth ()
  {
    return _width;
  }

  public void setWidth (int width)
  {
    _width = width;
  }

  public int getHeight ()
  {
    return _height;
  }

  public void setHeight (int height)
  {
    _height = height;
  }

  public Size getSize ()
  {
    return _size;
  }

  public void setSize (Size size)
  {
    this._size = size;
  }

    @Override
    public boolean equals(Object o)
    {
        if (o == this) return true;
        if (o == null) return false;
        if (!(o instanceof Image)) return false;

        Image other = (Image) o;

        if (_width != other._width) return false;
        if (_height != other._height) return false;
        if (!_uri.equals(other._uri)) return false;
        if (!_title.equals(other._title)) return false;
        if (!_size.equals(other._size)) return false;

        return true;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[Image ");
        sb.append("width=").append(_width);
        sb.append(", height=").append(_height);
        sb.append(", uri=").append(_uri);
        sb.append(", title=").append(_title);
        sb.append(", size=").append(_size);
        sb.append("]");
        return sb.toString();
    }
}
