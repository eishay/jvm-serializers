package serializers;

import serializers.wobly.optimized.WoblyOptimizedUtils;
import serializers.wobly.simple.WoblySimpleUtils;

public class Wobly {
	public static void register(TestGroups groups)
	{
		groups.media.add(new WoblySimpleUtils.WoblyTransformer(), new WoblySimpleUtils.WoblySerializer());
		groups.media.add(new WoblyOptimizedUtils.WoblyTransformer(), new WoblyOptimizedUtils.WoblySerializer());
	}
	
}
