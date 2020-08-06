package serializers.scala

object Runner {
  def main(args: Array[String]){
    println("start")
    var image1 = new Image("a", "b", 1, 2, Size.SMALL)
    var image2 = new Image("a", "b", 1, 2, Size.SMALL)
    var content = new MediaContent(null)
    content.addImage(image1)
    content.addImage(image2)
    
    println("end")
  }
}
