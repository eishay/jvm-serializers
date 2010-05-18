package serializers.scala.media

@serializable
case class Image(
	uri: String,
	title: Option[String],
	width: Int,
	height: Int,
	size: Image.Size)

object Image {
	sealed abstract class Size
	object Size {
		case object Small extends Size
		case object Large extends Size
	}
}
