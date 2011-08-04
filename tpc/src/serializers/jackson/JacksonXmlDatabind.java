package serializers.jackson;

import serializers.JavaBuiltIn;
import serializers.TestGroups;

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
public class JacksonXmlDatabind
{
    public static void register(TestGroups groups)
    {
        XmlMapper mapper = new XmlMapper(new XmlFactory(null,
                new InputFactoryImpl(), new OutputFactoryImpl()));
        groups.media.add(JavaBuiltIn.MediaTransformer,
                new StdJacksonDataBind<MediaContent>("xml/jackson/databind-aalto",
                        MediaContent.class, mapper));
    }
}
