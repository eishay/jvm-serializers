package serializers.jackson;

import serializers.*;

import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.fasterxml.aalto.stax.OutputFactoryImpl;

import com.fasterxml.jackson.dataformat.xml.*;

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
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new StdJacksonDataBind<MediaContent>("xml/jackson+aalto/databind",
                        MediaContent.class, mapper),
                new SerFeatures(
                        SerFormat.XML,
                        SerGraph.FLAT_TREE,
                        SerClass.ZERO_KNOWLEDGE,
                        ""
                )
        );
    }
}
