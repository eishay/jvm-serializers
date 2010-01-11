package serializers.extjava;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;


public class MediaContent implements Externalizable {

   private static final long serialVersionUID = 1L;
   private List<Image>       _images;
   private Media             _media;


   public MediaContent() {}

   public MediaContent( Media media ) {
      _media = media;
   }

   public void addImage( Image image ) {
      if ( _images == null ) {
         _images = new ArrayList<Image>();
      }
      _images.add(image);
   }

   public Image getImage( int i ) {
      return _images.get(i);
   }

   public Media getMedia() {
      return _media;
   }

   public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
      _media = null;
      if ( in.readBoolean() ) {
         _media = new Media();
         _media.readExternal(in);
      }
      _images = null;
      int nbImages = in.readInt();
      if (nbImages >= 0) {
         _images = new ArrayList<Image>(nbImages); 
         for ( int i = 0; i < nbImages; i++ ) {
            Image image = new Image();
            image.readExternal(in);
            _images.add(image);
         }
      }
   }

   public void writeExternal( ObjectOutput out ) throws IOException {
      out.writeBoolean(_media != null);
      if ( _media != null ) _media.writeExternal(out);
      if ( _images == null ) {
         out.writeInt(-1);
      } else {
         int nbImages = _images.size();
         out.writeInt(nbImages);
         for ( int i = 0; i < nbImages; i++ ) {
            _images.get(i).writeExternal(out);
         }
      }
   }

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
}
