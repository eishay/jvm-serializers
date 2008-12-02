package serializers.scala

import Player._
@serializable
class Media (_uri: String, _title: String,
             _width: Int, _height: Int, _format: String, 
             _duration: Long, _size: Long, 
             _bitrate: Int, 
             _copyright: String, 
             _player: Player.Value){
  
  var _persons: List[String] = Nil 
  def uri() = _uri
  def title() = _title
  def width() = _width
  def height() = _height
  def format() = _format
  def duration() = _duration
  def size() = _size
  def bitrate() = _bitrate
  def copyright() = _copyright
  def persons() = _persons
  def player() = _player
  
  def addPerson(persons: String){
      persons :: _persons
  }  
}
