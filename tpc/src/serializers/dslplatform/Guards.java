package serializers.dslplatform;

public final class Guards {
    /// No-oped for perf reasons
    public static <T> void checkNulls(final Iterable<T> values) {
        return;
//              if (values == null) return;
//
//              int i = 0;
//              for (final T value : values) {
//                     if (value == null) throw new IllegalArgumentException("Element at index " + i + " was a null value, which is not permitted.");
//                     i++;
//              }
    }
}
