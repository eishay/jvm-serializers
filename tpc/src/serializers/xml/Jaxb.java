package serializers.xml;

import java.io.*;

import javax.xml.bind.*;
import javax.xml.stream.*;

import core.TestGroups;
import serializers.JavaBuiltIn;
import core.serializers.Serializer;

import data.media.MediaContent;

import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.fasterxml.aalto.stax.OutputFactoryImpl;

public class Jaxb<T> extends Serializer<T>
{
    public static void register(TestGroups groups)
    {
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new Jaxb<MediaContent>("JAXB/aalto", MediaContent.class,
                        new InputFactoryImpl(), new OutputFactoryImpl()));
    }

    private final String name;
    private final XMLInputFactory inputFactory;
    private final XMLOutputFactory outputFactory;
    private final JAXBContext jaxbContext;
    
    public Jaxb(String name,Class<T> clazz,
            XMLInputFactory inputF, XMLOutputFactory outputF)
    {
        this.name = name;
        inputFactory = inputF;
        outputFactory = outputF;
        try {
            jaxbContext = JAXBContext.newInstance(clazz);
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getName() { return name; }

    @Override
    public byte[] serialize(T data) throws IOException
    {
        ByteArrayOutputStream baos = outputStream(data);
        try {
            XMLStreamWriter sw = outputFactory.createXMLStreamWriter(baos, "UTF-8");
            jaxbContext.createMarshaller().marshal(data, sw);
            sw.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
        return baos.toByteArray();
    }

    @Override
    public T deserialize(byte[] data) throws Exception
    {
        try {
            XMLStreamReader sr = inputFactory.createXMLStreamReader(new ByteArrayInputStream(data));
            @SuppressWarnings("unchecked")
            T result = (T) jaxbContext.createUnmarshaller().unmarshal(sr);
            sr.close();
            return result;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
