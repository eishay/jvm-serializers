package serializers.scala.media

case class Media (
	val uri: String,
	val title: Option[String],
	val width: Int,
	val height: Int,
	val format: String,
	val duration: Long,
	val size: Long,
	val bitrate: Option[Int],
	val persons: List[String],
	val player: Media.Player,
	val copyright: Option[String]) extends Serializable

object Media {
	sealed abstract class Player
	object Player {
		case object Java extends Media.Player
		case object Flash extends Media.Player
	}
}
