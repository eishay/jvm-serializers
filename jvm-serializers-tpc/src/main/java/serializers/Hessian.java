package serializers;

import java.io.*;
import java.lang.reflect.Array;

import data.media.MediaContent;

import com.caucho.hessian.io.*;

public class Hessian
{
    public static void register(TestGroups groups)
    {
        groups.media.add(JavaBuiltIn.mediaTransformer, new HessianSerializer<MediaContent>(MediaContent.class), 
                new SerFeatures(
                        SerFormat.BIN_CROSSLANG,
                        SerGraph.FULL_GRAPH,
                        SerClass.ZERO_KNOWLEDGE,""
                ) 
        );
    }

    // ------------------------------------------------------------
    // Serializer (just one)

	public final static class HessianSerializer<T> extends Serializer<T>
	{
	    private final Class<T> clz;
	    
	    public HessianSerializer(Class<T> c) { clz = c; }
	    
            public String getName() { return "hessian"; }

            @SuppressWarnings("unchecked")
            public T deserialize(byte[] array) throws Exception
	    {
	        ByteArrayInputStream in = new ByteArrayInputStream(array);
	        Hessian2StreamingInput hin = new Hessian2StreamingInput(in);
	        return (T) hin.readObject();
	    }

	    public byte[] serialize(T data) throws java.io.IOException
	    {
	        ByteArrayOutputStream out = outputStream(data);
	        Hessian2StreamingOutput hout = new Hessian2StreamingOutput(out);
	        hout.writeObject(data);
	        return out.toByteArray();
	    }

            @Override
            public final void serializeItems(T[] items, OutputStream out) throws Exception
            {
                Hessian2StreamingOutput hout = new Hessian2StreamingOutput(out);
                for (Object item : items) {
                    hout.writeObject(item);
                }
                hout.flush();
                hout.close();
            }

            @SuppressWarnings("unchecked")
            @Override
            public T[] deserializeItems(InputStream in, int numberOfItems) throws Exception 
            {
                Hessian2StreamingInput hin = new Hessian2StreamingInput(in);
                T[] result = (T[]) Array.newInstance(clz, numberOfItems);
                for (int i = 0; i < numberOfItems; ++i) {
                    result[i] = (T) hin.readObject();
                }
                hin.close();
                return result;
            }	
	}
}
