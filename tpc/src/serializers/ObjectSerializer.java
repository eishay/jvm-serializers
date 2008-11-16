package serializers;


public interface ObjectSerializer<T>
{
  public T create() throws Exception;
  public T deserialize(byte[] array) throws Exception;
  public byte[] serialize(T content) throws Exception;
  public String getName ();

}
