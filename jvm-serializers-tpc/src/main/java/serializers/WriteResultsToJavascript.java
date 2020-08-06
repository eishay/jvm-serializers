package serializers;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.*;
import java.util.*;

/**
 * Run this via the "./mk-js-stats" script.
 */
public class WriteResultsToJavascript
{
    public static final String[] columns = { "create", "ser", "deser", "total", "size", "size-gz" };
    public static final int roundTripColumnIndex = 3;

    public static void _main(String[] args)
        throws Exit
    {
        if (args.length != 2) {
            throw new Exit(1, "Usage: COMMAND <input-stats.txt> <output-stats.js>");
        }

        File inputFile = new File(args[0]);
        File outputFile = new File(args[1]);


        List<Entry> entries = readInputFile(inputFile);
        loadEntryProperties(entries);
        calculateSpeedProperties(entries);
        writeOutputFile(outputFile, entries);
    }

    // ----------------------------------------------------------------------------
    // Lookup/derive "properties" for each serializer.

    private static void calculateSpeedProperties(List<Entry> entries)
    {
        // Get a sorted list of all running times.
        int[] times = new int[entries.size()];
        int i = 0;
        for (Entry e : entries) {
            times[i++] = e.results.get(roundTripColumnIndex);
        }
        Arrays.sort(times);

        // TODO: something smarter than percentiles.
        int p50 = times[(times.length + 1) / 2];
        int p75 = times[(times.length * 3) / 4];

        for (Entry e : entries) {
            int time = e.results.get(roundTripColumnIndex);
            if (time <= p50) {
                e.properties.add(Property.Fast);
            }
            if (time > p75) {
                e.properties.add(Property.Slow);
            }
        }
    }

    private static void loadEntryProperties(List<Entry> entries)
        throws Exit
    {
        Map<String,SerFeatures> featureMap = new BenchmarkExporter().getFeatureMap();

        for (Entry e : entries) {
            SerFeatures f = featureMap.get(e.name);
            if (f == null) {
                throw new Exit(1, "Unable to load serializer features for \"" + e.name + "\".",
                                  "Are you sure this serializer name is still valid in the benchmark code?");
            }
            switch (f.format) {
                case BINARY:
                    e.properties.add(Property.Binary);
                    break;
                case BIN_CROSSLANG:
                    e.properties.add(Property.Binary);
                    e.properties.add(Property.Neutral);
                    break;
                case XML:
                    e.properties.add(Property.Xml);
                    e.properties.add(Property.Text);
                    e.properties.add(Property.Neutral);
                    break;
                case JSON:
                    e.properties.add(Property.Json);
                    e.properties.add(Property.Text);
                    e.properties.add(Property.Neutral);
                    break;
            }

            // TODO: Clean up the Category.Spec stuff.
        }
    }

    // ----------------------------------------------------------------------------
    // Write output file.

    private static void writeOutputFile(File outputFile, List<Entry> entries)
        throws Exit
    {
        // Write output.
        try {
            FileOutputStream fout = new FileOutputStream(outputFile);
            try {
                JsonFactory factory = new JsonFactory();
                factory.disable(JsonGenerator.Feature.QUOTE_FIELD_NAMES);
                JsonGenerator gen = factory.createGenerator(fout, JsonEncoding.UTF8);
                gen.useDefaultPrettyPrinter();
                fout.write("var benchmarkResults = ".getBytes("UTF-8"));
                writeJavascriptStats(gen, columns, entries);
            }
            finally {
                fout.close();
            }
        }
        catch (IOException ex) {
            throw new Exit(1, "Error writing to output file \"" + outputFile.getPath() + "\": " + ex.getMessage());
        }
    }

    private static void writeJavascriptStats(JsonGenerator g, String[] columns, List<Entry> entries)
        throws IOException
    {
        g.writeStartObject();

        // Write properties (grouped by category).
        g.writeArrayFieldStart("categories");
        for (Category category : Category.values()) {
            g.writeStartObject();

            g.writeStringField("name", category.ident);
            g.writeStringField("description", category.description);
            g.writeArrayFieldStart("properties");
            for (Property property : category.properties) {
                g.writeStartObject();
                g.writeStringField("name", property.ident);
                g.writeStringField("description", property.description);
                g.writeEndObject();
            }
            g.writeEndArray();

            g.writeEndObject();
        }
        g.writeEndArray();

        // Write columns.
        g.writeArrayFieldStart("columns");
        for (String column : columns) {
            g.writeString(column);
        }
        g.writeEndArray();

        // Write entries.
        g.writeArrayFieldStart("entries");
        for (Entry entry : entries) {
            g.writeStartObject();
            {
                g.writeStringField("name", entry.name);
                g.writeArrayFieldStart("properties");
                for (Property property : entry.properties) {
                    g.writeString(property.ident);
                }
                g.writeEndArray();
                g.writeArrayFieldStart("results");
                for (int i : entry.results) {
                    g.writeNumber(i);
                }
                g.writeEndArray();
            }
            g.writeEndObject();
        }
        g.writeEndArray();

        g.writeEndObject();

        g.flush();
    }

    // ----------------------------------------------------------------------------
    // Read input file.

