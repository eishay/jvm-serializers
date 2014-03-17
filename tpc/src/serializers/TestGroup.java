package serializers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class TestGroup<J>
{
	public final ArrayList<Entry<J,Object>> entries = new ArrayList<Entry<J,Object>>();
	final Set<String> entryNames = new HashSet<String>();
	public final Map<String,Entry<J,Object>> extensionMap = new HashMap<String,Entry<J,Object>>(); // So we know which one to use to load from files.
    Map<String,Serializer> serMap= new HashMap<String,Serializer>();

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

	public <S> void add(Transformer<J,S> transformer, Serializer<S> serializer, String extension )
	{
		Entry<J,Object> entry_ = add_(transformer, serializer);

		Object displaced = extensionMap.put(extension, entry_);
		if (displaced != null) {
			throw new AssertionError("Duplicate entry for file extension \"" + extension + "\"");
		}
	}

    public Map<String, Serializer> getSerMap() {
        return serMap;
    }

    public <S> Entry<J,Object> add_(Transformer<J,S> transformer, Serializer<S> serializer)
	{
		Entry<J,S> entry = new Entry<J,S>(transformer, serializer);
        serMap.put(serializer.getName(),serializer);

		@SuppressWarnings("unchecked")
		Entry<J,Object> entry_ = (Entry<J,Object>) entry;

		entries.add(entry_);

		String name = entry_.serializer.getName();
		boolean isUnique = entryNames.add(name);
		if (!isUnique) {
			throw new AssertionError("duplicate serializer name \"" + name + "\"");
		}

		return entry_;
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
