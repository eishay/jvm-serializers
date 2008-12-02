package serializers.java;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MediaContent  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private List<Image> _images;
  private final Media _media;
  
  public MediaContent (Media media)
  {
    _media = media;
  }

  public void addImage (Image image)
  {
    if(_images == null)
    {
      _images = new ArrayList<Image>();
    }
    _images.add(image);
  }
  
  public Image getImage (int i)
  {
    return _images.get(i);
  }
  
  public Media getMedia ()
  {
    return _media;
  }

}
