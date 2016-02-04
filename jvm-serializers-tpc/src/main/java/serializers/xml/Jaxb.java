package serializers.xml;

import data.media.MediaContent;
import serializers.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Jaxb<T> extends Serializer<T>
{
    public static void register(TestGroups groups)
    {
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new Jaxb<>("xml/JAXB", MediaContent.class),
                new SerFeatures(
                        SerFormat.XML,
                        SerGraph.FULL_GRAPH,
                        SerClass.CLASSES_KNOWN,
                        ""
                )
        );
    }

    private final String name;
    private final JAXBContext jaxbContext;

    @SuppressWarnings("UnusedParameters")
    public Jaxb(String name, Class<T> clazz)
    {
        this.name = name;
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
            jaxbContext.createMarshaller().marshal(data, baos);
        } catch (Exception e) {
            throw new IOException(e);
        }
        return baos.toByteArray();
    }

    @Override
    public T deserialize(byte[] data) throws Exception
    {
        try {
            @SuppressWarnings("unchecked")
            T result = (T) jaxbContext.createUnmarshaller().unmarshal(new ByteArrayInputStream(data));
            return result;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
