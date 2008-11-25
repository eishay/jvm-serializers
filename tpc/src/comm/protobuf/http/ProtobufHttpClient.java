package comm.protobuf.http;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

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

public class ProtobufHttpClient
{
  private Stub _newStub;
  private final ObjectSerializer<MediaContent> _serializer = new ProtobufSerializer();

  public ProtobufHttpClient (Stub newStub)
  {
    _newStub = newStub;
  }

  public static void main (String... args)
      throws Exception
  {
    final HttpClient client = new HttpClient();
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
//          System.out.println("calling");
          PostMethod post = new PostMethod("http://localhost:8080/protowar/ProtoWar");
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          DataOutputStream dos = new DataOutputStream(baos);
          dos.writeInt(method.getIndex());
          byte[] array = request.toByteArray();
          dos.writeInt(array.length);
          dos.write(array);
          dos.flush();
          byte[] toSend = baos.toByteArray();
//          System.out.println("sending size = " + toSend.length);
          post.setRequestEntity(new ByteArrayRequestEntity(toSend ));
          client.executeMethod(post);
          DataInputStream dis = new DataInputStream(post.getResponseBodyAsStream());
//          byte[] ba = new byte[200];
//          dis.readFully(ba );
//          String str = new String(ba);
//          System.out.println(str );
          int size = dis.readInt();
          array = new byte[size];
          dis.readFully(array);
          done.run(responsePrototype.newBuilderForType().mergeFrom(array).build());
//          System.out.println("done");
          post.releaseConnection();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    };

    ProtobufHttpClient pc = new ProtobufHttpClient(MediaServer.newStub(channel));
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
    for (int i = 0; i < 20; i++)
    {
      long time = System.currentTimeMillis();
      for (int j = 0; j < 100; j++)
      {
        _newStub.callMethod(MediaServer.getDescriptor().findMethodByName("updateMedia"), null, _serializer.create(), done);
      }
      double delta = ((double) System.currentTimeMillis() - (double) time) / 100d;
      min = Math.min(delta, min);
      System.out.printf("#" + i + " took %10.10f milli\n", delta);
    }
    System.out.println("min " + min);
  }
}
