package serializers.javaxjson;

import org.glassfish.json.*;

import serializers.*;

public class JavaxJsonStreamGlassfish extends JavaxJsonStream {
    private static final JsonProviderImpl JSON = new JsonProviderImpl();

    public JavaxJsonStreamGlassfish() {
        super(JSON);
    }

    @Override
    public String getName() {
        return "json/javax-stream/glassfish";
    }

    public static void register(TestGroups groups) {
        groups.media.add(JavaBuiltIn.mediaTransformer, new JavaxJsonStreamGlassfish(),
                new SerFeatures(
                        SerFormat.JSON,
                        SerGraph.FLAT_TREE,
                        SerClass.MANUAL_OPT,
                        ""
                )
        );
    }
}
