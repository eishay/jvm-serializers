package serializers.scala
@serializable
class MediaContent (val media: Media){
  var _images: List[Image] = Nil
  def images = _images

  def addImage(image: Image){
    _images = image :: _images
  }
}
