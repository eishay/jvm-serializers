package serializers;

public abstract class Transformer<A,B>
{
	public abstract B forward(A a);
	public abstract A reverse(B a);
	public abstract A shallowReverse(B a);

	private static final Transformer<Object,Object> Identity = new Transformer<Object,Object>()
	{
		public Object forward(Object a) { return a; }
		public Object reverse(Object b) { return b; }
		public Object shallowReverse(Object b) { return b; }
	};

	public static <T> Transformer<T,T> Identity()
	{
		@SuppressWarnings("unchecked")
		Transformer<T,T> t = (Transformer<T,T>) Identity;
		return t;
	}
}
