package serializers.dsljson;

import data.media.MediaContent;
import serializers.*;

import com.dslplatform.json.*;
import com.dslplatform.json.runtime.Settings;

public class DSLJson {

    public static void register(final TestGroups groups) {
        groups.media.add(JavaBuiltIn.mediaTransformer, new DSLJsonSerializer(false), new SerFeatures(
                SerFormat.JSON, SerGraph.FLAT_TREE, SerClass.CLASSES_KNOWN, "Serializes all properties with exact names."));
        groups.media.add(JavaBuiltIn.mediaTransformer, new DSLJsonSerializer(true), new SerFeatures(
                SerFormat.JSON, SerGraph.FLAT_TREE, SerClass.CLASSES_KNOWN,
                "JSON array format - all properties without names."));
    }

    static class DSLJsonSerializer extends Serializer<MediaContent> {
        private final JsonWriter writer;
        private final JsonReader reader;
        private final JsonWriter.WriteObject<MediaContent> encoder;
        private final JsonReader.ReadObject<MediaContent> decoder;
        private final boolean asArray;

        DSLJsonSerializer(boolean asArray) {
            DslJson<Object> dslJson = new DslJson<>(Settings.withRuntime().allowArrayFormat(asArray).includeServiceLoader());
            this.writer = dslJson.newWriter();
            this.reader = dslJson.newReader();
            this.encoder = dslJson.tryFindWriter(MediaContent.class);
            this.decoder = dslJson.tryFindReader(MediaContent.class);
            this.asArray = asArray;
        }

        @Override
        public String getName() {
            return asArray ? "json-array/dsl-json/databind" : "json/dsl-json/databind";
        }

        @Override
        public MediaContent deserialize(final byte[] array) throws Exception {
            reader.process(array, array.length).read();
            return decoder.read(reader);
        }

        @Override
        public byte[] serialize(final MediaContent content) throws Exception {
            writer.reset();
            encoder.write(writer, content);
            return writer.toByteArray();
        }
    }
}
