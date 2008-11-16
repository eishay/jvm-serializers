#!/usr/local/bin/thrift --gen cpp --gen java:hashcode,beans --gen py --php --gen rb --gen perl --erl --xsd -r
#
# News Media
# Before running this file, you will need to have installed the thrift compiler
# into /usr/local/bin.

namespace java serializers.thrift

typedef i32 int
typedef i64 long

enum Size {
  SMALL = 1,
  LARGE = 2,
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
  3: optional int width,
  4: optional int height,
  5: optional Size size,
}

struct Media {
  1: string uri,             //url to the thumbnail
  2: optional string title,
  3: optional int width,
  4: optional int height,
  5: optional string format,
  6: optional long duration,
  7: optional long size,
  8: optional int bitrate,
  9: optional list<string> person,
  10: optional Player player,
  11: optional string copyright,
}

struct MediaContent {
  1: optional list<Image> image,
  2: optional Media media,
}
