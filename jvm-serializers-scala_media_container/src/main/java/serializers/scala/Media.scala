package serializers.scala

@serializable
class Media (val uri: String, val title: String,
             val width: Int, val height: Int,  val format: String,
             val duration: Long, val size: Long,
             val bitrate: Int,
             val copyright: Option[String],
             val player: Player.Value){

  def this(uri: String, title: String,
           width: Int, height: Int,  format: String,
           duration: Long, size: Long,
           bitrate: Int,
           player: Player.Value) = this(uri, title, width, height, format, duration, size, bitrate, None, player)

  private var _persons: List[String] = Nil
  def persons = _persons

  def addPerson(persons: String){
      _persons = persons :: _persons
  }
}
