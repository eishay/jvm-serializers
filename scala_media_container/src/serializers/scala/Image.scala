package serializers.scala

import Size._

@serializable
class Image (_uri: String, _title: String, 
             _width: Int, _height: Int, 
             _size: Size.Value){
  def uri() = _uri
  def title() = _title
  def width() = _width
  def height() = _height
  def size() = _size
}
