package serializers.xml;

import java.io.*;

import javax.xml.stream.*;

import serializers.*;

/**
 * Codec that works with standard full Stax implementations, where
 * we have Stax input and output factories.
 */
public class XmlStax
{
    /**
     * Since XML streams must still have a single root, we'll use
     * this as the tag
     */
    public final static String STREAM_ROOT = "stream";
    
    public static final Handler[] HANDLERS = new Handler[] {
        new Handler("woodstox",
                new com.ctc.wstx.stax.WstxInputFactory(),
                new com.ctc.wstx.stax.WstxOutputFactory()),
        new Handler("aalto",
                new com.fasterxml.aalto.stax.InputFactoryImpl(),
                new com.fasterxml.aalto.stax.OutputFactoryImpl()),
        new Handler("fastinfo",
                new com.sun.xml.fastinfoset.stax.factory.StAXInputFactory(),
                new com.sun.xml.fastinfoset.stax.factory.StAXOutputFactory()),
    };
    
    public static void register(TestGroups groups, boolean woodstox, boolean aalto, boolean fastinfoset)
    {
        if (woodstox) {
            groups.media.add(JavaBuiltIn.mediaTransformer, new StaxMediaSerializer(HANDLERS[0]),
                    new SerFeatures(
                            SerFormat.XML,
                            SerGraph.UNKNOWN,
                            SerClass.MANUAL_OPT,
                            ""
                    )
            );

        }
        if (aalto) {
            groups.media.add(JavaBuiltIn.mediaTransformer, new StaxMediaSerializer(HANDLERS[1]),
                    new SerFeatures(
                            SerFormat.XML,
                            SerGraph.UNKNOWN,
                            SerClass.MANUAL_OPT,
                            ""
                    )
            );
        }
        if (fastinfoset) {
            groups.media.add(JavaBuiltIn.mediaTransformer, new StaxMediaSerializer(HANDLERS[2]),
                    new SerFeatures(
                            SerFormat.XML,
                            SerGraph.UNKNOWN,
                            SerClass.MANUAL_OPT,
                            ""
                    )
            );
        }
    }

    // -------------------------------------------------------------------
    // Implementations

    public static final class Handler
    {
        protected final String name;
        protected final XMLInputFactory inFactory;
        protected final XMLOutputFactory outFactory;

        protected Handler(String name, XMLInputFactory inFactory, XMLOutputFactory outFactory)
        {
            this.name = name;
            this.inFactory = inFactory;
            this.outFactory = outFactory;
        }
    }

    // -------------------------------------------------------------------
    // Serializers

    // Serializer for full Stax implementations
    public static final class StaxMediaSerializer extends BaseStaxMediaSerializer
    {
        private final Handler handler;

        public StaxMediaSerializer(Handler handler)
        {
            // yes, standard implementations better implement it correctly
            super(true);
            this.handler = handler;
        }

        @Override
        public String getName() { return "xml/"+handler.name+"-manual"; }

        @Override
        protected XMLStreamReader createReader(InputStream in) throws XMLStreamException {
            return handler.inFactory.createXMLStreamReader(in);
        }

        @Override
        protected XMLStreamWriter createWriter(OutputStream out) throws XMLStreamException {
            return handler.outFactory.createXMLStreamWriter(out, "UTF-8");
        }
    }
}
