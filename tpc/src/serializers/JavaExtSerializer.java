package serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import serializers.extjava.Image;
import serializers.extjava.Media;
import serializers.extjava.MediaContent;
import serializers.extjava.Image.Size;
import serializers.extjava.Media.Player;


public class JavaExtSerializer implements ObjectSerializer<MediaContent> {

   public int expectedSize = 0;


   public JavaExtSerializer() {}

   public String getName() {
      return "java (externalizable)";
   }

   public MediaContent deserialize( byte[] array ) throws Exception {
      ObjectInput ois = new ExternalizableObjectInput(new ByteArrayInputStream(array));
      MediaContent mediaContent = null;
      if ( ois.readBoolean() ) {
         mediaContent = new MediaContent();
         mediaContent.readExternal(ois);
      }
      ois.close();
      return mediaContent;
   }

   public MediaContent create() {
      Media media = new Media(null, "video/mpg4", Player.JAVA, "Javaone Keynote", "http://javaone.com/keynote.mpg", 1234567, 123, 0, 0, 0);
      media.addToPerson("Bill Gates");
      media.addToPerson("Steve Jobs");

      Image image1 = new Image(0, "Javaone Keynote", "http://javaone.com/keynote_large.jpg", 0, Size.LARGE);
      Image image2 = new Image(0, "Javaone Keynote", "http://javaone.com/keynote_thumbnail.jpg", 0, Size.SMALL);

      MediaContent content = new MediaContent(media);
      content.addImage(image1);
      content.addImage(image2);
      return content;
   }

   public byte[] serialize( MediaContent content ) throws IOException, Exception {
      ByteArrayOutputStream baos = new ByteArrayOutputStream(expectedSize);
      ObjectOutput oos = new ExternalizableObjectOutput(baos);
      oos.writeBoolean(content != null);
      if ( content != null ) content.writeExternal(oos);
      oos.close();
      byte[] array = baos.toByteArray();
      expectedSize = array.length;
      return array;
   }
   
   private static class ExternalizableObjectInput extends DataInputStream implements ObjectInput {

      public ExternalizableObjectInput( InputStream in ) {
         super(in);
      }

      public Object readObject() throws ClassNotFoundException, IOException {
         throw new UnsupportedOperationException("This implementation of ObjectInput does not provide readObject(). Use constructor and x.readExternal(in) instead.");
      }
   }

   public static class ExternalizableObjectOutput extends DataOutputStream implements ObjectOutput {

      public ExternalizableObjectOutput( OutputStream out) {
         super(out);
      }

      public void writeObject( Object obj ) throws IOException {
         throw new UnsupportedOperationException("This implementation of ObjectOutput does not provide writeObject(). Use x.writeExternal(in) instead.");
      }
   }

}