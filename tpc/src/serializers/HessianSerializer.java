package serializers;

import java.io.*;
import java.util.*;

import serializers.java.MediaContent;

import com.caucho.hessian.io.*;

public class HessianSerializer extends StdMediaSerializer
{
    private ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
    
    public HessianSerializer()
    {
        super("hessian");
    }

    public MediaContent deserialize (byte[] array) throws Exception
    {
        ByteArrayInputStream in = new ByteArrayInputStream(array);
        Hessian2StreamingInput hin = new Hessian2StreamingInput(in);
        return (MediaContent) hin.readObject();
    }

    public byte[] serialize(MediaContent content) throws Exception
    {
        out.reset();
        Hessian2StreamingOutput hout = new Hessian2StreamingOutput(out);
        hout.writeObject(content);
        byte[] array = out.toByteArray();
        return array;
    }
}
