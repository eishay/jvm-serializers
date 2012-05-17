package serializers;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.DeflaterOutputStream;

/**
 * Common base class for various benchmark implementations.
 */
abstract class BenchmarkBase
{
    public final static int DEFAULT_ITERATIONS = 2000;
    public final static int DEFAULT_TRIALS = 500;

    /**
     * Number of milliseconds to warm up for each operation type for each serializer. Let's
     * start with 3 seconds.
     */
    final static long DEFAULT_WARMUP_MSECS = 3000;

    // These tests aren't included by default.  Use the "-hidden" flag to enable them.
    protected static final HashSet<String> HIDDEN = new HashSet<String>();
    static {
            // CKS is not included because it's not really publicly released.
            HIDDEN.add("cks");
            HIDDEN.add("cks-text");
    }

    protected static final String ERROR_DIVIDER = "-------------------------------------------------------------------";

    // ------------------------------------------------------------------------------------
    // Helper classes, enums
    // ------------------------------------------------------------------------------------
    
    public enum measurements
    {
            totalTime("total (nanos)"), timeSerialize("ser (nanos)"), 
            timeDeserialize("deser (nanos)"),
            length("size (bytes)"), lengthDeflate("size+dfl (bytes)"),
				timeCreate("create (nanos)")
            ;

            public final String displayName;

            measurements(String displayName)
            {
                    this.displayName = displayName;
            }
    }

    // Simple container class for config parameters from command-line
    protected final static class Params
    {
        public int iterations = DEFAULT_ITERATIONS;
        public int trials = DEFAULT_TRIALS;
        public long warmupTime = DEFAULT_WARMUP_MSECS;
        public boolean prewarm = true;
        public Boolean filterIsInclude;
        public Set<String> filterStrings;
        public boolean printChart = false;
        public boolean enableHidden = false;

        // Information in input data file:
        public String dataFileName;
        public String dataType; // from first part of file name (comma-separated)
        public String dataExtra; // from second part
        public String dataExtension; // from last part of file name
    }
    

    // ------------------------------------------------------------------------------------
    // Actual benchmark flow
    // ------------------------------------------------------------------------------------

    protected void runBenchmark(String[] args,
            TestCase testCreate,
            TestCase testSerialize, 
            TestCase testDeserialize)
        {
        Params params = new Params();
        findParameters(args, params);
        TestGroups groups = new TestGroups();
        addTests(groups);
        runTests(groups, params,
                testCreate,
                testSerialize, 
                testDeserialize);
    }

    /**
     * Method called to find add actual test codecs
     */
    protected abstract void addTests(TestGroups groups);
    
