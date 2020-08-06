package serializers.scala

import sbinary.DefaultProtocol
import sbinary.DefaultProtocol._

object MyProtocol extends DefaultProtocol{
//  import sbinary.DefaultProtocol._

//  implicit val ImageFormat : Format[Image] = asProduct5(Image)(Image.unapply(_).get)
  implicit object ImageFormat extends Format[Image]{
    def reads(in : Input) = {
      Image(
        read[String](in),
        read[String](in),
        read[Int](in),
        read[Int](in),
        read[Size.Value](in)
      )
    }

    def writes(out : Output, value : Image) = {
      write[String](out, value.uri)
      write[String](out, value.title)
      write[Int](out, value.width)
      write[Int](out, value.height)
      write[Size.Value](out, value.size)
    }
  }

  implicit object MediaFormat extends Format[Media]{
    def reads(in : Input) = {
      val back = new Media(
        read[String](in),
        read[String](in),
        read[Int](in),
        read[Int](in),
        read[String](in),
        read[Long](in),
        read[Long](in),
        read[Int](in),
        read[Option[String]](in),
        read[Player.Value](in)
      )
      read[List[String]](in).foreach(p => back.addPerson(p))
      back
    }

    def writes(out : Output, value : Media) = {
      write[String](out, value.uri)
      write[String](out, value.title)
      write[Int](out, value.width)
      write[Int](out, value.height)
      write[String](out, value.format)
      write[Long](out, value.duration)
      write[Long](out, value.size)
      write[Int](out, value.bitrate)
      write[Option[String]](out, value.copyright)
      write[Player.Value](out, value.player)
      write[List[String]](out, value.persons)
    }
  }

//  implicit object PlayerFormat extends Format[Player.Value] {
//    def reads(in : Input) = Player.Value(read[Int](in))
//    def writed(out : Output, value : Player.Value) = write[Int](out, value)
//  }

  implicit val PlayerFormat = enumerationFormat[Player.Value](Player)
  implicit val SizeFormat = enumerationFormat[Size.Value](Size)

  implicit object MediaContentFormat extends Format[MediaContent]{
    def reads(in : Input) = {
      val back = new MediaContent(read[Media](in))
      read[List[Image]](in).foreach(i => back.addImage(i))
      back
    }

    def writes(out : Output, value : MediaContent) = {
      write[Media](out, value.media)
      write[List[Image]](out, value.images)
    }
  }

}


object SbinarySerializerSupport {
  import MyProtocol._
  def deserialize(array : Array[Byte]) : MediaContent = fromByteArray[MediaContent](array)
  def serialize(content : MediaContent) : Array[Byte] = toByteArray(content)
}