    private static List<Entry> readInputFile(File inputFile) throws Exit
    {
        if (!inputFile.exists()) {
            throw new Exit(1, "Input file \"" + inputFile.getPath() + "\" doesn't exist.");
        }
        if (!inputFile.isFile()) {
            throw new Exit(1, "Input file \"" + inputFile.getPath() + "\" doesn't refer to a normal file.");
        }

        try {
            FileInputStream fin = new FileInputStream(inputFile);
            try {
                return readStats(new InputStreamReader(fin, "UTF-8"));
            }
            finally {
                try { fin.close(); } catch (IOException ex) { /* ignore */ }
            }
        }
        catch (InputError ex) {
            throw new Exit(1, "Error in input file \"" + inputFile.getPath() + "\": " + ex.getMessage());
        }
        catch (IOException ex) {
            throw new Exit(1, "Error reading from input file \"" + inputFile.getPath() + "\": " + ex.getMessage());
        }
    }

    private static List<Entry> readStats(Reader in0)
        throws IOException, InputError
    {
        BufferedReader in = new BufferedReader(in0);
        ArrayList<Entry> entries = new ArrayList<Entry>();
        Set<String> entryNames = new HashSet<String>();
        int lineNumber = 0;
        int tokensPerLine = columns.length + 1;
        while (true) {
            String line = in.readLine();
            if (line == null) break;
            lineNumber++;
            line = line.trim();
            if (line.length() == 0) continue;  // skip blank lines.

            String[] parts = line.split(" +");
            if (parts.length != tokensPerLine) {
                throw new InputError(lineNumber, "Expecting " + tokensPerLine + " tokens, got " + parts.length + ".");
            }
            String name = parts[0];

            boolean added = entryNames.add(name);
            if (!added) {
                throw new InputError(lineNumber, "Duplicate row name: \"" + name + "\"");
            }

            Entry entry = new Entry(name);
            entries.add(entry);

            for (int i = 0; i < columns.length; i++) {
                int ci = i+1;
                String c = parts[ci];
                int v;
                try {
                    v = Integer.parseInt(c);
                }
                catch (NumberFormatException ex) {
                    throw new InputError(lineNumber, "column " + (ci+1) + ": Expecting integer, got \"" + c + "\".");
                }
                if (v < 0) {
                    throw new InputError(lineNumber, "column " + (ci+1) + ": Negatives not allowed.");
                }
                entry.results.add(v);
            }
        }

        if (entries.isEmpty()) {
            throw new InputError(lineNumber, "No valid rows found.");
        }

        return entries;
    }

    public static final class InputError extends Exception
    {
        public InputError(int lineNumber, String message) {
            super("line " + lineNumber + ": " + message);
        }
    }

    // ----------------------------------------------------------------------------
    // Model objects

    public enum Category
    {
        Format("Format", "The format used to serialize."),
        Spec("Spec", "How you specify the data structure to the serializer."),
        PojoHints("POJO Hints", "For POJO serializers, did we specify any hints to help them out?"),
        Speed("Speed", null),
        ;

        public final String ident;
        public final String description;
        public final List<Property> properties;

        Category(String ident, String description)
        {
            this.ident = ident;
            this.description = description;
            this.properties = new ArrayList<Property>();
        }
    }

    public enum Property
    {
        Neutral(Category.Format, "neutral", "The serialization format is language-neutral."),
        Binary (Category.Format, "binary", "Some binary format."),
        Text   (Category.Format, "text", "Some human-readable text format."),
        Json   (Category.Format, "json", null),
        Xml    (Category.Format, "xml", null),

        Pojo      (Category.Spec, "pojo", "The serializer works with user-defined plain Java classes."),
        Schema    (Category.Spec, "schema", "You must define a schema for your data types and run the library's compiler to generate Java classes."),
        Manual    (Category.Spec, "manual", "You must call the serialization routines manually."),
        ManualMore(Category.Spec, "manual.more", "You must call the serialization routines manually, and there is more effort than in the \"manual\" case."),

        HintType(Category.PojoHints, "hint.type", "The serializer was given the list of types."),
        HintNull(Category.PojoHints, "hint.null", "The serializer was given the list of non-null fields."),

        Fast(Category.Speed, "fast", "Serializers whose round-trip time is in the top 50th percentile."),
        Slow(Category.Speed, "slow", "Serializers whose round-trip time is in the bottom 25th percentile."),
        ;

        public final Category category;
        public final String ident;
        public final String description;

        Property(Category category, String ident, String description)
        {
            this.ident = ident;
            this.category = category;
            this.description = description;
            category.properties.add(this);
        }

        static {
            HashSet<String> idents = new HashSet<String>();
            for (Property p : Property.values()) {
                boolean added = idents.add(p.ident);
                if (!added) {
                    throw new AssertionError("duplicate identifier \"" + p.ident + "\"");
                }
            }
        }
    }

    public static final class Entry
    {
        public final String name;
        public final EnumSet<Property> properties = EnumSet.noneOf(Property.class);
        public final List<Integer> results = new ArrayList<Integer>();

        public Entry(String name)
        {
            this.name = name;
        }
    }

    // ----------------------------------------------------------------------------
    // Command-line app boilerplate.

    public static void main(String[] args)
    {
        int exitCode = 0;
        try {
            _main(args);
        }
        catch (Exit exit) {
            for (String line : exit.errorLines) {
                System.err.println(line);
            }
            exitCode = exit.code;
        }
        System.exit(exitCode);
    }

    public static final class Exit extends Exception
    {
        public final int code;
        public final String[] errorLines;

        public Exit(int code, String... errorLines)
        {
            this.code = code;
            this.errorLines = errorLines;
        }
    }
}
