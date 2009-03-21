package serializers;

import java.io.ByteArrayOutputStream;

public interface ObjectSerializer<T>
{
    public T create() throws Exception;
    public T deserialize(byte[] array) throws Exception;
    public byte[] serialize(T content, ByteArrayOutputStream bos) throws Exception;
    public String getName ();
}
