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
 * Date: 09.03.14
 * Time: 10:09
 * To change this template use File | Settings | File Templates.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * tweak to generate a string containing all registered benchmarks and extract bench feature data. called by run script
 */
public class BenchmarkExporter extends BenchmarkRunner {

    String alltests = ""; // ',' separated 
    HashMap<String,SerFeatures> featureMap = new HashMap<String,SerFeatures>(); // have to map back after running .. sigh

    public BenchmarkExporter() {
        runBenchmark(new String[0]);
    }
    
    protected void runBenchmark(String[] args)
    {
        TestGroups groups = new TestGroups();
        addTests(groups);
        Set<String> media = groups.groupMap.get("media").entries.keySet();
        for (Iterator<String> iterator = media.iterator(); iterator.hasNext(); ) {
            String next = iterator.next().trim();
            if ( ! next.equals("cks") && ! next.equals("cks-text") ) // used to read data, exclude
                alltests += next+ (iterator.hasNext() ? "," : "");
            SerFeatures features = groups.groupMap.get("media").entries.get(next).serializer.getFeatures();
//            System.out.println("serializer:"+next+" miscFeatures: "+miscFeatures);
            featureMap.put(next, features);
        }
    }

    public String getAlltests() {
        return alltests;
    }

    public HashMap<String, SerFeatures> getFeatureMap() {
        return featureMap;
    }

    public static void main(String arg[]) {
        System.out.println(new BenchmarkExporter().getAlltests());
    }

}
