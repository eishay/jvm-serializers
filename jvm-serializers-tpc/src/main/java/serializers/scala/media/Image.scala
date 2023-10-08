package serializers.scala.media

case class Image(
	uri: String,
	title: Option[String],
	width: Int,
	height: Int,
	size: Image.Size) extends Serializable

object Image {
	sealed abstract class Size
	object Size {
		case object Small extends Size
		case object Large extends Size
	}
}
