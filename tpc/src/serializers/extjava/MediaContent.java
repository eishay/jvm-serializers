package serializers.extjava;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class MediaContent implements Externalizable
{
  private static final long serialVersionUID = 1L;
  private List<Image> _images;
  private Media _media;

  public MediaContent()
  {
  }

  public MediaContent(Media media)
  {
    _media = media;
  }

  public void addImage(Image image)
  {
    if (_images == null)
    {
      _images = new ArrayList<Image>();
    }
    _images.add(image);
  }

  public Image getImage(int i)
  {
    return _images.get(i);
  }

  public Media getMedia()
  {
    return _media;
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException,
      ClassNotFoundException
  {
    _media = (Media) in.readObject();
    int nbImages = in.readInt();
    _images = new ArrayList<Image>(nbImages);
    for (int i = 0; i < nbImages; i++)
    {
      _images.add((Image) in.readObject());
    }
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException
  {
    out.writeObject(_media);
    if (_images == null)
    {
      out.writeInt(0);
    }
    else
    {
      int nbImages = _images.size();
      out.writeInt(nbImages);
      for (int i = 0; i < nbImages; i++)
      {
        out.writeObject(_images.get(i));
      }
    }
  }

}