    protected void findParameters(String[] args, Params params)
    {
        Set<String> optionsSeen = new HashSet<String>();

        for (String arg : args) {
            String remainder;
            if (arg.startsWith("--")) {
                remainder = arg.substring(2);
            }
            else if (arg.startsWith("-")) {
                remainder = arg.substring(1);
            }
            else if (params.dataFileName == null) {
                params.dataFileName = arg;
                continue;
            }
            else {
                System.err.println("Expecting only one non-option argument (<data-file> = \"" + params.dataFileName + "\").");
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
                System.exit(1);
            }
            if (option.equals("include")) {
                if (value == null) {
                    System.err.println("The \"include\" option requires a value.");
                    System.exit(1);
                }
                if (params.filterIsInclude == null) {
                    params.filterIsInclude = true;
                    params.filterStrings = new HashSet<String>(Arrays.asList(value.split(",")));
                } else {
                    System.err.println("Can't use 'include' and 'exclude' options at the same time.");
                    System.exit(1);
                }
            }
            else if (option.equals("exclude")) {
                if (value == null) {
                    System.err.println("The \"exclude\" option requires a value.");
                    System.exit(1);
                }
                if (params.filterIsInclude == null) {
                    params.filterIsInclude = false;
                    params.filterStrings = new HashSet<String>(Arrays.asList(value.split(",")));
                } else {
                    System.err.println("Can't use 'include' and 'exclude' options at the same time.");
                    System.exit(1);
                }   
            }
            else if (option.equals("iterations")) {
                if (value == null) {
                    System.err.println("The \"iterations\" option requires a value.");
                    System.exit(1);
                }
                try {
                    params.iterations = Integer.parseInt(value);
                } catch (NumberFormatException ex) {
                    System.err.println("Invalid value for \"iterations\" option: \"" + value + "\"");
                    System.exit(1);
                }
                if (params.iterations < 1) {
                    System.err.println("Invalid value for \"iterations\" option: \"" + value + "\"");
                    System.exit(1);
                }
            }
            else if (option.equals("trials")) {
                if (value == null) {
                    System.err.println("The \"trials\" option requires a value.");
                    System.exit(1);
                }
                try {
                    params.trials = Integer.parseInt(value);
                } catch (NumberFormatException ex) {
                    System.err.println("Invalid value for \"trials\" option: \"" + value + "\"");
                    System.exit(1);
                }
                if (params.trials < 1) {
                    System.err.println("Invalid value for \"trials\" option: \"" + value + "\"");
                    System.exit(1);
                }
            }
            else if (option.equals("warmup-time")) {
                if (value == null) {
                    System.err.println("The \"warmup-time\" option requires a value.");
                    System.exit(1);
                }
                try {
                    params.warmupTime = Long.parseLong(value);
                } catch (NumberFormatException ex) {
                    System.err.println("Invalid value for \"warmup-time\" option: \"" + value + "\"");
                    System.exit(1);
                }
                if (params.warmupTime < 0) {
                    System.err.println("Invalid value for \"warmup-time\" option: \"" + value + "\"");
                    System.exit(1);
                    }
            }
            else if (option.equals("skip-pre-warmup")) {
                if (value != null) {
                    System.err.println("The \"skip-pre-warmup\" option does not take a value: \"" + arg + "\"");
                    System.exit(1);
                }
                params.prewarm = false;
            }
            else if (option.equals("chart")) {
                if (value != null) {
                    System.err.println("The \"chart\" option does not take a value: \"" + arg + "\"");
                    System.exit(1);
                }
                params.printChart = true;
            }
            else if (option.equals("hidden")) {
                if (value != null) {
                    System.err.println("The \"hidden\" option does not take a value: \"" + arg + "\"");
                    System.exit(1);
                }
                params.enableHidden = true;
            }
            else if (option.equals("help")) {
                if (value != null) {
                    System.err.println("The \"help\" option does not take a value: \"" + arg + "\"");
                    System.exit(1);
                }
                if (args.length != 1) {
                    System.err.println("The \"help\" option cannot be combined with any other option.");
                    System.exit(1);
                }
                System.out.println();
                System.out.println("Usage: run [options] <data-file>");
                System.out.println();
                System.out.println("Options:");
                System.out.println("  -iterations=n         [default=" + DEFAULT_ITERATIONS + "]");
                System.out.println("  -trials=n             [default=" + DEFAULT_TRIALS + "]");
                System.out.println("  -warmup-time=millis   [default=" + DEFAULT_WARMUP_MSECS + "]");
                System.out.println("  -skip-pre-warmup      (don't warm all serializers before the first measurement)");
                System.out.println("  -chart                (generate a Google Chart URL for the results)");
                System.out.println("  -include=impl1,impl2,impl3,...");
                System.out.println("  -exclude=impl1,impl2,impl3,...");
                System.out.println("  -hidden               (enable \"hidden\" serializers)");
                System.out.println("  -help");
                System.out.println();
                System.out.println("Example: run  -chart -include=protobuf,thrift  data/media.1.cks");
                System.out.println();
                System.exit(0);
            }
            else {
                System.err.println("Unknown option: \"" + arg + "\"");
                System.err.println("Use \"-help\" for usage information.");
                System.exit(1);
            }
        }

        if (params.dataFileName == null) {
            System.err.println("Missing <data-file> argument.");
            System.err.println("Use \"-help\" for usage information.");
            System.exit(1);
        }

        // And then let's verify input data file bit more...
        File dataFile = new File(params.dataFileName);
        if (!dataFile.exists()) {
            System.out.println("Couldn't find data file \"" + dataFile.getPath() + "\"");
            System.exit(1);
        }
        String[] parts = dataFile.getName().split("\\.");
        if (parts.length < 3) {
            System.out.println("Data file \"" + dataFile.getName() + "\" should be of the form \"<type>.<name>.<extension>\"");
            System.exit(1);
        }
        params.dataType = parts[0];
        params.dataExtra = parts[1];
        params.dataExtension = parts[parts.length-1];
    }

