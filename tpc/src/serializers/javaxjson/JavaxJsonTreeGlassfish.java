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
        return "javax-json/tree/glassfish";
    }

    public static void register(TestGroups groups) {
        groups.media.add(new JavaxJsonTransformer(JSON), new JavaxJsonTreeGlassfish());
    }
}
