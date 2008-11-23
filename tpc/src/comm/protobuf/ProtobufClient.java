package comm.protobuf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import serializers.ObjectSerializer;
import serializers.ProtobufSerializer;
import serializers.protobuf.MediaContentHolder.MediaContent;
import serializers.protobuf.MediaContentHolder.MediaServer;
import serializers.protobuf.MediaContentHolder.MediaServer.Stub;

import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcChannel;
import com.google.protobuf.RpcController;
import com.google.protobuf.Descriptors.MethodDescriptor;

public class ProtobufClient
{
  private Stub _newStub;
  private final ObjectSerializer<MediaContent> _serializer = new ProtobufSerializer();

  public ProtobufClient (Stub newStub)
  {
    _newStub = newStub;
  }

  public static void main (String... args)
      throws Exception
  {
    Socket socket = new Socket("127.0.0.1", 7777);
    final DataInputStream dis = new DataInputStream(socket.getInputStream());
    final DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
    RpcChannel channel = new RpcChannel()
    {
      public void callMethod (MethodDescriptor method,
                              RpcController controller,
                              Message request,
                              Message responsePrototype,
                              RpcCallback<Message> done)
      {
        try
        {
          //System.out.println("calling");
          dos.writeInt(method.getIndex());
          byte[] array = request.toByteArray();
          dos.writeInt(array.length);
          dos.write(array);
          int size = dis.readInt();
          array = new byte[size];
          dis.readFully(array);
          done.run(responsePrototype.newBuilderForType().mergeFrom(array).build());
          //System.out.println("done");
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    };

    ProtobufClient pc = new ProtobufClient(MediaServer.newStub(channel));
    pc.start();
  }

  private void start ()
      throws Exception
  {
    
    RpcCallback<Message> done = new RpcCallback<Message>()
    {

      public void run (Message message)
      {
        //System.out.println(message.getDescriptorForType().getFullName());
      }
    };
    double min = 10000;
    for (int i = 0; i < 100; i++)
    {
      long time = System.currentTimeMillis();
      for (int j = 0; j < 1000; j++)
      {
        _newStub.callMethod(MediaServer.getDescriptor().findMethodByName("updateMedia"), null, _serializer.create(), done);
      }
      double delta = ((double) System.currentTimeMillis() - (double) time) / 1000d;
      min = Math.min(delta, min);
      System.out.printf("took %10.10f milli\n", delta);
    }
    System.out.println("min " + min);
  }
}
