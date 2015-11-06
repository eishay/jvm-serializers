package serializers.coherence;

import data.media.MediaContent;
import serializers.*;

public class Coherence {


    public static void register(TestGroups groups) {
        groups.media.add(JavaBuiltIn.mediaTransformer, new CoherencePofSerializer<MediaContent>(MediaContent.class),
                new SerFeatures(
                        SerFormat.BIN_CROSSLANG,
                        SerGraph.FULL_GRAPH,
                        SerClass.MANUAL_OPT, "coherence-pof"
                )
        );
    }

    // ------------------------------------------------------------
    // Serializer (just one)

    public final static class CoherencePofSerializer<T> extends AbstractCoherencePofSerializer<T> {

        public CoherencePofSerializer(Class<T> c) {
            super(c, "pregen/media.coherence/serializers/coherence/pof-config.xml");

        }
        public String getName() {
            return "coherence-pof";
        }

    }
}
