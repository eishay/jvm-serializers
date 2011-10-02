package serializers

import _root_.java.util.ArrayList
import _root_.scala.collection.JavaConversions._

import _root_.sbinary.Operations
import _root_.sbinary.{Format, Input, Output, DefaultProtocol}

import _root_.serializers.{scala => sdata}

object Scala
{
	def register(groups: TestGroups)
	{
		groups.media.add(MediaTransformer, new JavaBuiltIn.GenericSerializer[sdata.media.MediaContent]("scala/java-built-in"))
		groups.media.add(MediaTransformer, MediaSerializer)
	}

	// --------------------------------------------------------------------------
	// MediaContent

	object MediaTransformer extends data.media.MediaTransformer[sdata.media.MediaContent]
	{
		import sdata.media._

		def forward(mc: data.media.MediaContent) : MediaContent =
			new MediaContent(forwardMedia(mc.media), List.concat(mc.images.map(forwardImage)))

		def forwardMedia(m: data.media.Media) : Media =
			new Media(
				m.uri,
				nullToOption(m.title),
				m.width,
				m.height,
				m.format,
				m.duration,
				m.size,
				if (m.hasBitrate) Some(m.bitrate) else None,
				List.concat(m.persons),
				forwardPlayer(m.player),
				nullToOption(m.copyright))

		def forwardPlayer(p: data.media.Media.Player) : Media.Player = p match {
			case data.media.Media.Player.JAVA => Media.Player.Java
			case data.media.Media.Player.FLASH => Media.Player.Flash
		}

		def forwardImage(im: data.media.Image) : Image =
			new Image(
				im.uri,
				nullToOption(im.title),
				im.width,
				im.height,
				forwardSize(im.size))

		def forwardSize(s: data.media.Image.Size) = s match {
			case data.media.Image.Size.SMALL => Image.Size.Small
			case data.media.Image.Size.LARGE => Image.Size.Large
		}

		def reverse(mc: MediaContent) : data.media.MediaContent =
			new data.media.MediaContent(reverseMedia(mc.media), new ArrayList(mc.images.map(reverseImage)))

		def reverseMedia(m: Media) : data.media.Media =
			new data.media.Media(
				m.uri,
				m.title.getOrElse(null),
				m.width,
				m.height,
				m.format,
				m.duration,
				m.size,
				m.bitrate.getOrElse(0),
				m.bitrate.isDefined,
				new ArrayList(m.persons),
				reversePlayer(m.player),
				m.copyright.getOrElse(null))

		def reversePlayer(s: Media.Player) : data.media.Media.Player = s match {
			case Media.Player.Java => data.media.Media.Player.JAVA
			case Media.Player.Flash => data.media.Media.Player.FLASH
		}

		def reverseImage(im: Image) : data.media.Image =
			new data.media.Image(
				im.uri,
				im.title.getOrElse(null),
				im.width,
				im.height,
				reverseSize(im.size))

		def reverseSize(s: Image.Size) : data.media.Image.Size = s match {
			case Image.Size.Small => data.media.Image.Size.SMALL
			case Image.Size.Large => data.media.Image.Size.LARGE
		}

		def shallowReverse(mc: MediaContent) : data.media.MediaContent =
			new data.media.MediaContent(reverseMedia(mc.media), new ArrayList())

	}

	def nullToOption(s: String) =
		if (s == null) None else Some(s)

	// -------------------------------------------------------
	// SBinary Serializers

	object MediaSerializer extends Serializer[sdata.media.MediaContent]
	{
		import sdata.media._

		def getName = "scala/sbinary"
		def serialize(content: MediaContent) = Operations.toByteArray[MediaContent](content)(MediaProtocol.MediaContentFormat)
		def deserialize(array: Array[Byte]) = Operations.fromByteArray[MediaContent](array)(MediaProtocol.MediaContentFormat)
	}

	object MediaProtocol extends DefaultProtocol
	{
		import sdata.media._
		import Operations.{read, write}

		implicit object MediaContentFormat extends Format[MediaContent]
		{
			def reads(in : Input) =
				new MediaContent(
					read[Media](in)(MediaFormat),
					read[List[Image]](in)(listFormat(ImageFormat)))

			def writes(out : Output, value : MediaContent) = {
				write[Media](out, value.media)(MediaFormat)
				write[List[Image]](out, value.images)(listFormat(ImageFormat))
			}
		}

		implicit object MediaFormat extends Format[Media]
		{
			def reads(in : Input) =
				new Media(
					read[String](in),
					read[Option[String]](in),
					read[Int](in),
					read[Int](in),
					read[String](in),
					read[Long](in),
					read[Long](in),
					read[Option[Int]](in),
					read[List[String]](in),
					read[Media.Player](in)(PlayerFormat),
					read[Option[String]](in))

			def writes(out : Output, value : Media) = {
				write[String](out, value.uri)
				write[Option[String]](out, value.title)
				write[Int](out, value.width)
				write[Int](out, value.height)
				write[String](out, value.format)
				write[Long](out, value.duration)
				write[Long](out, value.size)
				write[Option[Int]](out, value.bitrate)
				write[List[String]](out, value.persons)
				write[Media.Player](out, value.player)(PlayerFormat)
				write[Option[String]](out, value.copyright)
			}
		}

		implicit object ImageFormat extends Format[Image]
		{
			def reads(in : Input) =
				Image(
					read[String](in),
					read[Option[String]](in),
					read[Int](in),
					read[Int](in),
					read[Image.Size](in)(SizeFormat))

			def writes(out : Output, value : Image) {
				write[String](out, value.uri)
				write[Option[String]](out, value.title)
				write[Int](out, value.width)
				write[Int](out, value.height)
				write[Image.Size](out, value.size)(SizeFormat)
			}
		}

		implicit object PlayerFormat extends Format[Media.Player]
		{
			def reads(in : Input) = read[Byte](in) match {
				case 0 => Media.Player.Java
				case 1 => Media.Player.Flash
			}

			def writes(out : Output, value : Media.Player) {
				write[Byte](out, value match {
					case Media.Player.Java => 0
					case Media.Player.Flash => 1
				})
			}
		}

		implicit object SizeFormat extends Format[Image.Size]
		{
			def reads(in : Input) = read[Byte](in) match {
				case 0 => Image.Size.Small
				case 1 => Image.Size.Large
			}

			def writes(out : Output, value : Image.Size) {
				write[Byte](out, value match {
					case Image.Size.Small => 0
					case Image.Size.Large => 1
				})
			}
		}
	}
}
