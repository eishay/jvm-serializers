package serializers.extjava;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


public class ExternalizationHelper {

   public static final String readString( final ObjectInput in ) throws IOException {
      if ( !in.readBoolean() ) return null;
      return in.readUTF();
   }

   public static final void writeString( final ObjectOutput out, String s ) throws IOException {
      out.writeBoolean(s != null);
      if ( s != null ) out.writeUTF(s);
   }
}
