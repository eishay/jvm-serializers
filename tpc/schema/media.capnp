@0xfdb08e6f13e7cf36;
using Java = import "/java.capnp";
$Java.package("serializers.capnproto.media");
$Java.outerClassname("Mediacontent");

struct Image {
  uri @0 :Text;
  title @1 :Text;
  width @2 :Int32;
  height @3 :Int32;
  enum Size {
    small @0;
    large @1;
  }
  size @4 :Size;
}

struct Media {
  uri @0 :Text;
  title @1 :Text;
  width @2 :Int32;
  height @3 :Int32;
  format @4 :Text;
  duration @5 :Int64;
  size @6 :Int64;
  bitrate @7 :Int32;
  persons @8 :List(Text);
  enum Player {
    java @0;
    flash @1;
  }
  player @9 :Player;
  copyright @10 :Text;
}

struct MediaContent {
  images @0 :List(Image);
  media @1 :Media;
}
