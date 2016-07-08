// automatically generated, do not modify

package serializers.flatbuffers.media;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Media extends Table {
  public static Media getRootAsMedia(ByteBuffer _bb) { return getRootAsMedia(_bb, new Media()); }
  public static Media getRootAsMedia(ByteBuffer _bb, Media obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public Media __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public String uri() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer uriAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public String title() { int o = __offset(6); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer titleAsByteBuffer() { return __vector_as_bytebuffer(6, 1); }
  public int width() { int o = __offset(8); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int height() { int o = __offset(10); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public String format() { int o = __offset(12); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer formatAsByteBuffer() { return __vector_as_bytebuffer(12, 1); }
  public long duration() { int o = __offset(14); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public long size() { int o = __offset(16); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public int bitrate() { int o = __offset(18); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public String persons(int j) { int o = __offset(20); return o != 0 ? __string(__vector(o) + j * 4) : null; }
  public int personsLength() { int o = __offset(20); return o != 0 ? __vector_len(o) : 0; }
  public byte player() { int o = __offset(22); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public String copyright() { int o = __offset(24); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer copyrightAsByteBuffer() { return __vector_as_bytebuffer(24, 1); }

  public static int createMedia(FlatBufferBuilder builder,
      int uriOffset,
      int titleOffset,
      int width,
      int height,
      int formatOffset,
      long duration,
      long size,
      int bitrate,
      int personsOffset,
      byte player,
      int copyrightOffset) {
    builder.startObject(11);
    Media.addSize(builder, size);
    Media.addDuration(builder, duration);
    Media.addCopyright(builder, copyrightOffset);
    Media.addPersons(builder, personsOffset);
    Media.addBitrate(builder, bitrate);
    Media.addFormat(builder, formatOffset);
    Media.addHeight(builder, height);
    Media.addWidth(builder, width);
    Media.addTitle(builder, titleOffset);
    Media.addUri(builder, uriOffset);
    Media.addPlayer(builder, player);
    return Media.endMedia(builder);
  }

  public static void startMedia(FlatBufferBuilder builder) { builder.startObject(11); }
  public static void addUri(FlatBufferBuilder builder, int uriOffset) { builder.addOffset(0, uriOffset, 0); }
  public static void addTitle(FlatBufferBuilder builder, int titleOffset) { builder.addOffset(1, titleOffset, 0); }
  public static void addWidth(FlatBufferBuilder builder, int width) { builder.addInt(2, width, 0); }
  public static void addHeight(FlatBufferBuilder builder, int height) { builder.addInt(3, height, 0); }
  public static void addFormat(FlatBufferBuilder builder, int formatOffset) { builder.addOffset(4, formatOffset, 0); }
  public static void addDuration(FlatBufferBuilder builder, long duration) { builder.addLong(5, duration, 0); }
  public static void addSize(FlatBufferBuilder builder, long size) { builder.addLong(6, size, 0); }
  public static void addBitrate(FlatBufferBuilder builder, int bitrate) { builder.addInt(7, bitrate, 0); }
  public static void addPersons(FlatBufferBuilder builder, int personsOffset) { builder.addOffset(8, personsOffset, 0); }
  public static int createPersonsVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startPersonsVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addPlayer(FlatBufferBuilder builder, byte player) { builder.addByte(9, player, 0); }
  public static void addCopyright(FlatBufferBuilder builder, int copyrightOffset) { builder.addOffset(10, copyrightOffset, 0); }
  public static int endMedia(FlatBufferBuilder builder) {
    int o = builder.endObject();
    builder.required(o, 4);  // uri
    builder.required(o, 12);  // format
    builder.required(o, 20);  // persons
    return o;
  }
};

