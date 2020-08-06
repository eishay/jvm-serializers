package serializers.protobuf;

import serializers.*;
import serializers.protobuf.media.MediaContentHolder.MediaContent;

import java.io.IOException;
import java.nio.charset.Charset;

import com.google.protobuf.JsonFormat;

public class ProtobufJson
{
    public static void register(TestGroups groups) {
        groups.media.add(new Protobuf.Transformer(), new JsonSerializer(),
                new SerFeatures(
                        SerFormat.JSON,
                        SerGraph.FLAT_TREE,
                        SerClass.CLASSES_KNOWN,
                        ""
                )
        );
    }

    private static final Charset _charset = Charset.forName("UTF-8");

    static final class JsonSerializer extends Serializer<MediaContent>
    {
        public MediaContent deserialize (byte[] array) throws Exception
        {
            MediaContent.Builder builder = MediaContent.newBuilder();
            JsonFormat.merge(new String(array, _charset.name()), builder);
            return builder.build();
        }

        public byte[] serialize(MediaContent content) throws IOException {
            return JsonFormat.printToString(content).getBytes(_charset.name());
        }

        public String getName () {
            return "json/protobuf";
	}
    }
}
