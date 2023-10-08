package serializers;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * Class loader to use for loading non-shared implementation classes. This is necessary so
 * that implementation classes can be loaded in, and as importantly, swapped out,
 * before and after test run.
 * 
 * @author tatu
 */
public class SimpleClassLoader
    extends ClassLoader
{
    private final HashMap<String,byte[]> loadedByteCode = new HashMap<String,byte[]>();
    
    public SimpleClassLoader(ClassLoader parent, File dir) throws IOException
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[8000];
        
        for (File file : dir.listFiles()) {
            if (!file.getName().endsWith(".jar")) continue;
            ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
            ZipEntry entry;
            
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.endsWith(".class")) {
                    // strip out ".class"; replace '/' with '.'
                    String className = name.substring(0, name.length()-6); 
                    className = className.replace('/', '.');
                    byte[] data = readAll(zis, buffer, bytes);
                    if (loadedByteCode.put(name, data) != null) {
                        throw new IllegalArgumentException("Duplicate class '"+className+"' (from jar '"+file.getPath()+"')");
                    }
                }
            }
        }
    }

    @Override
    public Class<?> findClass(String name)
    {
        byte[] bytecode = loadedByteCode.get(name);
        if (bytecode == null) return null;
        return defineClass(name, bytecode, 0, bytecode.length);        
    }
    
    private final byte[] readAll(InputStream in, byte[] buffer, ByteArrayOutputStream bytes) throws IOException
    {
        bytes.reset();
        
        int count;
        
        while ((count = in.read(buffer)) > 0) {
            bytes.write(buffer, 0, count);
        }
        return bytes.toByteArray();
    }        
}
