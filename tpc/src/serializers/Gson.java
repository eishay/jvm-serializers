package serializers;

import java.io.*;

import data.media.MediaContent;

/**
 * This serializer uses Google-gson for data binding.
 * to configure)
 */
public class Gson
{
	public static void register(TestGroups groups)
	{
            groups.media.add(JavaBuiltIn.MediaTransformer,
                    new GenericSerializer<MediaContent>("json/google-gson/databind", MediaContent.class));
	}

	// ------------------------------------------------------------
	// Serializer (just one)

	static class GenericSerializer<T> extends Serializer<T>
	{
    	        private final com.google.gson.Gson _gson = new com.google.gson.Gson();
    	        private final String name;
    	        private final Class<T> type;
    
    	        public GenericSerializer(String name, Class<T> clazz)
    	        {
    	            this.name = name;
    	            type = clazz;
    	        }
    
    	        public String getName() { return name; }
    	        
    	        public T deserialize(byte[] array) throws Exception
    	        {
    	                Reader r = new InputStreamReader(new ByteArrayInputStream(array), "UTF-8");
    	                T result = _gson.fromJson(r, type);
    	                r.close();
    	                return result;
    	        }
    
    	        public byte[] serialize(T data) throws IOException
    	        {
    	                ByteArrayOutputStream baos = outputStream(data);
    	                OutputStreamWriter w = new OutputStreamWriter(baos, "UTF-8");
    	                _gson.toJson(data, w);
    	                w.close();
    	                return baos.toByteArray();
    	        }
    	}
}
