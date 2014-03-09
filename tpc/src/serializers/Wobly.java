package serializers;

import serializers.wobly.compact.WoblyCompactUtils;
import serializers.wobly.simple.WoblySimpleUtils;

public class Wobly {
	public static void register(TestGroups groups)
	{
		groups.media.add(new WoblySimpleUtils.WoblyTransformer(), new WoblySimpleUtils.WoblySerializer(),
                new SerFeatures(
                        SerFormat.BINARY,
                        SerGraph.FLAT_TREE,
                        SerClass.CLASS_SPECIFIC_MANUAL_OPTIMIZATIONS,
                        ""
                )
        );
		groups.media.add(new WoblyCompactUtils.WoblyTransformer(), new WoblyCompactUtils.WoblySerializer(),
                new SerFeatures(
                        SerFormat.BINARY,
                        SerGraph.FLAT_TREE,
                        SerClass.CLASS_SPECIFIC_MANUAL_OPTIMIZATIONS,
                        ""
                )
        );
	}
	
}
