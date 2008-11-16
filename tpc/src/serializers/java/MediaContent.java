package serializers.java;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MediaContent  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private List<Image> _images;
  private Media _media;
  
  public void addImage (Image image)
  {
    if(_images == null)
    {
      _images = new ArrayList<Image>();
    }
    _images.add(image);
  }
  
  public List<Image> getImage (int i)
  {
    return _images;
  }
  
  public void setMedia (Media media)
  {
    _media = media;
  }
  
  public Media getMedia ()
  {
    return _media;
  }

}
