package serializers.java;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.twolattes.json.Entity;
import com.twolattes.json.Value;

@Entity
public class MediaContent  implements Serializable
{
  private static final long serialVersionUID = 1L;

  // Note: use FIELD_NAME_IMAGES
  private @Value(name = "im") List<Image> _images;
  // Note: use FIELD_NAME_MEDIA
  private @Value(name = "md") Media _media;
  
    public MediaContent() { }

  public MediaContent (Media media)
  {
    _media = media;
  }

  public List<Image> getImages() { return _images; }
  public void setImages(List<Image> i) { _images = i; }

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

    public void setMedia(Media m) { _media = m; }

    public int hashCode() {
       final int prime = 31;
       int result = 1;
       result = prime * result + ((_images == null) ? 0 : _images.hashCode());
       result = prime * result + ((_media == null) ? 0 : _media.hashCode());
       return result;
    }

    public boolean equals( Object obj ) {
       if ( this == obj ) return true;
       if ( obj == null ) return false;
       if ( getClass() != obj.getClass() ) return false;
       MediaContent other = (MediaContent)obj;
       if ( _images == null ) {
          if ( other._images != null ) return false;
       } else if ( !_images.equals(other._images) ) return false;
       if ( _media == null ) {
          if ( other._media != null ) return false;
       } else if ( !_media.equals(other._media) ) return false;
       return true;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[MediaContent: ");
        sb.append("media=").append(_media);
        sb.append(", images=").append(_images);
        sb.append("]");
        return sb.toString();
    }
}
