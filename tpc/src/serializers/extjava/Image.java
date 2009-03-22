package serializers.extjava;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Image implements Externalizable
{
  private static final long serialVersionUID = 1L;

  public enum Size
  {
    SMALL, LARGE
  }

  private String _uri;
  private String _title;
  private int _width;
  private int _height;
  private Size _size;

  public Image()
  {
  }

  public Image(int height, String title, String uri, int width, Size size)
  {
    super();
    _height = height;
    _title = title;
    _uri = uri;
    _width = width;
    _size = size;
  }

  public String getUri()
  {
    return _uri;
  }

  public void setUri(String uri)
  {
    _uri = uri;
  }

  public String getTitle()
  {
    return _title;
  }

  public void setTitle(String title)
  {
    _title = title;
  }

  public int getWidth()
  {
    return _width;
  }

  public void setWidth(int width)
  {
    _width = width;
  }

  public int getHeight()
  {
    return _height;
  }

  public void setHeight(int height)
  {
    _height = height;
  }

  public Size getSize()
  {
    return _size;
  }

  public void setSize(Size size)
  {
    this._size = size;
  }

  public void readExternal(ObjectInput in) throws IOException,
      ClassNotFoundException
  {
    _title = (String) in.readObject();
    _width = in.readInt();
    _height = in.readInt();
    _size = Size.values()[in.readByte()];
  }

  public void writeExternal(ObjectOutput out) throws IOException
  {
    out.writeObject(_title);
    out.writeInt(_width);
    out.writeInt(_height);
    out.writeByte(_size.ordinal());
  }
}
