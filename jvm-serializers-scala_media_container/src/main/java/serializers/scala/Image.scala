package serializers.scala

@serializable
case class Image(uri: String, title: String, width: Int, height: Int, size: Size.Value)
