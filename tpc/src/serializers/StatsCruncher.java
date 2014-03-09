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
import java.net.URI;
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

    HashMap<String, SerFeatures> mappedFeatures = new HashMap<>();
    HashMap<String, TestCaseResult> mappedResults;
    List<TestCaseResult> resultList;

    static SerFormat BIN = SerFormat.BINARY;
    static SerFormat BIN_CL = SerFormat.BINARY_CROSSLANG;
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
            resultList.add(res);
        }
    }

    public int max(List<TestCaseResult> resultList, String arg) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < resultList.size(); i++) {
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
        int max = max(resultList,higherValueName);
        String res = "https://chart.googleapis.com/chart?cht=bhs&chs=600x"+(resultList.size()*26+20); // html: finally a device independent technology
//        res+="&chtt="+URLEncoder.encode(title);
        res+="&chd=t:";
        for (int i = 0; i < resultList.size(); i++) {
            TestCaseResult testCaseResult = resultList.get(i);
            int val = testCaseResult.getInt(lowerValueName);
            res+= val +((i<resultList.size()-1) ? ",":"|");
        }
        for (int i = 0; i < resultList.size(); i++) {
            TestCaseResult testCaseResult = resultList.get(i);
            int valLower = (testCaseResult.getInt(lowerValueName));
            int val = testCaseResult.getInt(higherValueName);
            val -= valLower;
            res+=val+((i<resultList.size()-1) ? ",":"");
        }
        res += "&chco=5d99f9,4d89f9&chdlp=t";
        res += "&chbh=20";
        res += "&chds=0,"+max;
        res += "&chxr=1,0,"+max;
        res += "&chxt=y,x&chxl=0:|";
        for (int i = 0; i < resultList.size(); i++) {
            TestCaseResult testCaseResult = resultList.get(resultList.size()-i-1);
            res+=URLEncoder.encode(testCaseResult.getName())+((i<resultList.size()-1) ? "|":"");
        }
        return "<b>"+title+"</b><br><img src='"+res+"'/>";
    }

    /**
     * 
     * @param commaSeparatedNames - * for all
     * @param intFieldToSort - result field name to sort for (create ser deser total size compressedSize) 
     * @param maxRangeDiff - max distance of min value (remove outliers)
     * @param maxListLen - max number of entries in result
     * @param features - required features. null in a feature is wildcard. e.g. new SerFeature( JSON, null, null ) 
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
        Collections.sort(chartList, new Comparator<TestCaseResult>() {
            @Override
            public int compare(TestCaseResult o1, TestCaseResult o2) {
                return o1.getInt(intFieldToSort)-o2.getInt(intFieldToSort);
            }
        });
        // process feature filter (I am in dirt mode here ..)
        for (int i = 0; i < chartList.size(); i++) {
            TestCaseResult testCaseResult = chartList.get(i);
            SerFeatures feature = mappedFeatures.get(testCaseResult.getName());
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

    String generateChartAndDump(String title, final String intFieldToSort,final String lowerValueName, SerFormat fmt, SerGraph graph,SerClass clz ) {
        return generateChartAndDump(title, "*", intFieldToSort, lowerValueName, 50, 18, new SerFeatures(fmt,graph,clz));
    }

    String generateChartAndDump(String title, String commaSeparatedNamesOrStar, final String intFieldToSort,final String lowerValueName, int maxRangeDiff, int maxListLen, SerFeatures featureFilter) {
        List<TestCaseResult> testCaseResults = generateChartList(commaSeparatedNamesOrStar, intFieldToSort, maxRangeDiff, maxListLen, featureFilter);
        
        return "<b>"+title+"</b><br>\n"+dump(testCaseResults)+generateChart(testCaseResults,title,lowerValueName,intFieldToSort);
    }

    String dump(List<TestCaseResult> total) {
        String res = "\npre.                                    create     ser   deser   total   size  +dfl\n";
        for (int i = 0; i < total.size(); i++) {
            TestCaseResult testCaseResult = total.get(i);
            res+=testCaseResult.toString()+"\n";
        }
        return res+"\n";
    }

    //////////////////////////////////////////////////////////////////////////////////


    public static void main(String arg[]) throws IOException, URISyntaxException {
        StatsCruncher statsCruncher = new StatsCruncher();
        statsCruncher.readStats();
        List<TestCaseResult> total = statsCruncher.generateChartList("*", "total", 1000, 1000, new SerFeatures(null,null,null));
        statsCruncher.dump(total);

        System.out.println( statsCruncher.generateChartAndDump("ZeroEffort Full Serializers, total Time+Ser Time in ns", "total", "ser", null, SerGraph.FULL_GRAPH_WITH_SHARED_OBJECTS, SerClass.ZERO_KNOWLEDGE) );
        System.out.println( statsCruncher.generateChartAndDump("ZeroEffort Serializers, total Time+Ser Time in ns", "total", "ser", null, null, SerClass.ZERO_KNOWLEDGE) );
        System.out.println( statsCruncher.generateChartAndDump("CL Binary Serializers, total Time+Ser Time in ns", "total", "ser", SerFormat.BINARY_CROSSLANG, null, null) );
        System.out.println( statsCruncher.generateChartAndDump("Class known, total Time+Ser Time in ns", "total", "ser", null, null, SerClass.CLASSES_KNOWN) );
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
