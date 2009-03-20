package serializers;

import java.util.HashSet;
import java.util.Set;

public class BenchmarkRunner
{
  public final static int ITERATIONS = 2000;
  public final static int TRIALS = 20;

  @SuppressWarnings("unchecked")
  private Set<ObjectSerializer> _serializers = new HashSet<ObjectSerializer>();

  public static void main(String ...args) throws Exception
  {
    BenchmarkRunner runner = new BenchmarkRunner();
    runner.addObjectSerializer(new ProtobufSerializer());
    runner.addObjectSerializer(new ThriftSerializer());
    runner.addObjectSerializer(new JavaSerializer());
    runner.addObjectSerializer(new JavaExtSerializer());
    runner.addObjectSerializer(new ScalaSerializer());
    runner.addObjectSerializer(new StaxSerializer());
    runner.addObjectSerializer(new JsonSerializer());
    runner.addObjectSerializer(new XStreamSerializer("xstream (xpp)", false, false));
    runner.addObjectSerializer(new XStreamSerializer("xstream (stax)", true, false));
    runner.addObjectSerializer(new XStreamSerializer("xstream (stax with conv)", true, true));
    runner.addObjectSerializer(new JavolutionXMLFormatSerializer());
    runner.addObjectSerializer(new SbinarySerializer());
    //runner.addObjectSerializer(new YamlSerializer());
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
    long start = System.nanoTime();
    for(int i = 0; i < ITERATIONS * 10; i++)
    {
      serializer.create();
    }
    return timePerIteration(start) / 10d;
  }

  private double timePerIteration (long start)
  {
    return ((double)System.nanoTime() - (double)start) / (double)(ITERATIONS);
  }

  private <T> double serializeObjects(ObjectSerializer<T> serializer) throws Exception
  {
    System.gc();
    T obj = serializer.create();
    long start = System.nanoTime();
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
    long start = System.nanoTime();
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

    System.out.printf("%-30s, %15s, %15s, %15s, %10s\n", " ", "Object create", "Serializaton", "Deserialization", "Serialized Size");
    for(ObjectSerializer serializer: _serializers)
    {
      double timeCreate = createObjects(serializer);
      for(int i = 0; i < TRIALS; i++)
        timeCreate = Math.min(timeCreate, createObjects(serializer));

      double timeSer = serializeObjects(serializer);
      for(int i = 0; i < TRIALS; i++)
        timeSer = Math.min(timeSer, serializeObjects(serializer));

      double timeDSer = deserializeObjects(serializer);
      for(int i = 0; i < TRIALS; i++)
        timeDSer = Math.min(timeDSer, deserializeObjects(serializer));

      byte[] array = serializer.serialize(serializer.create());
      System.out.printf("%-30s, %15.5f, %15.5f, %15.5f, %10d\n", serializer.getName(), timeCreate, timeSer, timeDSer, array.length);
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
