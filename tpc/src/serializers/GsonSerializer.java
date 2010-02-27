package serializers;

import java.io.*;

import serializers.java.MediaContent;
import serializers.java.Media;

import com.google.gson.*;

/**
 * This serializer uses Google-gson for data binding.
 * to configure)
 */
public class GsonSerializer
    extends StdMediaSerializer
{
    final Gson _gson = new Gson();

    public int expectedSize = 0;

    public GsonSerializer() { super("json/google-gson"); }

    public final byte[] serialize(MediaContent content) throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(expectedSize);
        OutputStreamWriter w = new OutputStreamWriter(baos, "UTF-8");
        _gson.toJson(content, w);
        w.close();
        byte[] array = baos.toByteArray();
        expectedSize = array.length;
        return array;
    }

    public final MediaContent deserialize(byte[] array) throws Exception
    {
        Reader r = new InputStreamReader(new ByteArrayInputStream(array), "UTF-8");
        MediaContent result = _gson.fromJson(r, MediaContent.class);
        r.close();
        return result;
    }
}

