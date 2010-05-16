package serializers;

import java.io.*;

public abstract class Serializer<S>
{
	public abstract S deserialize(byte[] array) throws Exception;
	public abstract byte[] serialize(S content) throws Exception;
 	public abstract String getName();

 	public ByteArrayOutputStream outputStream(S content)
 	{
 	    /* 15-May-2010, tsaloranta: For now, let's use a reasonable
 	     *  guess; not too small nor too large a buffer.
 	     */
 	    return new ByteArrayOutputStream(500);
 	}
}
