package org.protowar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import serializers.ObjectSerializer;
import serializers.ProtobufSerializer;
import serializers.protobuf.MediaContentHolder.MediaContent;
import serializers.protobuf.MediaContentHolder.MediaServer;

import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.Descriptors.MethodDescriptor;

class ProtoServerHandler extends MediaServer
{
  private final ObjectSerializer<MediaContent> _serializer = new ProtobufSerializer();

  @Override
  public void updateMedia (RpcController controller, MediaContent request, RpcCallback<MediaContent> done)
  {
    try
    {
      //System.out.println(request.getMedia().getUri());
      done.run(_serializer.create());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  void handle(final OutputStream os, final InputStream is) throws IOException
  {
    RpcCallback<Message> done = new RpcCallback<Message>()
    {
      DataOutputStream dos = new DataOutputStream(os);

      public void run (Message content)
      {
        try
        {
          byte[] array = _serializer.serialize((MediaContent) content);
          dos.writeInt(array.length);
          dos.write(array);
          dos.flush();
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    };
    DataInputStream dis = new DataInputStream(is);
    int index = dis.readInt();
    MethodDescriptor method = getDescriptor().getMethods().get(index);
    byte[] array = new byte[dis.readInt()];
    dis.readFully(array);
    Message request = getRequestPrototype(method).newBuilderForType().mergeFrom(array).build();
    callMethod(method, null, request, done);
  }
}
