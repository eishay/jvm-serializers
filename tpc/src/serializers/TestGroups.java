package serializers;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import serializers.jackson.JacksonJsonManual;

public final class TestGroups
{
	public final TestGroup<data.media.MediaContent> media = new TestGroup<data.media.MediaContent>();
    {
        media.addExtensionHandler(
            "json",
            JavaBuiltIn.mediaTransformer,
            new JacksonJsonManual("", (JsonFactory) new JsonFactory().enable(JsonParser.Feature.ALLOW_COMMENTS)));
    }

	public final Map<String,TestGroup<?>> groupMap = new HashMap<String,TestGroup<?>>();

	{
		groupMap.put("media", media);
	}
}
