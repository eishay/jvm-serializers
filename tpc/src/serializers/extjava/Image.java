package serializers.extjava;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


public class Image implements Externalizable {

   private static final long serialVersionUID = 1L;


   public enum Size {
      SMALL, LARGE
   }


   private String _uri;
   private String _title;
   private int    _width;
   private int    _height;
   private Size   _size;


   public Image() {}

   public Image( int height, String title, String uri, int width, Size size ) {
      super();
      _height = height;
      _title = title;
      _uri = uri;
      _width = width;
      _size = size;
   }

   public String getUri() {
      return _uri;
   }

   public void setUri( String uri ) {
      _uri = uri;
   }

   public String getTitle() {
      return _title;
   }

   public void setTitle( String title ) {
      _title = title;
   }

   public int getWidth() {
      return _width;
   }

   public void setWidth( int width ) {
      _width = width;
   }

   public int getHeight() {
      return _height;
   }

   public void setHeight( int height ) {
      _height = height;
   }

   public Size getSize() {
      return _size;
   }

   public void setSize( Size size ) {
      this._size = size;
   }

   public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
      _title = ExternalizationHelper.readString(in);
      _uri = ExternalizationHelper.readString(in);
      _width = in.readInt();
      _height = in.readInt();
      _size = Size.values()[in.readByte()];
   }

   public void writeExternal( ObjectOutput out ) throws IOException {
      ExternalizationHelper.writeString(out, _title);
      ExternalizationHelper.writeString(out, _uri);
      out.writeInt(_width);
      out.writeInt(_height);
      out.writeByte(_size.ordinal());
   }

   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + _height;
      result = prime * result + ((_size == null) ? 0 : _size.hashCode());
      result = prime * result + ((_title == null) ? 0 : _title.hashCode());
      result = prime * result + ((_uri == null) ? 0 : _uri.hashCode());
      result = prime * result + _width;
      return result;
   }

   public boolean equals( Object obj ) {
      if ( this == obj ) return true;
      if ( obj == null ) return false;
      if ( getClass() != obj.getClass() ) return false;
      Image other = (Image)obj;
      if ( _height != other._height ) return false;
      if ( _size == null ) {
         if ( other._size != null ) return false;
      } else if ( !_size.equals(other._size) ) return false;
      if ( _title == null ) {
         if ( other._title != null ) return false;
      } else if ( !_title.equals(other._title) ) return false;
      if ( _uri == null ) {
         if ( other._uri != null ) return false;
      } else if ( !_uri.equals(other._uri) ) return false;
      if ( _width != other._width ) return false;
      return true;
   }
}
