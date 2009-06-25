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

  public int imageCount() { return _images.size(); }
  
  public Image getImage (int i)
  {
    return _images.get(i);
  }
  
  public Media getMedia ()
  {
    return _media;
  }

    @Override
    public boolean equals(Object o)
    {
        if (o == this) return true;
        if (o == null) return false;
        if (!(o instanceof MediaContent)) return false;

        MediaContent other = (MediaContent) o;
        if (!_media.equals(other._media)) {
            return false;
        }
        return _images.equals(other._images);
    }
}
