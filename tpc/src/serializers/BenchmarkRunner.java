package serializers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class BenchmarkRunner
{
	public final static int DEFAULT_ITERATIONS = 2000;
	public final static int DEFAULT_TRIALS = 20;

	/**
	 * Number of milliseconds to warm up for each operation type for each serializer. Let's
	 * start with 3 seconds.
	 */
	final static long DEFAULT_WARMUP_MSECS = 3000;

	public static void main(String[] args) throws Exception
	{
		// --------------------------------------------------
		// Parse command-line options.

		Boolean filterIsInclude = null;
		Set<String> filterStrings = null;
		Integer iterations = null;
		Integer trials = null;
		Long warmupTime = null;
		boolean printChart = false;
		String dataFileName = null;

		Set<String> optionsSeen = new HashSet<String>();

		for (String arg : args) {
			String remainder;
			if (arg.startsWith("--")) {
				remainder = arg.substring(2);
			}
			else if (arg.startsWith("-")) {
				remainder = arg.substring(1);
			}
			else if (dataFileName == null) {
				dataFileName = arg;
				continue;
			}
			else {
				System.err.println("Expecting only one non-option argument (<data-file> = \"" + dataFileName + "\").");
				System.err.println("Found a second one: \"" + arg + "\"");
				System.err.println("Use \"-help\" for usage information.");
				System.exit(1); return;
			}

			String option, value;
			int eqPos = remainder.indexOf('=');
			if (eqPos >= 0) {
				option = remainder.substring(0, eqPos);
				value = remainder.substring(eqPos+1);
			} else {
				option = remainder;
				value = null;
			}

			if (!optionsSeen.add(option)) {
				System.err.println("Repeated option: \"" + arg + "\"");
				System.exit(1); return;
			}

			if (option.equals("include")) {
				if (value == null) {
					System.err.println("The \"include\" option requires a value.");
					System.exit(1); return;
				}
				if (filterIsInclude == null) {
					filterIsInclude = true;
					filterStrings = new HashSet<String>(Arrays.asList(value.split(",")));
				} else {
					System.err.println("Can't use 'include' and 'exclude' options at the same time.");
					System.exit(1); return;
				}
			}
			else if (option.equals("exclude")) {
				if (value == null) {
					System.err.println("The \"exclude\" option requires a value.");
					System.exit(1); return;
				}
				if (filterIsInclude == null) {
					filterIsInclude = false;
					filterStrings = new HashSet<String>(Arrays.asList(value.split(",")));
				} else {
					System.err.println("Can't use 'include' and 'exclude' options at the same time.");
					System.exit(1); return;
				}
			}
			else if (option.equals("iterations")) {
				if (value == null) {
					System.err.println("The \"iterations\" option requires a value.");
					System.exit(1); return;
				}
				assert iterations == null;
				try {
					iterations = Integer.parseInt(value);
				} catch (NumberFormatException ex) {
					System.err.println("Invalid value for \"iterations\" option: \"" + value + "\"");
					System.exit(1); return;
				}
				if (iterations < 1) {
					System.err.println("Invalid value for \"iterations\" option: \"" + value + "\"");
					System.exit(1); return;
				}
			}
			else if (option.equals("trials")) {
				if (value == null) {
					System.err.println("The \"trials\" option requires a value.");
					System.exit(1); return;
				}
				assert trials == null;
				try {
					trials = Integer.parseInt(value);
				} catch (NumberFormatException ex) {
					System.err.println("Invalid value for \"trials\" option: \"" + value + "\"");
					System.exit(1); return;
				}
				if (trials < 1) {
					System.err.println("Invalid value for \"trials\" option: \"" + value + "\"");
					System.exit(1); return;
				}
			}
			else if (option.equals("warmup-time")) {
				if (value == null) {
					System.err.println("The \"warmup-time\" option requires a value.");
					System.exit(1); return;
				}
				assert warmupTime == null;
				try {
					warmupTime = Long.parseLong(value);
				} catch (NumberFormatException ex) {
					System.err.println("Invalid value for \"warmup-time\" option: \"" + value + "\"");
					System.exit(1); return;
				}
				if (warmupTime < 0) {
					System.err.println("Invalid value for \"warmup-time\" option: \"" + value + "\"");
					System.exit(1); return;
				}
			}
			else if (option.equals("chart")) {
				if (value != null) {
					System.err.println("The \"chart\" option does not take a value: \"" + arg + "\"");
					System.exit(1); return;
				}
				assert !printChart;
				printChart = true;
			}
			else if (option.equals("help")) {
				if (value != null) {
					System.err.println("The \"help\" option does not take a value: \"" + arg + "\"");
					System.exit(1); return;
				}
				if (args.length != 1) {
					System.err.println("The \"help\" option cannot be combined with any other option.");
					System.exit(1); return;
				}

				System.out.println();
				System.out.println("Usage: run [options] <data-file>");
				System.out.println();
				System.out.println("Options:");
				System.out.println("  -iterations=n              [default=" + DEFAULT_ITERATIONS + "]");
				System.out.println("  -trials=n                  [default=" + DEFAULT_TRIALS + "]");
				System.out.println("  -warmup-time=milliseconds  [default=" + DEFAULT_WARMUP_MSECS + "]");
				System.out.println("  -chart");
				System.out.println("  -include=impl1,impl2,impl3,...");
				System.out.println("  -exclude=impl1,impl2,impl3,...");
				System.out.println("  -help");
				System.out.println();
				System.out.println("Example: run  -chart -include=protobuf,thrift  data/media.1.cks");
				System.out.println();
				System.exit(0); return;
			}
			else {
				System.err.println("Unknown option \"" + option + "\": \"" + arg + "\"");
				System.err.println("Use \"-help\" for usage information.");
				System.exit(1); return;
			}
		}

		if (iterations == null) iterations = DEFAULT_ITERATIONS;
		if (trials == null) trials = DEFAULT_TRIALS;
		if (warmupTime == null) warmupTime = DEFAULT_WARMUP_MSECS;

		if (dataFileName == null) {
			System.err.println("Missing <data-file> argument.");
			System.err.println("Use \"-help\" for usage information.");
			System.exit(1); return;
		}

		// --------------------------------------------------
		// Load serializers.

		TestGroups groups = new TestGroups();

		// Binary Format, Specialized Serialization
		Protobuf.register(groups);
		Thrift.register(groups);
		ActiveMQProtobuf.register(groups);
		Kryo.register(groups);
		AvroSpecific.register(groups);
		CksBinary.register(groups);

		// Binary Formats, Generic Serialization
		JavaBuiltIn.register(groups);
		AvroGeneric.register(groups);

		// Abbreviated Text Formats
		JsonJackson.register(groups);

		// Text Formats
		JsonJacksonDatabind.register(groups);
		JsonTwoLattes.register(groups);
		ProtostuffJson.register(groups);
		CksText.register(groups);
		ProtobufJson.register(groups);
		Gson.register(groups);

		// --------------------------------------------------
		// Load data value.

		Object dataValue;
		TestGroup<?> group;
		{
			File dataFile = new File(dataFileName);
			String[] parts = dataFile.getName().split("\\.");
			if (parts.length < 3) {
				System.out.println("Data file \"" + dataFile.getName() + "\" should be of the form \"<type>.<name>.<extension>\"");
				System.exit(1); return;
			}

			String dataType = parts[0];
			String extension = parts[parts.length-1];

			group = groups.groupMap.get(dataType);
			if (group == null) {
				System.out.println("Data file \"" + dataFileName + "\" can't be loaded.");
				System.out.println("Don't know about data type \"" + dataType + "\"");
				System.exit(1); return;
			}

			TestGroup.Entry<?,Object> loader = group.extensionMap.get(parts[parts.length-1]);
			if (loader == null) {
				System.out.println("Data file \"" + dataFileName + "\" can't be loaded.");
				System.out.println("No deserializer registered for data type \"" + dataType + "\" and file extension \"." + extension + "\"");
				System.exit(1); return;
			}

			// Load entire file into a byte array.
			byte[] fileBytes = readFile(new File(dataFileName));

			Object deserialized;
			try {
				deserialized = loader.serializer.deserialize(fileBytes);
			}
			catch (Exception ex) {
				System.err.println("Error loading data from file \"" + dataFileName + "\".");
				System.err.println(ex.getMessage());
				System.exit(1); return;
			}

			dataValue = loader.transformer.reverse(deserialized);
		}

		@SuppressWarnings("unchecked")
		TestGroup<Object> group_ = (TestGroup<Object>) group;

		// --------------------------------------------------

		Iterable<TestGroup.Entry<Object,Object>> matchingEntries;
		if (filterStrings == null) {
			matchingEntries = group_.entries;
		}
		else {
			ArrayList<TestGroup.Entry<Object,Object>> al = new ArrayList<TestGroup.Entry<Object,Object>>();
			matchingEntries = al;

			for (TestGroup.Entry<?,Object> entry_ : group.entries) {
				@SuppressWarnings("unchecked")
				TestGroup.Entry<Object,Object> entry = (TestGroup.Entry<Object,Object>) entry_;

				String name = entry.serializer.getName();
				boolean found = filterStrings.remove(name);
				if (found == filterIsInclude) {
					al.add(entry);
				}
			}

			for (String s : filterStrings) {
				System.err.println("Warning: there is no implementation named \"" + s + "\"");
			}
		}

		EnumMap<measurements, Map<String, Double>> values = start(iterations, trials, warmupTime, matchingEntries, dataValue);


		if (printChart) {
			printImages(values);
		}
	}

	// ------------------------------------------------------------------------------------

	private static byte[] readFile(File file)
		throws IOException
	{
		FileInputStream fin = new FileInputStream(file);
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] data = new byte[1024];
			while (true) {
				int numBytes = fin.read(data);
				if (numBytes < 0) break;
				baos.write(data, 0, numBytes);
			}
			return baos.toByteArray();
		}
		finally {
			fin.close();
		}
	}

	// ------------------------------------------------------------------------------------

	private static double iterationTime(long delta, int iterations)
	{
		return (double) delta / (double) (iterations);
	}

	private static final TestCase Create = new TestCase()
	{
		public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
		{
			long start = System.nanoTime();
			for (int i = 0; i < iterations; i++)
			{
				transformer.forward(value);
			}
			return iterationTime(System.nanoTime() - start, iterations);
		}
	};

	private static final TestCase Serialize = new TestCase()
	{
		public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
		{
			long start = System.nanoTime();
			for (int i = 0; i < iterations; i++)
			{
				Object obj = transformer.forward(value);
				serializer.serialize(obj);
			}
			return iterationTime(System.nanoTime() - start, iterations);
		}
	};

	private static final TestCase SerializeSameObject = new TestCase()
	{
		public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
		{
			// let's reuse same instance to reduce overhead
			Object obj = transformer.forward(value);
			long start = System.nanoTime();
			for (int i = 0; i < iterations; i++)
			{
				serializer.serialize(obj);
				//if (i % 1000 == 0)
				//	doGc();
			}
			return iterationTime(System.nanoTime() - start, iterations);
		}
	};

	private static final TestCase Deserialize = new TestCase()
	{
		public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
		{
			byte[] array = serializer.serialize(transformer.forward(value));
			long start = System.nanoTime();
			for (int i = 0; i < iterations; i++)
			{
				serializer.deserialize(array);
			}
			return iterationTime(System.nanoTime() - start, iterations);
		}
	};

	private static final TestCase DeserializeAndCheck = new TestCase()
	{
		public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
		{
			byte[] array = serializer.serialize(transformer.forward(value));
			long start = System.nanoTime();
			for (int i = 0; i < iterations; i++)
			{
				Object obj = serializer.deserialize(array);
				transformer.reverse(obj);
			}
			return iterationTime(System.nanoTime() - start, iterations);
		}
	};

	private static final TestCase DeserializeAndCheckShallow = new TestCase()
	{
		public <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception
		{
			byte[] array = serializer.serialize(transformer.forward(value));
			long start = System.nanoTime();
			for (int i = 0; i < iterations; i++)
			{
				Object obj = serializer.deserialize(array);
				transformer.shallowReverse(obj);
			}
			return iterationTime(System.nanoTime() - start, iterations);
		}
	};

	/**
	 * JVM is not required to honor GC requests, but adding bit of sleep around request is
	 * most likely to give it a chance to do it.
	 */
	private static void doGc()
	{
		try {
			Thread.sleep(50L);
		} catch (InterruptedException ie) {
			System.err.println("Interrupted while sleeping in serializers.BenchmarkRunner.doGc()");
		}
		System.gc();
		try { // longer sleep afterwards (not needed by GC, but may help with scheduling)
			Thread.sleep(200L);
		} catch (InterruptedException ie) {
			System.err.println("Interrupted while sleeping in serializers.BenchmarkRunner.doGc()");
		}
	}

	// ------------------------------------------------------------------------------------

	private static abstract class TestCase
	{
		public abstract <J> double run(Transformer<J,Object> transformer, Serializer<Object> serializer, J value, int iterations) throws Exception;
	}

	private static final class TestCaseRunner<J>
	{
		private final Transformer<J,Object> transformer;
		private final Serializer<Object> serializer;
		private final J value;

		public TestCaseRunner(Transformer<J,Object> transformer, Serializer<Object> serializer, J value)
		{
			this.transformer = transformer;
			this.serializer = serializer;
			this.value = value;
		}

		public double run(TestCase tc, int iterations) throws Exception
		{
			return tc.run(transformer, serializer, value, iterations);
		}

		public double runTakeMin(int trials, TestCase tc, int iterations) throws Exception
		{
			double minTime = Double.MAX_VALUE;
			for (int i = 0; i < trials; i++) {
				double time = tc.run(transformer, serializer, value, iterations);
				minTime = Math.min(minTime, time);
			}
			return minTime;
		}
	}

	enum measurements
	{
		timeCreate, timeSerializeDifferentObjects, timeSerializeSameObject, timeDeserializeNoFieldAccess, timeDeserializeAndCheck, timeDeserializeAndCheckShallow, totalTime, length
	}

	private static <J> EnumMap<measurements, Map<String, Double>> start(int iterations, int trials, long warmupTime, Iterable<TestGroup.Entry<J,Object>> groups, J value) throws Exception
	{
		System.out.printf("%-24s %6s %7s %7s %8s %8s %8s %8s %7s\n",
			"",
			"create",
			"ser",
			"+same",
			"deser",
			"+shal",
			"+deep",
			"total",
			"size");
		EnumMap<measurements, Map<String, Double>> values = new EnumMap<measurements, Map<String, Double>>(measurements.class);
		for (measurements m : measurements.values())
			values.put(m, new HashMap<String, Double>());


		for (TestGroup.Entry<J,Object> entry : groups)
		{
			TestCaseRunner<J> runner = new TestCaseRunner<J>(entry.transformer, entry.serializer, value);

			String name = entry.serializer.getName();

			/*
			 * Should only warm things for the serializer that we test next: HotSpot JIT will
			 * otherwise spent most of its time optimizing slower ones... Use
			 * -XX:CompileThreshold=1 to hint the JIT to start immediately
			 *
			 * Actually: 1 is often not a good value -- threshold is the number
			 * of samples needed to trigger inlining, and there's no point in
			 * inlining everything. Default value is in thousands, so lowering
			 * it to, say, 1000 is usually better.
			 */
			warmCreation(runner, warmupTime);

			doGc();
			double timeCreate = runner.runTakeMin(trials, Create, iterations * 100); // do more iteration for object creation because of its short time

			warmSerialization(runner, warmupTime);

			// actually: let's verify serializer actually works now:
			checkCorrectness(entry.transformer, entry.serializer, value);

			doGc();
			double timeSerializeDifferentObjects = runner.runTakeMin(trials, Serialize, iterations);

			doGc();
			double timeSerializeSameObject = runner.runTakeMin(trials, SerializeSameObject, iterations);

			warmDeserialization(runner, warmupTime);

			doGc();
			double timeDeserializeNoFieldAccess = runner.runTakeMin(trials, Deserialize, iterations);

			doGc();
			double timeDeserializeAndCheckShallow = runner.runTakeMin(trials, DeserializeAndCheckShallow, iterations);

			doGc();
			double timeDeserializeAndCheck = runner.runTakeMin(trials, DeserializeAndCheck, iterations);

			double totalTime = timeSerializeDifferentObjects + timeDeserializeAndCheck;

			byte[] array = entry.serializer.serialize(entry.transformer.forward(value));
			System.out.printf("%-24s %6.0f %7.0f %7.0f %8.0f %8.0f %8.0f %8.0f %7d\n",
				name,
				timeCreate,
				timeSerializeDifferentObjects,
				timeSerializeSameObject,
				timeDeserializeNoFieldAccess,
				timeDeserializeAndCheckShallow,
				timeDeserializeAndCheck,
				totalTime,
				array.length);

			addValue(values, name, timeCreate, timeSerializeDifferentObjects, timeSerializeSameObject,
				timeDeserializeNoFieldAccess, timeDeserializeAndCheckShallow, timeDeserializeAndCheck, totalTime, array.length);
		}
		return values;
	}

	/**
	 * Method that tries to validate correctness of serializer, using
	 * round-trip (construct, serializer, deserialize; compare objects
	 * after steps 1 and 3).
	 * Currently only done for StdMediaDeserializer...
	 */
	private static <J> void checkCorrectness(Transformer<J,Object> transformer, Serializer<Object> serializer, J value)
		throws Exception
	{
		Object specialInput = transformer.forward(value);
		byte[] array = serializer.serialize(specialInput);
		Object specialOutput = serializer.deserialize(array);
		J output = transformer.reverse(specialOutput);

		if (!value.equals(output)) {
			/* Should throw an exception; but for now (that we have a few
							 * failures) let's just whine...
							 */
			String msg = "serializer '"+serializer.getName()+"' failed round-trip test (ser+deser produces Object different from input), input="+value+", output="+output;
			//throw new Exception("Error: "+msg);
			System.err.println("WARN: "+msg);
		}
	}

	private static void printImages(EnumMap<measurements, Map<String, Double>> values)
	{
		for (measurements m : values.keySet()) {
			Map<String, Double> map = values.get(m);
			ArrayList<Map.Entry<String,Double>> list = new ArrayList<Map.Entry<String,Double>>(map.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String,Double>>() {
				public int compare (Map.Entry<String,Double> o1, Map.Entry<String,Double> o2) {
					double diff = o1.getValue() - o2.getValue();
					return diff > 0 ? 1 : (diff < 0 ? -1 : 0);
				}
			});
			LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<String, Double>();
			for (Map.Entry<String, Double> entry : list) {
				if( !entry.getValue().isNaN() ) {
					sortedMap.put(entry.getKey(), entry.getValue());
				}
			}
			if (!sortedMap.isEmpty()) printImage(sortedMap, m);
		}
	}

	private static void printImage(Map<String, Double> map, measurements m)
	{
		StringBuilder valSb = new StringBuilder();
		String names = "";
		double max = Double.MIN_NORMAL;
		for (Map.Entry<String, Double> entry : map.entrySet())
		{
			valSb.append(entry.getValue()).append(',');
			max = Math.max(max, entry.getValue());
			names = entry.getKey() + '|' + names;
		}

		int height = Math.min(30+map.size()*20, 430);
		double scale = max * 1.1;
		System.out.println("<img src='http://chart.apis.google.com/chart?chtt="
			+ m.name()
			+ "&chf=c||lg||0||FFFFFF||1||76A4FB||0|bg||s||EFEFEF&chs=689x"+height+"&chd=t:"
			+ valSb.toString().substring(0, valSb.length() - 1)
			+ "&chds=0,"+ scale
			+ "&chxt=y"
			+ "&chxl=0:|" + names.substring(0, names.length() - 1)
			+ "&chm=N *f*,000000,0,-1,10&lklk&chdlp=t&chco=660000|660033|660066|660099|6600CC|6600FF|663300|663333|663366|663399|6633CC|6633FF|666600|666633|666666&cht=bhg&chbh=10&nonsense=aaa.png'/>");

	}

	private static void addValue(
		EnumMap<measurements, Map<String, Double>> values,
		String name,
		double timeCreate,
		double timeSerializeDifferentObjects,
		double timeSerializeSameObject,
		double timeDeserializeNoFieldAccess,
		double timeDeserializeAndCheckShallow,
		double timeDeserializeAndCheck,
		double totalTime,
		double length)
	{
		// Omit some charts for serializers that are extremely slow.
		if (!name.equals("json/google-gson") && !name.equals("scala")) {
			values.get(measurements.timeSerializeDifferentObjects).put(name, timeSerializeDifferentObjects);
			values.get(measurements.timeSerializeSameObject).put(name, timeSerializeSameObject);
			values.get(measurements.timeDeserializeNoFieldAccess).put(name, timeDeserializeNoFieldAccess);
			values.get(measurements.timeDeserializeAndCheckShallow).put(name, timeDeserializeAndCheckShallow);
			values.get(measurements.timeDeserializeAndCheck).put(name, timeDeserializeAndCheck);
			values.get(measurements.totalTime).put(name, totalTime);
		}
		values.get(measurements.length).put(name, length);
		values.get(measurements.timeCreate).put(name, timeCreate);
	}

	private static <J> void warmCreation(TestCaseRunner<J> runner, long warmupTime) throws Exception
	{
		// Instead of fixed counts, let's try to prime by running for N seconds
		long endTime = System.currentTimeMillis() + warmupTime;
		do
		{
			runner.run(Create, 100);
		}
		while (System.currentTimeMillis() < endTime);
	}

	private static <J> void warmSerialization(TestCaseRunner<J> runner, long warmupTime) throws Exception
	{
		// Instead of fixed counts, let's try to prime by running for N seconds
		long endTime = System.currentTimeMillis() + warmupTime;
		do
		{
			runner.run(Serialize, 100);
		}
		while (System.currentTimeMillis() < endTime);
	}

	private static <J> void warmDeserialization(TestCaseRunner<J> runner, long warmupTime) throws Exception
	{
		// Instead of fixed counts, let's try to prime by running for N seconds
		long endTime = System.currentTimeMillis() + warmupTime;
		do
		{
			runner.run(DeserializeAndCheck, 100);
		}
		while (System.currentTimeMillis() < endTime);
	}
}
