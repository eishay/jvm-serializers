package comm.protobuf.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

import serializers.ObjectSerializer;
import serializers.ProtobufSerializer;
import serializers.protobuf.MediaContentHolder.MediaContent;
import serializers.protobuf.MediaContentHolder.MediaServer;

import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.Descriptors.MethodDescriptor;

public class ProtobufServer extends MediaServer
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

  public static void main (String... args)
      throws IOException
  {
    ProtobufServer ps = new ProtobufServer();
    ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(7777);
    while (true)
    {
      try
      {
        ps.start(serverSocket);
      }
      catch (Exception e)
      {
        System.out.println(e);
      }
    }
  }

  private void start (ServerSocket serverSocket)
      throws Exception
  {
    System.out.println("listening");
    final Socket socket = serverSocket.accept();
    System.out.println("accepted");
    try
    {
      RpcCallback<Message> done = new RpcCallback<Message>()
      {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        public void run (Message content)
        {
          try
          {
            byte[] array = _serializer.serialize((MediaContent) content);
            dos.writeInt(array.length);
            dos.write(array);
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
        }
      };
      DataInputStream dis = new DataInputStream(socket.getInputStream());
      while (true)
      {
        int index = dis.readInt();
        MethodDescriptor method = getDescriptor().getMethods().get(index);
        byte[] array = new byte[dis.readInt()];
        dis.readFully(array);
        Message request = getRequestPrototype(method).newBuilderForType().mergeFrom(array).build();
        callMethod(method, null, request, done);
      }
    }
    finally
    {
      socket.close();
    }
  }
}
