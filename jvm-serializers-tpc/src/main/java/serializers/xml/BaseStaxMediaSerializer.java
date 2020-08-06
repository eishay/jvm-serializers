package serializers.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import serializers.Serializer;
import data.media.MediaContent;

public abstract class BaseStaxMediaSerializer extends Serializer<MediaContent>
{
    private final StaxSerializer _serializer = new StaxSerializer();

    private final StaxDeserializer _deserializer;
    
    protected BaseStaxMediaSerializer(boolean workingGetElementText)
    {
        _deserializer = new StaxDeserializer(workingGetElementText);
    }
    
    protected XMLStreamReader createReader(byte[] input) throws XMLStreamException {
        return createReader(new ByteArrayInputStream(input));
    }
    protected abstract XMLStreamReader createReader(InputStream in) throws XMLStreamException;
    protected abstract XMLStreamWriter createWriter(OutputStream output) throws XMLStreamException;
    
    // // Public API

    @Override
    public MediaContent deserialize (byte[] array) throws XMLStreamException
    {
        XMLStreamReader parser = createReader(array);
        MediaContent content = _deserializer.readDocument(parser);
        parser.close();
        return content;
    }

    @Override
    public MediaContent[] deserializeItems(InputStream in, int numberOfItems) throws XMLStreamException 
    {
        XMLStreamReader parser = createReader(in);
        MediaContent[] result = _deserializer.readDocument(parser, numberOfItems);
        parser.close();
        return result;
    }
    
    @Override
    public byte[] serialize(MediaContent content) throws Exception
    {
        ByteArrayOutputStream baos = outputStream(content);
        XMLStreamWriter writer = createWriter(baos);
        _serializer.writeDocument(writer, content);
        writer.flush();
        writer.close();
        return baos.toByteArray();
    }

    @Override
    public final void serializeItems(MediaContent[] items, OutputStream out) throws XMLStreamException
    {
        // XML requires single root, so add bogus "stream"
        XMLStreamWriter writer = createWriter(out);
        _serializer.writeDocument(writer, items);
        writer.flush();
        writer.close();
    }

 }