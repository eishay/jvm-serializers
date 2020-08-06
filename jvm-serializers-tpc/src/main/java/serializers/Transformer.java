package serializers;

public abstract class Transformer<A,B>
{
	public abstract B forward(A a);
	public abstract A reverse(B a);
	public abstract A shallowReverse(B a);

	public abstract A[] sourceArray(int size);
        public abstract B[] resultArray(int size);
	
        public B[] forwardAll(A[] a)
        {
            // this is unfortunate shuffling around type variables, but has to do:
            B[] b = resultArray(a.length);
            forward(a, b);
            return b;
        }
	
	/**
	 * Method called to convert an array of items from custom representation to standard one
	 */
	public void forward(A[] a, B[] b)
	{
	    for (int i = 0, len = a.length; i < len; ++i) {
	        b[i] = forward(a[i]);
	    }
	}

	public A[] reverseAll(B[] b)
	{
            A[] a = sourceArray(b.length);
            for (int i = 0, len = b.length; i < len; ++i) {
                a[i] = reverse(b[i]);
            }
            return a;
	}
}
