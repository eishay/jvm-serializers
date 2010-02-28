package serializers;

import java.util.ArrayList;

public final class TestGroup<J>
{
	public final ArrayList<Entry<J,Object>> entries = new ArrayList<Entry<J,Object>>();

	public <S> void add(Transformer<J,S> transformer, Serializer<S> serializer)
	{
		Entry<J,S> entry = new Entry<J,S>(transformer, serializer);

		@SuppressWarnings("unchecked")
		Entry<J,Object> entry_ = (Entry<J,Object>) entry;

		entries.add(entry_);
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
