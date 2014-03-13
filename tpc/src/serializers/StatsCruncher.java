package serializers;

/**
 * Copyright (c) 2012, Ruediger Moeller. All rights reserved.
 * <p/>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 * <p/>
 * Date: 08.03.14
 * Time: 22:08
 * To change this template use File | Settings | File Templates.
 */

import java.io.*;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.*;

/**
 * smallish program to read in the stats created by isolated runs (see new runscripts)
 * <p/>
 * 1. run-script runs each test in an isolated VM
 * 2. the stats script collects result to stats.txt and runs this class to produce various charts from the bench data
 * <p/>
 * this helps in filtering out outliers / modify charts without having to run the tests
 * <p/>
 * assumes to be run in the same working dir as the bash run script
 */
public class StatsCruncher {

    public static final int MAX_CHART_BARS = 18;
    HashMap<String, SerFeatures> mappedFeatures = new HashMap<>();
    HashMap<String, TestCaseResult> mappedResults;
    List<TestCaseResult> resultList;

    static SerFormat BIN = SerFormat.BINARY;
    static SerFormat BIN_CL = SerFormat.BIN_CROSSLANG;
    static SerFormat JSON = SerFormat.JSON;
    static SerFormat XML = SerFormat.XML;
    static SerFormat MISC = SerFormat.MISC;
    
    public StatsCruncher() {
        mappedFeatures = new BenchMarkExporter().getFeatureMap();
    }
    
    // ruediger: funny i am falling back to old school text parsing with +20 serializers on board ;-)
    void readStats() throws IOException {
        FileInputStream in = new FileInputStream("./stats.txt");
        BufferedReader din = new BufferedReader(new InputStreamReader(in));

        mappedResults = new HashMap<>();
        resultList = new ArrayList<>();

        String line;
        while ((line = din.readLine()) != null) {
            String split[] = line.split("\\s+");
            if (split == null || split.length < 2)
                continue;
            String testCase = split[0].trim();
            if ( split[1].trim().length() == 0 || !Character.isDigit(split[1].trim().charAt(0))) {
                continue;
            }
            TestCaseResult res = new TestCaseResult();
            res.setName(split[0].trim());
            res.setCreate(Integer.parseInt(split[1].trim()));
            res.setSer(Integer.parseInt(split[2].trim()));
            res.setDeser(Integer.parseInt(split[3].trim()));
            res.setTotal(Integer.parseInt(split[4].trim()));
            res.setSize(Integer.parseInt(split[5].trim()));
            res.setCompressedSize(Integer.parseInt(split[6].trim()));
            mappedResults.put(res.getName(), res);
            res.setFeatures(mappedFeatures.get(res.getName()));
            resultList.add(res);
        }
    }

