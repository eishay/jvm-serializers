package serializers;

import java.util.HashSet;
import java.util.Set;

public class BenchmarkRunner
{
  public final static int ITERATIONS = 200000;
  
  @SuppressWarnings("unchecked")
  private Set<ObjectSerializer> _serializers = new HashSet<ObjectSerializer>();

  public static void main(String ...args) throws Exception
  {
    BenchmarkRunner runner = new BenchmarkRunner();
    runner.addObjectSerializer(new ProtobufSerializer());
    runner.addObjectSerializer(new ThriftSerializer());
    runner.addObjectSerializer(new JavaSerializer());
    runner.start();
  }

  @SuppressWarnings("unchecked")
  private void addObjectSerializer (ObjectSerializer serializer)
  {
    _serializers.add(serializer);
  }

  private <T> double createObjects(ObjectSerializer<T> serializer) throws Exception
  {
    System.gc();
    long start = System.currentTimeMillis();
    for(int i = 0; i < ITERATIONS; i++)
    {
      serializer.create();
    }
    return timePerIteration(start);
  }

  private double timePerIteration (long start)
  {
    return ((double)System.currentTimeMillis() - (double)start) / (double)ITERATIONS;
  }
  
  private <T> double serializeObjects(ObjectSerializer<T> serializer) throws Exception
  {
    System.gc();
    T obj = serializer.create();
    long start = System.currentTimeMillis();
    for(int i = 0; i < ITERATIONS; i++)
    {
      serializer.serialize(obj);
    }
    return timePerIteration(start);
  }
  
  private <T> double deserializeObjects(ObjectSerializer<T> serializer) throws Exception
  {
    System.gc();
    byte[] array = serializer.serialize(serializer.create());
    long start = System.currentTimeMillis();
    for(int i = 0; i < ITERATIONS; i++)
    {
      serializer.deserialize(array);
    }
    return timePerIteration(start);
  }
  
  @SuppressWarnings("unchecked")
  private void start () throws Exception
  {
    warmObjects();

    for(ObjectSerializer serializer: _serializers)
    {
      double time = createObjects(serializer);
      System.out.printf("[%s] Object create time = %1.5f mili\n", serializer.getName(), time);

      time = serializeObjects(serializer);
      System.out.printf("[%s] Object serialize time = %1.5f mili\n", serializer.getName(), time);

      time = deserializeObjects(serializer);
      System.out.printf("[%s] Object dserialize time = %1.5f mili\n", serializer.getName(), time);

      byte[] array = serializer.serialize(serializer.create());
      System.out.println("[" + serializer.getName() + "] Serialized size = " + array.length);
    }
  }

  private <T> void warmObjects () throws Exception
  {
    System.out.println("warming up...");
    for(ObjectSerializer<T> serializer: _serializers) createObjects(serializer);
    for(ObjectSerializer<T> serializer: _serializers) serializeObjects(serializer);
    for(ObjectSerializer<T> serializer: _serializers) deserializeObjects(serializer);
    System.out.println("Starting");
  }
}
