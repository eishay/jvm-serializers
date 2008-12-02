package serializers.java;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Media implements Serializable
{
  private static final long serialVersionUID = 1L;

  public enum Player
  {
    JAVA, FLASH
  }
  private Player _player;
  private String _uri;
  private String _title;
  private int _width;
  private int _height;
  private String _format;
  private long _duration;
  private long _size;
  private int _bitrate;
  private List<String> _persons;
  private String _copyright;

  public Media(){}
  
  public Media (String copyright,
                String format,
                Player player,
                String title,
                String uri,
                long duration,
                long size,
                int height,
                int width,
                int bitrate)
  {
    _copyright = copyright;
    _duration = duration;
    _format = format;
    _height = height;
    _player = player;
    _size = size;
    _title = title;
    _uri = uri;
    _width = width;
    _bitrate = bitrate;
  }

  public Player getPlayer ()
  {
    return _player;
  }

  public void setPlayer (Player player)
  {
    _player = player;
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

  public String getFormat ()
  {
    return _format;
  }

  public void setFormat (String format)
  {
    _format = format;
  }

  public long getDuration ()
  {
    return _duration;
  }

  public void setDuration (long duration)
  {
    _duration = duration;
  }

  public long getSize ()
  {
    return _size;
  }

  public void setSize (long size)
  {
    _size = size;
  }

  public int getBitrate ()
  {
    return _bitrate;
  }

  public void setBitrate (int bitrate)
  {
    this._bitrate = bitrate;
  }

  public List<String> getPersons ()
  {
    return _persons;
  }

  public void addToPerson (String person)
  {
    if (null == _persons)
    {
      _persons = new ArrayList<String>();
    }
    _persons.add(person);
  }

  public String getCopyright ()
  {
    return _copyright;
  }

  public void setCopyright (String copyright)
  {
    _copyright = copyright;
  }

}
