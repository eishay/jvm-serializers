package serializers;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashSet;
import java.util.Set;

public class BenchmarkRunner
{
  public final static int ITERATIONS = 2000;
  public final static int TRIALS = 20;

  @SuppressWarnings("unchecked")
  private Set<ObjectSerializer> _serializers = new LinkedHashSet<ObjectSerializer>();

  public static void main(String ...args) throws Exception
  {
    BenchmarkRunner runner = new BenchmarkRunner();

    runner.addObjectSerializer(new ProtobufSerializer());
    runner.addObjectSerializer(new ThriftSerializer());
    runner.addObjectSerializer(new JavaSerializer());
    runner.addObjectSerializer(new JavaExtSerializer());
    runner.addObjectSerializer(new ScalaSerializer());

    // let's test both Woodstox and Aalto
    runner.addObjectSerializer(new StaxSerializer("stax/woodstox",
                                                  new com.ctc.wstx.stax.WstxInputFactory(),
                                                  new com.ctc.wstx.stax.WstxOutputFactory()));
    runner.addObjectSerializer(new StaxSerializer("stax/aalto",
                                                  new com.fasterxml.aalto.stax.InputFactoryImpl(),
                                                  new com.fasterxml.aalto.stax.OutputFactoryImpl()));
    // And also Fast Infoset (binary xml)
    runner.addObjectSerializer(new StaxSerializer("binaryxml/FI",
                                                  new com.sun.xml.fastinfoset.stax.factory.StAXInputFactory(),
                                                  new com.sun.xml.fastinfoset.stax.factory.StAXOutputFactory()));

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
    for(int i = 0, len = ITERATIONS * 10; i < len; i++)
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
    // let's reuse same instance to reduce overhead
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    T obj = serializer.create();
    long start = System.nanoTime();
    for(int i = 0; i < ITERATIONS; i++)
    {
        bos.reset();
        serializer.serialize(obj, bos);
    }
    return timePerIteration(start);
  }

  private <T> double deserializeObjects(ObjectSerializer<T> serializer) throws Exception
  {
    System.gc();
    byte[] array = serializer.serialize(serializer.create(), new ByteArrayOutputStream(200));
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

    System.out.printf("%-24s, %15s, %15s, %15s, %10s\n", " ", "Object create", "Serialization", "Deserialization", "Serialized Size");
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

      byte[] array = serializer.serialize(serializer.create(), new ByteArrayOutputStream(200));
      System.out.printf("%-24s, %15.5f, %15.5f, %15.5f, %10d\n", serializer.getName(), timeCreate, timeSer, timeDSer, array.length);
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
