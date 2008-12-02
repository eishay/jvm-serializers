package serializers.scala
@serializable
class MediaContent (_media: Media){
  var _images: List[Image] = Nil
  def images() = _images
  def media() = _media

  def addImage(image: Image){
      image :: _images
  }
}
