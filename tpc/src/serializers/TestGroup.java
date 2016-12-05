package serializers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TestGroup<J>
{
	public final Map<String,Entry<J,Object>> entries = new LinkedHashMap<String,Entry<J,Object>>();
    public final Map<String,Entry<J,Object>> extensionHandlers = new HashMap<String,Entry<J,Object>>();

    public <S> void add(Transformer<J,S> transformer, Serializer<S> serializer)
    {
        add(transformer,serializer,(SerFeatures)null);
    }

	public <S> void add(Transformer<J,S> transformer, Serializer<S> serializer, SerFeatures features)
	{
        if ( features != null )
            serializer.setFeatures(features);
		add_(transformer, serializer);
	}

    public <S> Entry<J,Object> add_(Transformer<J,S> transformer, Serializer<S> serializer)
	{
		Entry<J,S> entry = new Entry<J,S>(transformer, serializer);

		@SuppressWarnings("unchecked")
		Entry<J,Object> entry_ = (Entry<J,Object>) entry;

		String name = entry_.serializer.getName();
		Object displaced = entries.put(name, entry_);
		if (displaced != null) {
			throw new AssertionError("duplicate serializer name \"" + name + "\"");
		}

		return entry_;
	}

    public <S> void addExtensionHandler(String extension, Transformer<J,S> transformer, Serializer<S> serializer)
    {
		Entry<J,S> entry = new Entry<J,S>(transformer, serializer);

		@SuppressWarnings("unchecked")
		Entry<J,Object> entry_ = (Entry<J,Object>) entry;

		Object displaced = extensionHandlers.put(extension, entry_);
		if (displaced != null) {
			throw new AssertionError("duplicate handler for extension \"" + extension + "\"");
		}
    }

	public static final class Entry<J,S>
	{
		public final Transformer<J,S> transformer;
		public final Serializer<S> serializer;

		public Entry(Transformer<J,S> transformer, Serializer<S> serializer)
		{
			this.transformer = transformer;
			this.serializer = serializer;
		}
	}
}
