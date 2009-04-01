package serializers;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashSet;
import java.util.Set;

public class BenchmarkRunner
{
  public final static int ITERATIONS = 2000;
  public final static int TRIALS = 20;

    /**
     * Number of milliseconds to warm up for each operation type for
     * each serializer. Let's start with 3 seconds.
     */
    final static long WARMUP_MSECS = 3000;


  @SuppressWarnings("unchecked")
  private Set<ObjectSerializer> _serializers = new LinkedHashSet<ObjectSerializer>();

  public static void main(String ...args) throws Exception
  {
    BenchmarkRunner runner = new BenchmarkRunner();

    // binary codecs first
    runner.addObjectSerializer(new ProtobufSerializer());
    runner.addObjectSerializer(new ThriftSerializer());
    runner.addObjectSerializer(new HessianSerializer());

    // then language default serializers
    runner.addObjectSerializer(new JavaSerializer());
    runner.addObjectSerializer(new JavaExtSerializer());
    runner.addObjectSerializer(new ScalaSerializer());

    // then Json
    runner.addObjectSerializer(new JsonSerializer());

    // then xml via stax, textual and binary
    runner.addObjectSerializer(new StaxSerializer("stax/woodstox",
                                                  new com.ctc.wstx.stax.WstxInputFactory(),
                                                  new com.ctc.wstx.stax.WstxOutputFactory()));
    runner.addObjectSerializer(new StaxSerializer("stax/aalto",
                                                  new com.fasterxml.aalto.stax.InputFactoryImpl(),
                                                  new com.fasterxml.aalto.stax.OutputFactoryImpl()));
    runner.addObjectSerializer(new StaxSerializer("binaryxml/FI",
                                                  new com.sun.xml.fastinfoset.stax.factory.StAXInputFactory(),
                                                  new com.sun.xml.fastinfoset.stax.factory.StAXOutputFactory()));
    runner.addObjectSerializer(new XStreamSerializer("xstream (xpp)", false, null, null));
    runner.addObjectSerializer(new XStreamSerializer("xstream (xpp with conv)", true, null, null));
    runner.addObjectSerializer(new XStreamSerializer("xstream (stax)", false,
                                                  new com.ctc.wstx.stax.WstxInputFactory(),
                                                  new com.ctc.wstx.stax.WstxOutputFactory()
    ));
    runner.addObjectSerializer(new XStreamSerializer("xstream (stax with conv)", true,
                                                  new com.ctc.wstx.stax.WstxInputFactory(),
                                                  new com.ctc.wstx.stax.WstxOutputFactory()
    ));
    runner.addObjectSerializer(new JavolutionXMLFormatSerializer());

    runner.addObjectSerializer(new SbinarySerializer());
    //runner.addObjectSerializer(new YamlSerializer());

    System.out.println("Starting");

    runner.start();
  }

  @SuppressWarnings("unchecked")
  private void addObjectSerializer (ObjectSerializer serializer)
  {
    _serializers.add(serializer);
  }

  private <T> double createObjects(ObjectSerializer<T> serializer, int iterations) throws Exception
  {
    long delta = 0;
    for(int i = 0; i < iterations; i++)
    {
      long start = System.nanoTime();
      serializer.create();
      delta += System.nanoTime() - start;
    }
    return iterationTime(delta) / 10d;
  }

  private double iterationTime (long delta)
  {
    return (double)delta / (double)(ITERATIONS);
  }

  private <T> double serializeObjects(ObjectSerializer<T> serializer, int iterations) throws Exception
  {
    // let's reuse same instance to reduce overhead
    long delta = 0;
    for(int i = 0; i < iterations; i++)
    {
        T obj = serializer.create();
        long start = System.nanoTime();
        serializer.serialize(obj);
        delta += System.nanoTime() - start;
        if(i % 1000 == 0) doGc();
    }
    return iterationTime(delta) / 10d;
  }

  private <T> double deserializeObjects(ObjectSerializer<T> serializer, int iterations) throws Exception
  {
    byte[] array = serializer.serialize(serializer.create());
    long delta = 0;
    for(int i = 0; i < iterations; i++)
    {
      long start = System.nanoTime();
      serializer.deserialize(array);
      delta += System.nanoTime() - start;
    }
    return iterationTime(delta) / 10d;
  }

    /**
     * JVM is not required to honor GC requests, but adding bit of sleep
     * around request is most likely to give it a chance to do it.
     */
    private void doGc()
    {
        try { Thread.sleep(100L); } catch (InterruptedException ie) { }
        System.gc();
        System.gc();
        System.gc();
        System.gc();
        try { Thread.sleep(100L); } catch (InterruptedException ie) { }
    }

  @SuppressWarnings("unchecked")
  private void start () throws Exception
  {
    System.out.printf("%-24s, %15s, %15s, %15s, %15s, %10s\n",
                      " ", "Object create", "Serialization", "Deserialization", "Total Time", "Serialized Size");
    for(ObjectSerializer serializer: _serializers)
    {
        /* Should only warm things for the serializer that we test next: HotSpot
         * JIT will otherwise spent most of its time optimizing slower ones...
         */
        warmCreation(serializer);
        doGc();
        double timeCreate = Double.MAX_VALUE;
        for(int i = 0; i < TRIALS; i++)
            timeCreate = Math.min(timeCreate, createObjects(serializer, ITERATIONS));

        warmSerialization(serializer);
        doGc();
        double timeSer = Double.MAX_VALUE;
        for(int i = 0; i < TRIALS; i++)
            timeSer = Math.min(timeSer, serializeObjects(serializer, ITERATIONS));

        warmDeserialization(serializer);
        doGc();
        double timeDSer = Double.MAX_VALUE;
        for(int i = 0; i < TRIALS; i++)
            timeDSer = Math.min(timeDSer, deserializeObjects(serializer, ITERATIONS));

        byte[] array = serializer.serialize(serializer.create());
        System.out.printf("%-24s, %15.5f, %15.5f, %15.5f, %15.5f, %10d\n", serializer.getName(), timeCreate, timeSer, timeDSer, timeCreate + timeSer + timeDSer, array.length);
    }
  }

  private <T> void warmCreation(ObjectSerializer<T> serializer) throws Exception
  {
      // Instead of fixed counts, let's try to prime by running for N seconds
      long endTime = System.currentTimeMillis() + WARMUP_MSECS;
      do {
          createObjects(serializer, 1);
      } while (System.currentTimeMillis() < endTime);
  }

  private <T> void warmSerialization(ObjectSerializer<T> serializer) throws Exception
  {
      // Instead of fixed counts, let's try to prime by running for N seconds
      long endTime = System.currentTimeMillis() + WARMUP_MSECS;
      do {
          serializeObjects(serializer, 1);
      } while (System.currentTimeMillis() < endTime);
  }

  private <T> void warmDeserialization(ObjectSerializer<T> serializer) throws Exception
  {
      // Instead of fixed counts, let's try to prime by running for N seconds
      long endTime = System.currentTimeMillis() + WARMUP_MSECS;
      do {
          deserializeObjects(serializer, 1);
      } while (System.currentTimeMillis() < endTime);
  }
}