    public int max(List<TestCaseResult> resultList, String arg, int elems) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < Math.min(elems,resultList.size()); i++) {
            TestCaseResult testCaseResult = resultList.get(i);
            max = Math.max(testCaseResult.getInt(arg),max);
        }
        return max;
    }
    
    public int min(List<TestCaseResult> resultList, String arg) {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < resultList.size(); i++) {
            TestCaseResult testCaseResult = resultList.get(i);
            min = Math.min(testCaseResult.getInt(arg),min);
        }
        return min;
    }

    public String generateChart(List<TestCaseResult> resultList, String title, String lowerValueName, String higherValueName) {
        int chartSize = Math.min(MAX_CHART_BARS, resultList.size() ); // more bars aren't possible with gcharts
        int max = max(resultList, higherValueName, MAX_CHART_BARS);
        String res = "https://chart.googleapis.com/chart?cht=bhs&chs=600x"+(chartSize *20+14); // html: finally a device independent technology
//        res+="&chtt="+URLEncoder.encode(title);
        res+="&chd=t:";
        for (int i = 0; i < chartSize; i++) {
            TestCaseResult testCaseResult = resultList.get(i);
            int val = testCaseResult.getInt(lowerValueName);
            res+= val +((i< chartSize -1) ? ",":"|");
        }
        for (int i = 0; i < chartSize; i++) {
            TestCaseResult testCaseResult = resultList.get(i);
            int valLower = (testCaseResult.getInt(lowerValueName));
            int val = testCaseResult.getInt(higherValueName);
            val -= valLower;
            res+=val+((i< chartSize -1) ? ",":"");
        }
        res += "&chco=5d99f9,4d89f9";
        res += "&chdlp=t";
        res += "&chbh=15";
        res += "&chds=0,"+max;
        res += "&chxr=1,0,"+max;
        res += "&chxt=y,x&chxl=0:|";
        for (int i = 0; i < chartSize; i++) {
            TestCaseResult testCaseResult = resultList.get(chartSize -i-1);
            res+=URLEncoder.encode(testCaseResult.getName())+((i< chartSize -1) ? "|":"");
        }
        return "<b>"+title+"</b><br><img src='"+res+"'/>";
    }

    /**
     * 
     * @param commaSeparatedNames - * for all
     * @param intFieldToSort - result field name to sort for (create ser deser total size compressedSize) 
     * @param maxRangeDiff - max distance of min value (remove outliers)
     * @param maxListLen - max number of entries in result
     * @return
     */
    public List<TestCaseResult> generateChartList(String commaSeparatedNames, final String intFieldToSort, int maxRangeDiff, int maxListLen, SerFeatures featureFilter) 
    {
        String toChart[] = commaSeparatedNames.split(",");
        List<TestCaseResult> chartList = new ArrayList<>();
        if ( commaSeparatedNames.equals("*") ) {
            chartList.addAll(resultList);            
        } else {
            for (int i = 0; i < toChart.length; i++) {
                toChart[i] = toChart[i].trim();
                TestCaseResult res = mappedResults.get(toChart[i]);
                if ( res == null ) {
                    System.out.println("Cannot chart "+toChart[i]+" (not found in stats)");
                } else {
                    chartList.add(res);
                }
            }
        }
        chartList = sort(intFieldToSort, chartList);
        // process feature filter (I am in dirt mode here ..)
        for (int i = 0; i < chartList.size(); i++) {
            TestCaseResult testCaseResult = chartList.get(i);
            SerFeatures feature = testCaseResult.getFeatures();
            if ( feature != null ) {
                 if ( 
                        ( featureFilter.getClz() != null && ! featureFilter.getClz().equals(feature.getClz()) ) ||
                        ( featureFilter.getFormat() != null && ! featureFilter.getFormat().equals(feature.getFormat()) ) ||
                        ( featureFilter.getGraph() != null && ! featureFilter.getGraph().equals(feature.getGraph()) )
                    )    
                 {
                    chartList.remove(i--);    
                 }
            } else {
                System.out.println("feature is unexpectedly null for "+testCaseResult.getName());
                chartList.remove(i--);
            }
        }
        
        // process rangeDiff and maxLen
        int min = min(chartList,intFieldToSort);
        for (int i = 0; i < chartList.size(); i++) {
            TestCaseResult testCaseResult = chartList.get(i);
            if ( min * maxRangeDiff < testCaseResult.getInt(intFieldToSort) || i >= maxListLen )
                chartList.remove(i--);
        }
        return chartList;
    }

    protected List<TestCaseResult> sort(final String intFieldToSort, List<TestCaseResult> chartList) {
        List<TestCaseResult> res = new ArrayList<>(chartList);
        Collections.sort(res, new Comparator<TestCaseResult>() {
            @Override
            public int compare(TestCaseResult o1, TestCaseResult o2) {
                return o1.getInt(intFieldToSort) - o2.getInt(intFieldToSort);
            }
        });
        return res;
    }

    String dump(List<TestCaseResult> total) {
        String res = "\npre.                                    create     ser   deser   total   size  +dfl\n";
        for (int i = 0; i < total.size(); i++) {
            TestCaseResult testCaseResult = total.get(i);
            res+=testCaseResult.toString()+"\n";
        }
        return res+"\n";
    }

    String dumpFeatures(List<TestCaseResult> total) {
        String res = "\npre.                                    Effort          Format         Structure  Misc\n";
        for (int i = 0; i < total.size(); i++) {
            TestCaseResult testCaseResult = total.get(i);
            res+=testCaseResult.getFeatures().toString(testCaseResult.getName())+"\n";
        }
        return res+"\n";
    }

    String generateResultSection(List<TestCaseResult> testCaseResults, String title, String desc) {
        return  "\n\n<h3>"+title+"</h3>\n"+desc+"\n"+
                generateChart(testCaseResults,"Ser Time+Deser Time (ns)","ser","total")+"\n"+
                generateChart( sort("size",testCaseResults),"Size, Compressed [light] in bytes","compressedSize","size")+"\n"+
                dump(testCaseResults);
    }

    //////////////////////////////////////////////////////////////////////////////////


    // i just keep everything in code .. configfiles would add additional complexity and a query language ..
    // the report is built to be displayed with Textile
    public static void main(String arg[]) throws IOException, URISyntaxException {
        StatsCruncher statsCruncher = new StatsCruncher();
        statsCruncher.readStats();
        
        

//        PrintStream out = System.out;
        PrintStream out = new PrintStream(new FileOutputStream("report.textile"));

        out.println("<b>Test Platform</b>");
        out.println("OS:"+System.getProperty("os.name"));
        out.println("JVM:"+System.getProperty("java.vendor")+" "+System.getProperty("java.version"));
        out.println("CPU:"+System.getenv("PROCESSOR_IDENTIFIER")+" os-arch:"+System.getenv("PROCESSOR_ARCHITECTURE"));
        out.println("Cores (incl HT):"+Runtime.getRuntime().availableProcessors());
        out.println();

        out.println("<b>Disclamer</b>\n" +
                "\n" +
                "This test focusses on en/decoding of a cyclefree data structure, but the featureset of the libraries compared differs a lot:\n" +
                "* some serializers support cycle detection/object sharing others just write non-cyclic tree structures\n" +
                "* some include full metadata in serialized output, some don't \n" +
                "* some are cross platform, some are language specific \n" +
                "* some are text based, some are binary, \n" +
                "* some support versioning forward/backward, both, some don't \n\n" +
                "(See \"ToolBehavior\":wiki/ToolBehavior)\n" +
                "Other test data will yield different results (e.g. adding a non ascii char to every string :-) ). However the results give a raw estimation of library performance."
        );
        
        // first chart (like in previous tests)
        // fastest of flat serializers (with or without preparation, exclude manually optimized)
        List<TestCaseResult> all = statsCruncher.generateChartList("*", "total", 1000, 1000, new SerFeatures(null, null, null));
        for (int i = 0; i < all.size(); i++) {
            TestCaseResult testCaseResult = all.get(i);
            if ( 
                    testCaseResult.getFeatures().getGraph() == SerGraph.FULL_GRAPH     // exclude full serializers
                 || testCaseResult.getFeatures().getClz() == SerClass.MANUAL_OPT  // exclude manually optimized
                 || ",kryo-flat,fst-flat,protobuf/protostuff,protostuff-runtime,protobuf/protostuff-runtime,"
                            .indexOf(","+testCaseResult.getName()+",") >= 0 // prevent some libs to contribute twice to chart
               ) {
                all.remove(i--);
            }
        }
        String desc = "Benchmarks serializers \n" +
                "* Only cycle free tree structures. An object referenced twice will be serialized twice. \n" +
                "* no manual optimizations. \n" +
                "* schema is known in advance (pre registration or even class generation). (Not all might make use of that) \n";
        out.println( statsCruncher.generateResultSection(all, "Serializers (no shared refs)", desc ) );

        
        // Second chart
        // plain vanilla Full Graph serializers without generation/preparation
        all = statsCruncher.generateChartList("*", "total", 1000, 1000, new SerFeatures(null, null, null));
        for (int i = 0; i < all.size(); i++) {
            TestCaseResult testCaseResult = all.get(i);
            if (
                    testCaseResult.getFeatures().getGraph() != SerGraph.FULL_GRAPH     // exclude full serializers
                    || testCaseResult.getFeatures().getClz() == SerClass.MANUAL_OPT  // exclude manually optimized
                    || ",,".indexOf(","+testCaseResult.getName()+",") >= 0 // prevent some libs to contribute twice to chart
                    ) {
                all.remove(i--);
            }
        }

        desc = 
                "Contains serializer(-configurations) \n"+
                "* supporting full object graph write/read. Object graph may contain cycles. If an Object is referenced twice, it will be so after deserialization.\n"+
                "* nothing is known in advance, no class generation, no preregistering of classes. Everything is captured at runtime using e.g. reflection.\n"+
                "* note this usually cannot be used cross language, however JSON/XML formats may enable cross language deserialization.\n"
                ;
        out.println( statsCruncher.generateResultSection(all, "Full Object Graph Serializers",desc) );

        // 3rd chart
        // Cross language binary serializer 
        all = statsCruncher.generateChartList("*", "total", 1000, 1000, new SerFeatures(null, null, null));
        for (int i = 0; i < all.size(); i++) {
            TestCaseResult testCaseResult = all.get(i);
            if (
                    testCaseResult.getFeatures().getFormat() != SerFormat.BIN_CROSSLANG 
                    || testCaseResult.getFeatures().getClz() == SerClass.MANUAL_OPT  // exclude manually optimized
                    || ",,".indexOf(","+testCaseResult.getName()+",") >= 0 // prevent some libs to contribute twice to chart
                ) {
                all.remove(i--);
            }
        }
        desc =
                "Contains serializer(-configurations) \n"+
                "* Only cycle free tree structures. An object referenced twice will be serialized twice. \n" +
                "* schema is known in advance (pre registration, intermediate message description languages, class generation).\n"
        ;
        out.println( statsCruncher.generateResultSection(all, "Cross Lang Binary Serializers", desc) );

        // 4th chart
        // JSon+XML 
        all = statsCruncher.generateChartList("*", "total", 1000, 1000, new SerFeatures(null, null, null));
        for (int i = 0; i < all.size(); i++) {
            TestCaseResult testCaseResult = all.get(i);
            if (
                    (testCaseResult.getFeatures().getFormat() != SerFormat.JSON
                     && testCaseResult.getFeatures().getFormat() != SerFormat.XML)
                    || testCaseResult.getFeatures().getClz() == SerClass.MANUAL_OPT  // exclude manually optimized
                    || ",,".indexOf(","+testCaseResult.getName()+",") >= 0 // prevent some libs to contribute twice to chart
            ) 
            {
                all.remove(i--);
            }
        }
        desc =
                "* text format based. Usually can be read by anybody. Frequently inline schema inside data.\n"+
                "* Mixed regarding required preparation, object graph awareness (references). \n"
        ;
        out.println( statsCruncher.generateResultSection(all, "XML/JSon Serializers", desc) );

        // Manually optimized 
        all = statsCruncher.generateChartList("*", "total", 1000, 1000, new SerFeatures(null, null, null));
        for (int i = 0; i < all.size(); i++) {
            TestCaseResult testCaseResult = all.get(i);
            if (
                    testCaseResult.getFeatures().getClz() != SerClass.MANUAL_OPT  // exclude NON manually optimized
                    || ",,".indexOf(","+testCaseResult.getName()+",") >= 0 // prevent some libs to contribute twice to chart
                )
            {
                all.remove(i--);
            }
        }

        desc = "all flavours of manually optimized serializers. Handcoded and hardwired to exactly the benchmark's message structures.\n"+
                "* illustrates what's possible, at what level generic approaches can be optimized in case\n";
        
        out.println( statsCruncher.generateResultSection(all, "Manually optimized Serializers", desc) );

        // Cost of miscFeatures 
        all = statsCruncher.generateChartList("*", "total", 1000, 1000, new SerFeatures(null, null, null));
        for (int i = 0; i < all.size(); i++) {
            TestCaseResult testCaseResult = all.get(i);
            if (
                  ",fst,fst-flat,fst-flat-pre,kryo-manual,kryo-serializer,kryo-flat,kryo-flat-pre,protostuff,protostuff-runtime,protostuf-manual,msgpack-databind,msgpack-manual,"
                          .indexOf(","+testCaseResult.getName()+",") < 0 // only these
               )
            {
                all.remove(i--);
            }
        }
        desc = "shows performance vs convenience of manually-selected libs.\n"+
                "* cycle free, schema known at compile time, manual optimization: kryo-manual, msgpack-manual\n"+
                "* cycle free, schema known at compile time: protostuff, fst-flat-pre, kryo-flat-pre. (note: protostuff uses class generation while the other two just require a list of classes to be written) \n" +
                "* cycle free, schema UNKNOWN at compile time: fst-flat, kryo-flat, protostuff-runtime, msgpack-databind \n"+
                "* full object graph awareness, schema UNKNOWN at compile time: fst, kryo.\n";
        out.println( statsCruncher.generateResultSection(all, "Cost of features",desc) );

        all = statsCruncher.generateChartList("*", "total", 1000, 1000, new SerFeatures(null, null, null));
        out.println("<h3>Full data</h3>");
        out.println(statsCruncher.dump(all));
        out.println();
        out.println(statsCruncher.dumpFeatures(all));
        

        out.flush();
    }



    //////////////////////////////////////////////////////////////////////////////////

    static class TestCaseResult {
        String name;
        int create;
        int ser;
        int deser;
        int total;
        int size;
        int compressedSize;
        SerFeatures features = new SerFeatures();

        TestCaseResult() {
        }

        TestCaseResult(String name, int create, int ser, int deser, int total, int size, int compressedSize) {
            this.name = name;
            this.create = create;
            this.ser = ser;
            this.deser = deser;
            this.total = total;
            this.size = size;
            this.compressedSize = compressedSize;
        }

        public SerFeatures getFeatures() {
            return features;
        }

        public void setFeatures(SerFeatures features) {
            this.features = features;
        }

        public int getInt(String name) {
            try {
                final Field f = TestCaseResult.class.getDeclaredField(name);
                f.setAccessible(true);
                return f.getInt(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            return 0;
        }

        public String toString() {
            Formatter format = new Formatter().format(
                    "%-34s %6d %7d %7d %7d %6d %5d",
                    name,
                    create,
                    ser,
                    deser,
                    total,
                    size,
                    compressedSize);
            return format.toString();
        }
        
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCreate() {
            return create;
        }

        public void setCreate(int create) {
            this.create = create;
        }

        public int getSer() {
            return ser;
        }

        public void setSer(int ser) {
            this.ser = ser;
        }

        public int getDeser() {
            return deser;
        }

        public void setDeser(int deser) {
            this.deser = deser;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getCompressedSize() {
            return compressedSize;
        }

        public void setCompressedSize(int compressedSize) {
            this.compressedSize = compressedSize;
        }

    }

}
