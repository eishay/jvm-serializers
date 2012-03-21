package core;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Full test of various codecs, using a single <code>MediaItem</code>
 * as test data.
 */
public class BenchmarkRunner extends MediaItemBenchmark
{
    public BenchmarkRunner() throws IOException {
        super();
    }

    public static void main(String[] args) throws Exception {
        new BenchmarkRunner().runBenchmark(args);
    }

    @Override
    protected void addTests(TestGroups groups, Set<String> tests) throws Exception {
        
        for (String t : tests) {
            String clazz = testClassMap.get(t);
            if (clazz != null) {
                register(clazz, groups);
            } else {
                throw new IllegalArgumentException("Unknown test: " + t +
                        ". No corresponding class.");
            }
        }
    }
}
