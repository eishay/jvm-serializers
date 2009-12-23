package serializers;

public interface CheckingObjectSerializer<T> extends ObjectSerializer<T> {
    public void checkAllFields(T obj);
    public void checkMediaField(T obj);
}
