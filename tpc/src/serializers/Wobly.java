package serializers;

import core.TestGroups;
import serializers.wobly.compact.WoblyCompactUtils;
import serializers.wobly.simple.WoblySimpleUtils;

public class Wobly {
	public static void register(TestGroups groups)
	{
		groups.media.add(new WoblySimpleUtils.WoblyTransformer(), new WoblySimpleUtils.WoblySerializer());
		groups.media.add(new WoblyCompactUtils.WoblyTransformer(), new WoblyCompactUtils.WoblySerializer());
	}
	
}
