package serializers;

import java.io.IOException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;

import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.fasterxml.aalto.stax.OutputFactoryImpl;

import com.fasterxml.jackson.xml.XmlFactory;
import com.fasterxml.jackson.xml.XmlMapper;

import data.media.MediaContent;

/**
 * Test for handling XML using "jackson-xml-databind" codec
 * (https://github.com/FasterXML/jackson-xml-databind)
 * with Aalto Stax XML parser.
 */
public class JacksonXmlDatabind<T> extends Serializer<T>
{
    public static void register(TestGroups groups)
    {
        groups.media.add(JavaBuiltIn.MediaTransformer,
                new JacksonXmlDatabind<MediaContent>("xml/jackson-databind/aalto", MediaContent.class,
                        new InputFactoryImpl(), new OutputFactoryImpl()));
    }

    private final String name;
    private final JavaType type;
    private final XmlMapper mapper;

    public JacksonXmlDatabind(String name,Class<T> clazz,
            XMLInputFactory inputF, XMLOutputFactory outputF)
    {
        this.name = name;
        type = TypeFactory.type(clazz);
        mapper = new XmlMapper(new XmlFactory(null, inputF, outputF));
    }

    public String getName() { return name; }

    public byte[] serialize(T data) throws IOException
    {
            return mapper.writeValueAsBytes(data);
    }

    @SuppressWarnings("unchecked")
    public T deserialize(byte[] array) throws Exception
    {
        return (T) mapper.readValue(array, 0, array.length, type);
    }
    
}
