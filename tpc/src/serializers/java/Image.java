package serializers.java;

import java.io.Serializable;

public class Image implements Serializable
{
  private static final long serialVersionUID = 1L;

  public enum Size {SMALL, LARGE}

  private String _uri;
  private String _title;
  private int _width;
  private int _height;
  private Size size;
  
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
    return size;
  }
  public void setSize (Size size)
  {
    this.size = size;
  }
}
