package serializers;

import java.util.HashMap;
import java.util.Map;

public final class TestGroups
{
	public final TestGroup<data.media.MediaContent> media = new TestGroup<data.media.MediaContent>();
	public final Map<String,TestGroup<?>> groupMap = new HashMap<String,TestGroup<?>>();

	{
		groupMap.put("media", media);
	}
}
