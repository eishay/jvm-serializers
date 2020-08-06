package serializers.scala.media

case class MediaContent (val media: Media, val images: List[Image]) extends Serializable
