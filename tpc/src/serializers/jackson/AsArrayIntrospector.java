package serializers.jackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * Helper class used to force conditional "as-array" serialization
 * without requiring value classes to be annotated.
 */
class AsArrayIntrospector extends JacksonAnnotationIntrospector
{
    private static final long serialVersionUID = 1L;

    @Override
    public JsonFormat.Value findFormat(Annotated ann) {
        // 2.4 frowns upon trying to use this for Enums, so avoid those
        // also, limit to just claiming classes (POJOs) require it, not properties
        if (ann instanceof AnnotatedClass) {
            AnnotatedClass ac = (AnnotatedClass) ann;
            if (ac.getAnnotated().isEnum()) {
                return null;
            }
            return new JsonFormat.Value("", JsonFormat.Shape.ARRAY, "", "");
        }
        return null;
    }
}