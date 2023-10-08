package serializers.javaxjson;

import org.glassfish.json.*;

import serializers.*;

public class JavaxJsonTreeGlassfish extends JavaxJsonTree {
    private static final JsonProviderImpl JSON = new JsonProviderImpl();

    public JavaxJsonTreeGlassfish() {
        super(JSON);
    }

    @Override
    public String getName() {
        return "json/javax-tree/glassfish";
    }

    public static void register(TestGroups groups) {
        groups.media.add(new JavaxJsonTransformer(JSON), new JavaxJsonTreeGlassfish(),
                new SerFeatures(
                        SerFormat.JSON,
                        SerGraph.FLAT_TREE,
                        SerClass.ZERO_KNOWLEDGE,
                        ""
                )
        );
    }
}
