package serializers;

import java.io.*;

public abstract class Serializer<S>
{
	public abstract S deserialize(byte[] array) throws Exception;
	public abstract byte[] serialize(S content) throws Exception;
 	public abstract String getName();

 	// Size doesn't matter since after warm up the backing array will be big enough since it is reused.
 	private ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
 	
	public ByteArrayOutputStream outputStream (S content) {
		outputStream.reset();
		return outputStream;
	}

	// And then bit bigger default when serializing a list or array
	public ByteArrayOutputStream outputStreamForList (S[] items) {
		outputStream.reset();
		return outputStream;
	}

 	// Multi-item interfaces
 	
 	public S[] deserializeItems(InputStream in, int numberOfItems) throws Exception {
 	    throw new UnsupportedOperationException("Not implemented");
 	}

 	public void serializeItems(S[] items, OutputStream out) throws Exception {
            throw new UnsupportedOperationException("Not implemented");
 	}

        public final byte[] serializeAsBytes(S[] items) throws Exception {
            ByteArrayOutputStream bytes = outputStreamForList(items);
            serializeItems(items, bytes);
            return bytes.toByteArray();
        }

        // // // Helper methods

        protected byte[] readAll(InputStream in) throws IOException
        {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream(4000);
            byte[] buffer = new byte[4000];
            int count;
            
            while ((count = in.read(buffer)) >= 0) {
                bytes.write(buffer, 0, count);
            }
            in.close();
            return bytes.toByteArray();
        }
}
