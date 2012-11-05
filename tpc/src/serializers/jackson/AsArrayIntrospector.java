package serializers.jackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * Helper class used to force conditional "as-array" serialization
 * without requiring value classes to be annotated.
 */
class AsArrayIntrospector extends JacksonAnnotationIntrospector
{
    @Override
    public JsonFormat.Value findFormat(Annotated ann) {
        return new JsonFormat.Value("", JsonFormat.Shape.ARRAY, "", "");
    }
}