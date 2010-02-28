package serializers;

public abstract class Serializer<S>
{
	public abstract S deserialize(byte[] array) throws Exception;
	public abstract byte[] serialize(S content) throws Exception;
 	public abstract String getName();
}
