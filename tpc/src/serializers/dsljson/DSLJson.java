package serializers.dsljson;

import data.media.MediaContent;
import serializers.*;

import com.dslplatform.json.*;

import java.io.ByteArrayOutputStream;

public class DSLJson {

    public static void register(final TestGroups groups) {
        groups.media.add(JavaBuiltIn.mediaTransformer, new DSLJsonSerializer(false), new SerFeatures(
                SerFormat.JSON, SerGraph.FLAT_TREE, SerClass.CLASSES_KNOWN, "Serializes all properties with exact names."));
        groups.media.add(JavaBuiltIn.mediaTransformer, new DSLJsonSerializer(true), new SerFeatures(
                SerFormat.JSON, SerGraph.FLAT_TREE, SerClass.CLASSES_KNOWN,
                "JSON array format - all properties without names."));
    }

    static class DSLJsonSerializer extends Serializer<MediaContent> {
        final boolean asArray;
        final DslJson<Object> json;
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DSLJsonSerializer(boolean asArray) {
            this.asArray = asArray;
            this.json = new DslJson<Object>(new DslJson.Settings<>().allowArrayFormat(asArray).includeServiceLoader());
        }

        @Override
        public String getName() {
            return this.asArray ? "json-array/dsl-json/databind" : "json/dsl-json/databind";
        }

        @Override
        public MediaContent deserialize(final byte[] array) throws Exception {
            return json.deserialize(MediaContent.class, array, array.length);
        }

        @Override
        public byte[] serialize(final MediaContent content) throws Exception {
            json.serialize(content, buffer);
            return buffer.toByteArray();
        }
    }
}
