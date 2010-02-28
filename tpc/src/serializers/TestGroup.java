package serializers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class TestGroup<J>
{
	public final ArrayList<Entry<J,Object>> entries = new ArrayList<Entry<J,Object>>();
	public final Map<String,Entry<J,Object>> extensionMap = new HashMap<String,Entry<J,Object>>(); // So we know which one to use to load from files.

	public <S> void add(Transformer<J,S> transformer, Serializer<S> serializer)
	{
		Entry<J,S> entry = new Entry<J,S>(transformer, serializer);

		@SuppressWarnings("unchecked")
		Entry<J,Object> entry_ = (Entry<J,Object>) entry;

		entries.add(entry_);
	}

	public <S> void add(Transformer<J,S> transformer, Serializer<S> serializer, String extension)
	{
		Entry<J,S> entry = new Entry<J,S>(transformer, serializer);

		@SuppressWarnings("unchecked")
		Entry<J,Object> entry_ = (Entry<J,Object>) entry;

		entries.add(entry_);
		Object displaced =extensionMap.put(extension, entry_);
		if (displaced != null) {
			throw new AssertionError("Duplicate entry for file extension \"" + extension + "\"");
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
