package serializers.jackson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import data.media.MediaContent;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

import serializers.*;

import de.undercouch.bson4jackson.BsonFactory;
import de.undercouch.bson4jackson.BsonModule;

/**
 * This serializer uses bson4jackson in full automated data binding mode, which
 * can handle typical Java POJOs (esp. beans; otherwise may need to annotate
 * to configure)
 */
public class JacksonBsonDatabind
{
    public static void register(TestGroups groups)
    {
        JsonFactory f = new BsonFactory();
        ObjectMapper mapper = new ObjectMapper(f);
        mapper.registerModule(new BsonModule());
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new DataBindBase<MediaContent>(
                        "bson/jackson/databind", MediaContent.class, mapper),
                new SerFeatures(
                        SerFormat.BIN_CROSSLANG,
                        SerGraph.FLAT_TREE,
                        SerClass.CLASSES_KNOWN,
                        ""
                )
        );
    }

    public final static class DataBindBase<T> extends Serializer<T>
    {
        protected final String name;
        protected final JavaType type;
        protected final ObjectMapper mapper;
        protected final ObjectReader objectReader;
        protected final ObjectWriter objectWriter;
        
        public DataBindBase(String name, Class<T> clazz, ObjectMapper mapper)
        {
            this.name = name;
            type = mapper.constructType(clazz);
            this.mapper = mapper;
            objectReader = mapper.readerFor(type);
            objectWriter = mapper.writerFor(type);
        }

        @Override
        public final String getName() {
            return name;
        }

        protected final JsonParser constructParser(byte[] data) throws IOException {
            return mapper.getFactory().createParser(data, 0, data.length);
        }

        protected final JsonParser constructParser(InputStream in) throws IOException {
            return mapper.getFactory().createParser(in);
        }
        
        protected final JsonGenerator constructGenerator(OutputStream out) throws IOException {
            return mapper.getFactory().createGenerator(out, JsonEncoding.UTF8);
        }
        
        @Override
        public byte[] serialize(T data) throws IOException
        {
            return objectWriter.writeValueAsBytes(data);
        }
    
        @Override
        @SuppressWarnings("unchecked")
        public T deserialize(byte[] array) throws IOException
        {
            return (T) objectReader.readValue(array, 0, array.length);
        }
    
        // // Future extensions for testing performance for item sequences
        
        @Override
        public void serializeItems(T[] items, OutputStream out) throws IOException
        {
            JsonGenerator generator = constructGenerator(out);
            // JSON allows simple sequences, so:
            for (int i = 0, len = items.length; i < len; ++i) {
                mapper.writeValue(generator, items[i]);
            }
            generator.close();
        }
    
        @Override
        @SuppressWarnings("unchecked")
        public T[] deserializeItems(InputStream in, int numberOfItems) throws IOException 
        {
            T[] result = (T[]) new Object[numberOfItems];
            JsonParser parser = constructParser(in);
            for (int i = 0; i < numberOfItems; ++i) {
                result[i] = (T) mapper.readValue(parser, type);
            }
            parser.close();
            return result;
        }
    }
}