    /**
     * Method called to run individual test cases
     */
    protected void runTests(TestGroups groups, Params params,
            TestCase testCreate,
            TestCase testSerialize, 
            TestCase testDeserialize)
    {
        TestGroup<?> bootstrapGroup = findGroupForTestData(groups, params);
        Object testData = loadTestData(bootstrapGroup, params);
        Iterable<TestGroup.Entry<Object,Object>> matchingEntries
            = findApplicableTests(groups, params, bootstrapGroup);

        StringWriter errors = new StringWriter();
        PrintWriter errorsPW = new PrintWriter(errors);
        try {
            EnumMap<measurements, Map<String, Double>> values = runMeasurements(errorsPW, params, matchingEntries, testData,
                    testCreate,
                    testSerialize, 
                    testDeserialize
            );
                    
            if (params.printChart) {
                printImages(values);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
            System.exit(1); return;
        }

        // Print errors after chart.  That way you can't miss it.
        String errorsString = errors.toString();
        if (errorsString.length() > 0) {
            System.out.println(ERROR_DIVIDER);
            System.out.println("Errors occurred during benchmarking:");
            System.out.print(errorsString);
            System.exit(1); return;
        }
    }
    
    protected TestGroup<?> findGroupForTestData(TestGroups groups, Params params)
    {
        TestGroup<?> group = groups.groupMap.get(params.dataType);
        if (group == null) {
            System.out.println("Data file \"" + params.dataFileName + "\" can't be loaded.");
            System.out.println("Don't know about data type \"" + params.dataType + "\"");
            System.exit(1);
        }
        return group;
    }

    protected abstract Object convertTestData(TestGroup.Entry<?,Object> loader, Params params, byte[] data)
        throws Exception;

    protected Object loadTestData(TestGroup<?> bootstrapGroup, Params params)
    {
        TestGroup.Entry<?,Object> loader = bootstrapGroup.extensionMap.get(params.dataExtension);
        if (loader == null) {
            System.out.println("Data file \"" + params.dataFileName + "\" can't be loaded.");
            System.out.println("No deserializer registered for data type \"" + params.dataType
                    + "\" and file extension \"." + params.dataExtension + "\"");
            System.exit(1);
        }
        byte[] fileBytes;
        try {
            fileBytes = readFile(new File(params.dataFileName)); // Load entire file into a byte array.
        }
        catch (Exception ex) {
            System.err.println("Error loading data from file \"" + params.dataFileName + "\".");
            System.err.println(ex.getMessage());
            System.exit(1); return null;
        }
        try {
            return convertTestData(loader, params, fileBytes);
        } catch (Exception ex) {
            System.err.println("Error converting test data from file \"" + params.dataFileName + "\".");
            System.err.println(ex.getMessage());
            System.exit(1); return null;
        }
    }
    
    /**
     * Method called to both load in test data and figure out which tests should
     * actually be run, from all available test cases.
     */
    protected Iterable<TestGroup.Entry<Object,Object>> findApplicableTests(TestGroups groups, Params params,
            TestGroup<?> bootstrapGroup)
    {
        @SuppressWarnings("unchecked")
        TestGroup<Object> group_ = (TestGroup<Object>) bootstrapGroup;
        Set<String> matched = new HashSet<String>();

        Iterable<TestGroup.Entry<Object,Object>> available;

        if (params.enableHidden) {
            // Use all of them.
            available = group_.entries;
        } else {
            // Remove the hidden ones.
            ArrayList<TestGroup.Entry<Object,Object>> unhidden = new ArrayList<TestGroup.Entry<Object,Object>>();
            for (TestGroup.Entry<?,Object> entry_ : bootstrapGroup.entries) {
                @SuppressWarnings("unchecked")
                TestGroup.Entry<Object,Object> entry = (TestGroup.Entry<Object,Object>) entry_;
                String name = entry.serializer.getName();
                if (!HIDDEN.contains(name)) unhidden.add(entry);
            }
            available = unhidden;
        }

        if (params.filterStrings == null) {
            return available;
        }
        ArrayList<TestGroup.Entry<Object,Object>> matchingEntries = new ArrayList<TestGroup.Entry<Object,Object>>();

        for (TestGroup.Entry<?,Object> entry_ : available) {
            @SuppressWarnings("unchecked")
            TestGroup.Entry<Object,Object> entry = (TestGroup.Entry<Object,Object>) entry_;
            String name = entry.serializer.getName();
            // See if any of the filters match.
            boolean found = false;
            for (String s : params.filterStrings) {
                boolean thisOneMatches = match(s, name);
                if (thisOneMatches) {
                    matched.add(s);
                    found = true;
                }
            }
            
            if (found == params.filterIsInclude) {
                matchingEntries.add(entry);
            }
        }        
        Set<String> unmatched = new HashSet<String>(params.filterStrings);
        unmatched.removeAll(matched);
        for (String s : unmatched) {
            System.err.println("Warning: there is no implementation name matching the pattern \"" + s + "\"");
            if (!params.enableHidden) {
                for (String hiddenName : HIDDEN) {
                    if (match(s, hiddenName)) {
                        System.err.println("(The \"" + hiddenName + "\", serializer is hidden by default.");
                        System.err.println(" Use the \"-hidden\" option to enable hidden serializers)");
                        break;
                    }
                }
            }
        }
        return matchingEntries;
    }

    protected <J> EnumMap<measurements, Map<String, Double>> runMeasurements(PrintWriter errors,
            Params params, Iterable<TestGroup.Entry<J,Object>> groups, J value,
            TestCase testCreate,
            TestCase testSerialize, 
            TestCase testDeserialize
    ) throws Exception
    {
                // Check correctness first.
                System.out.println("Checking correctness...");
                for (TestGroup.Entry<J,Object> entry : groups)
                {
                        checkCorrectness(errors, entry.transformer, entry.serializer, value);
                }
                System.out.println("[done]");

                // Pre-warm.
                if (params.prewarm) {
                        System.out.print("Pre-warmup...");
                        for (TestGroup.Entry<J,Object> entry : groups)
                        {
                                TestCaseRunner<J> runner = new TestCaseRunner<J>(entry.transformer, entry.serializer, value);
                                String name = entry.serializer.getName();
                                System.out.print(" " + name);

                                warmTest(runner, params.warmupTime, testCreate);
                                warmTest(runner, params.warmupTime, testSerialize);
                        }
                        System.out.println();
                        System.out.println("[done]");
                }

                System.out.printf("%-32s %6s %7s %7s %7s %6s %5s\n",
                        params.printChart ? "\npre." : "",
                        "create",
                        "ser",
                        "deser",
                        "total",
                        "size",
                        "+dfl");
                EnumMap<measurements, Map<String, Double>> values = new EnumMap<measurements, Map<String, Double>>(measurements.class);
                for (measurements m : measurements.values())
                        values.put(m, new HashMap<String, Double>());

                // Actual tests.
                for (TestGroup.Entry<J,Object> entry : groups)
                {
                        TestCaseRunner<J> runner = new TestCaseRunner<J>(entry.transformer, entry.serializer, value);
                        String name = entry.serializer.getName();
                        try {
                                /*
                                 * Should only warm things for the serializer that we test next: HotSpot JIT will
                                 * otherwise spent most of its time optimizing slower ones...
                                 */
                                warmTest(runner, params.warmupTime, testCreate);

                                doGc();
                                double timeCreate = runner.runTakeMin(params.trials, testCreate, params.iterations * 100); // do more iteration for object creation because of its short time

                                warmTest(runner, params.warmupTime, testSerialize);

                                doGc();
                                double timeSerialize = runner.runTakeMin(params.trials, testSerialize, params.iterations);

                                doGc();
                                double timeDeserialize = runner.runTakeMin(params.trials, testDeserialize, params.iterations);

                                double totalTime = timeSerialize + timeDeserialize;

                                byte[] array = serializeForSize(entry.transformer, entry.serializer, value);
                                byte[] compressDeflate = compressDeflate(array);

                                System.out.printf("%-32s %6.0f %7.0f %7.0f %7.0f %6d %5d\n",
                                        name,
                                        timeCreate,
                                        timeSerialize,
                                        timeDeserialize,
                                        totalTime,
                                        array.length,
                                        compressDeflate.length);

                                addValue(values, name, timeCreate, timeSerialize, 
                                        timeDeserialize, totalTime,
                                        array.length, compressDeflate.length);
                        }
                        catch (Exception ex) {
                                System.out.println("ERROR: \"" + name + "\" crashed during benchmarking.");
                                errors.println(ERROR_DIVIDER);
                                errors.println("\"" + name + "\" crashed during benchmarking.");
                                ex.printStackTrace(errors);
                        }
                }

                return values;
    }

    protected abstract <J> byte[] serializeForSize(Transformer<J,Object> tranformer, Serializer<Object> serializer, J value)
        throws Exception;
    
    protected static void addValue(
            EnumMap<measurements, Map<String, Double>> values,
            String name,
            double timeCreate,
            double timeSerialize,
            double timeDeserialize,
            double totalTime,
            double length, double lengthDeflate)
    {
        values.get(measurements.timeSerialize).put(name, timeSerialize);
        values.get(measurements.timeDeserialize).put(name, timeDeserialize);
        values.get(measurements.totalTime).put(name, totalTime);
        values.get(measurements.length).put(name, length);
        values.get(measurements.lengthDeflate).put(name, lengthDeflate);
        values.get(measurements.timeCreate).put(name, timeCreate);
    }
    
    // ------------------------------------------------------------------------------------
    // Helper methods for test warmup
    // ------------------------------------------------------------------------------------
    
    protected <J> void warmTest(TestCaseRunner<J> runner, long warmupTime, TestCase test) throws Exception
    {
        // Instead of fixed counts, let's try to prime by running for N seconds
        long endTime = System.currentTimeMillis() + warmupTime;
        do {
            runner.run(test, 10);
        }
        while (System.currentTimeMillis() < endTime);
    }
    
    // ------------------------------------------------------------------------------------
    // Helper methods, validation,  result graph generation
    // ------------------------------------------------------------------------------------

    /**
     * Method that tries to validate correctness of serializer, using
     * round-trip (construct, serializer, deserialize; compare objects
     * after steps 1 and 3).
     */
    protected abstract <J> void checkCorrectness(PrintWriter errors, Transformer<J,Object> transformer,
            Serializer<Object> serializer, J value)
        throws Exception;
    
    protected <J> void checkSingleItem(PrintWriter errors, Transformer<J,Object> transformer,
            Serializer<Object> serializer, J value)
        throws Exception
    {
        Object specialInput;
        String name = serializer.getName();
        try {
            specialInput = transformer.forward(value);
        }
        catch (Exception ex) {
            System.out.println("ERROR: \"" + name + "\" crashed during forward transformation.");
            errors.println(ERROR_DIVIDER);
            errors.println("\"" + name + "\" crashed during forward transformation.");
            ex.printStackTrace(errors);
            return;
        }

        byte[] array;
        try {
            array = serializer.serialize(specialInput);
        }
        catch (Exception ex) {
      	  ex.printStackTrace();
            System.out.println("ERROR: \"" + name + "\" crashed during serialization.");
            errors.println(ERROR_DIVIDER);
            errors.println("\"" + name + "\" crashed during serialization.");
            ex.printStackTrace(errors);
            return;
        }

        Object specialOutput;

        try {
            specialOutput = serializer.deserialize(array);
        }
        catch (Exception ex) {              
            System.out.println("ERROR: \"" + name + "\" crashed during deserialization.");
            errors.println(ERROR_DIVIDER);
            errors.println("\"" + name + "\" crashed during deserialization.");
            ex.printStackTrace(errors);
            return;
        }

        J output;
        try {
            output = transformer.reverse(specialOutput);
        }
        catch (Exception ex) {
            System.out.println("ERROR: \"" + name + "\" crashed during reverse transformation.");
            errors.println(ERROR_DIVIDER);
            errors.println("\"" + name + "\" crashed during reverse transformation.");
            ex.printStackTrace(errors);
            return;
        }
        if (!value.equals(output)) {
            System.out.println("ERROR: \"" + name + "\" failed round-trip check.");
            errors.println(ERROR_DIVIDER);
            errors.println("\"" + name + "\" failed round-trip check.");
            errors.println("ORIGINAL:  " + value);
            errors.println("ROUNDTRIP: " + output);

            System.err.println("ORIGINAL:  " + value);
            System.err.println("ROUNDTRIP: " + output);
                    
        }
    }
    
    // ------------------------------------------------------------------------------------
    // Helper methods, result graph generation
    // ------------------------------------------------------------------------------------
    
    protected static void printImages(EnumMap<measurements, Map<String, Double>> values)
    {
        System.out.println();
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
        System.out.println();
    }

    protected static void printImage(Map<String, Double> map, measurements m)
    {
        StringBuilder valSb = new StringBuilder();
        String names = "";
        double max = Double.MIN_NORMAL;
        for (Map.Entry<String, Double> entry : map.entrySet())
        {
            double value = entry.getValue();
            valSb.append((int) value).append(',');
            max = Math.max(max, entry.getValue());
            names = urlEncode(entry.getKey()) + '|' + names;
        }
        
        int headerSize = 30;
        int maxPixels = 300 * 1000; // Limit set by Google's Chart API.
        int maxHeight = 600;
        int width = maxPixels / maxHeight;

        int barThickness = 10;
        int barSpacing = 10;

        int height;

        // Reduce bar thickness and spacing until we can fit in the maximum height.
        while (true) {
            height = headerSize + map.size()*(barThickness + barSpacing);
            if (height <= maxHeight) break;
            barSpacing--;
            if (barSpacing == 1) break;
            height = headerSize + map.size()*(barThickness + barSpacing);
            if (height <= maxHeight) break;
            barThickness--;
            if (barThickness == 1) break;
        }

        boolean truncated = false;
        if (height > maxHeight) {
            truncated = true;
            height = maxHeight;
        }

        double scale = max * 1.1;
        System.out.println("<img src='https://chart.googleapis.com/chart?chtt="
                + urlEncode(m.displayName)
                + "&chf=c||lg||0||FFFFFF||1||76A4FB||0|bg||s||EFEFEF&chs="+width+"x"+height+"&chd=t:"
                + valSb.toString().substring(0, valSb.length() - 1)
                + "&chds=0,"+ scale
                + "&chxt=y"
                + "&chxl=0:|" + names.substring(0, names.length() - 1)
                + "&chm=N *f*,000000,0,-1,10&lklk&chdlp=t&chco=660000|660033|660066|660099|6600CC|6600FF|663300|663333|663366|663399|6633CC|6633FF|666600|666633|666666&cht=bhg&chbh=" + barThickness + ",0," + barSpacing + "&nonsense=aaa.png'/>");

        if (truncated) {
            System.err.println("WARNING: Not enough room to fit all bars in chart.");
        }
    }
    
    // ------------------------------------------------------------------------------------
    // Static helper methods
    // ------------------------------------------------------------------------------------

    protected static double iterationTime(long delta, int iterations)
    {
        return (double) delta / (double) (iterations);
    }

    protected static String urlEncode(String s)
    {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    // JVM is not required to honor GC requests, but adding bit of sleep around request is
    // most likely to give it a chance to do it.
    protected static void doGc()
    {
        try {
            Thread.sleep(50L);
        } catch (InterruptedException ie) {
            System.err.println("Interrupted while sleeping in serializers.BenchmarkBase.doGc()");
        }
        System.gc();
        try { // longer sleep afterwards (not needed by GC, but may help with scheduling)
            Thread.sleep(200L);
        } catch (InterruptedException ie) {
            System.err.println("Interrupted while sleeping in serializers.BenchmarkBase.doGc()");
        }
    }
    
    protected static byte[] readFile(File file) throws IOException
    {
            FileInputStream fin = new FileInputStream(file);
            try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
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

    protected static byte[] compressDeflate(byte[] data)
    {
            try {
                    ByteArrayOutputStream bout = new ByteArrayOutputStream(500);
                    DeflaterOutputStream compresser = new DeflaterOutputStream(bout);
                    compresser.write(data, 0, data.length);
                    compresser.finish();
                    compresser.flush();
                    return bout.toByteArray();
            }
            catch (IOException ex) {
                    AssertionError ae = new AssertionError("IOException while writing to ByteArrayOutputStream!");
                    ae.initCause(ex);
                    throw ae;
            }
    }
    protected static boolean match(String pattern, String name)
    {
            StringBuilder regex = new StringBuilder();

            while (pattern.length() > 0) {
                    int starPos = pattern.indexOf('*');
                    if (starPos < 0) {
                            regex.append(Pattern.quote(pattern));
                            break;
                    }
                    else {
                            String beforeStar = pattern.substring(0, starPos);
                            String afterStar = pattern.substring(starPos + 1);

                            regex.append(Pattern.quote(beforeStar));
                            regex.append(".*");
                            pattern = afterStar;
                    }
            }

            return Pattern.matches(regex.toString(), name);
    }

}
