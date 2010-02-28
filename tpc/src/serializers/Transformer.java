package serializers;

public abstract class Transformer<A,B>
{
	public abstract B forward(A a);
	public abstract A reverse(B a);
	public abstract A shallowReverse(B a);
}
