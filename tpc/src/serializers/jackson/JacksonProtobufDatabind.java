package serializers.jackson;

import java.io.*;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.protobuf.*;
import com.fasterxml.jackson.dataformat.protobuf.schema.ProtobufSchema;
import com.fasterxml.jackson.dataformat.protobuf.schema.ProtobufSchemaLoader;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

import serializers.*;
import data.media.MediaContent;

public class JacksonProtobufDatabind
{
    public static void register(TestGroups groups) {
        ProtobufMapper mapper = new ProtobufMapper();
        ProtobufSchema schema;
        try {
            schema = new ProtobufSchemaLoader().load(new File("schema/media.proto.jackson"),
                    "MediaContent");
                    //"schema/media.proto"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // not sure if using (or not) of index actually makes much diff but:
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
        final JavaType type = mapper.constructType(MediaContent.class);
        
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new StdJacksonDataBind<MediaContent>("protobuf/jackson/databind", type, mapper,
                        mapper.readerFor(MediaContent.class).with(schema),
                        mapper.writerFor(MediaContent.class).with(schema)),
                new SerFeatures(
                        SerFormat.BINARY,
                        SerGraph.FLAT_TREE,
                        SerClass.CLASSES_KNOWN,
                        ""
                )
        );

        mapper = new ProtobufMapper();
        mapper.registerModule(new AfterburnerModule());
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
        
        groups.media.add(JavaBuiltIn.mediaTransformer,
                new StdJacksonDataBind<MediaContent>("protobuf/jackson+afterburner/databind", type, mapper,
                        mapper.readerFor(MediaContent.class).with(schema),
                        mapper.writerFor(MediaContent.class).with(schema)),
                new SerFeatures(
                        SerFormat.BINARY,
                        SerGraph.FLAT_TREE,
                        SerClass.CLASSES_KNOWN,
                        ""
                )
        );
    }
}
