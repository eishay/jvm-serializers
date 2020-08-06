package serializers.xml;

import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.fasterxml.aalto.stax.OutputFactoryImpl;
import data.media.MediaContent;
import serializers.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class JaxbAalto<T> extends Serializer<T>
{
    public static void register(TestGroups groups)
    {
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new JaxbAalto<>("xml/JAXB/aalto", MediaContent.class,
                        new InputFactoryImpl(), new OutputFactoryImpl()),
                new SerFeatures(
                        SerFormat.XML,
                        SerGraph.FULL_GRAPH,
                        SerClass.CLASSES_KNOWN,
                        ""
                )
        );
    }

    private final String name;
    private final XMLInputFactory inputFactory;
    private final XMLOutputFactory outputFactory;
    private final JAXBContext jaxbContext;
    
    @SuppressWarnings("UnusedParameters")
    public JaxbAalto(String name, Class<T> clazz,
                     XMLInputFactory inputF, XMLOutputFactory outputF)
    {
        this.name = name;
        inputFactory = inputF;
        outputFactory = outputF;
        try {
            jaxbContext = JAXBContext.newInstance(MediaContent.class);
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
