namespace java serializers.thrift.media

typedef i32 int
typedef i64 long

enum Size {
  SMALL = 0,
  LARGE = 1,
}

enum Player {
  JAVA = 0,
  FLASH = 1,
}

/**
 * Some comment...
 */
struct Image {
  1: string uri,              //url to the images
  2: optional string title,  
  3: required int width,
  4: required int height,
  5: required Size size,
}

struct Media {
  1: string uri,             //url to the thumbnail
  2: optional string title,
  3: required int width,
  4: required int height,
  5: required string format,
  6: required long duration,
  7: required long size,
  8: optional int bitrate,
  9: required list<string> person,
  10: required Player player,
  11: optional string copyright,
}

struct MediaContent {
  1: required list<Image> image,
  2: required Media media,
}
