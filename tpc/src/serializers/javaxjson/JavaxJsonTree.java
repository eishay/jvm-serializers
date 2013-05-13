package serializers.javaxjson;

import java.io.*;

import javax.json.*;
import javax.json.spi.*;

import serializers.*;

/**
 * Base class for javax.json benchmark using the tree model intermediate
 */
public abstract class JavaxJsonTree extends Serializer<JsonStructure> {
    private final JsonProvider json;

    public JavaxJsonTree(JsonProvider json) {
        this.json = json;
    }

    @Override
    public JsonStructure deserialize(byte[] array) throws Exception {
    	return json.createReader(new ByteArrayInputStream(array)).read();
    }

    @Override
    public byte[] serialize(JsonStructure content) throws Exception {
    	ByteArrayOutputStream outputStream = outputStream(content);
    	JsonWriter writer = json.createWriter(outputStream);
    	writer.write(content);
    	writer.close();
    	return outputStream.toByteArray();
    }

}