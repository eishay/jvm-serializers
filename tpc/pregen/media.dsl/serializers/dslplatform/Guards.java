package serializers.dslplatform;


import java.util.*;
import java.math.BigDecimal;

public final class Guards   {
	
	
	public static <T> void checkNulls(final Iterable<T> values) {
		if (values == null) return;

		int i = 0;
		for (final T value : values) {
			if (value == null) throw new IllegalArgumentException("Element at index " + i + " was a null value, which is not permitted.");
			i++;
		}
	}

	public static <T> void checkNulls(final T[] values) {
		if (values == null) return;

		int i = 0;
		for (final T value : values) {
			if (value == null) throw new IllegalArgumentException("Element at index " + i + " was a null value, which is not permitted.");
			i++;
		}
	}
	
	public static void checkScale(final BigDecimal value, final int scale) {
		try {
			if (value != null) value.setScale(scale);
		} catch (final ArithmeticException e) {
			throw new IllegalArgumentException("Decimal places allowed: " + scale + ". Value: " + value, e);
		}
	}

	public static void checkScale(final BigDecimal[] values, final int scale) {
		if (values == null) return;

		int i = 0;
		for (final BigDecimal value : values) {
			try {
				if (value != null) value.setScale(scale);
			} catch (final ArithmeticException e) {
				throw new IllegalArgumentException("Invalid value for element at index " + i + ". Decimal places allowed: " + scale + ". Value: " + value, e);
			}

			i++;
		}
	}

	public static void checkScale(final Iterable<BigDecimal> values, final int scale) {
		if (values == null) return;

		int i = 0;
		for (final BigDecimal value : values) {
			try {
				if (value != null) value.setScale(scale);
			} catch (final ArithmeticException e) {
				throw new IllegalArgumentException(
						"Invalid value for element at index " + i + ". Decimal places allowed: " + scale + ". Value: " + value, e);
			}

			i++;
		}
	}

	public static BigDecimal setScale(final BigDecimal value, final int scale) {
		return value.setScale(scale, BigDecimal.ROUND_HALF_UP);
	}

	public static List<BigDecimal> setScale(final List<BigDecimal> values, final int scale) {
		if (values == null) return null;

		final ArrayList<BigDecimal> result = new ArrayList<BigDecimal>(values.size());
		for (final BigDecimal value : values) result.add(value != null ? setScale(value, scale) : null);
		return result;
	}

	public static Set<BigDecimal> setScale(final Set<BigDecimal> values, final int scale) {
		if (values == null) return null;

		final HashSet<BigDecimal> result = new HashSet<BigDecimal>(values.size());
		for (final BigDecimal value : values) result.add(value != null ? setScale(value, scale) : null);
		return result;
	}

	public static BigDecimal[] setScale(final BigDecimal[] values, final int scale) {
		if (values == null) return null;

		final BigDecimal[] result = new BigDecimal[values.length];
		int i = 0;
		for (final BigDecimal value : values) result[i++] = value != null ? setScale(value, scale) : null;
		return result;
	}
	
	public static void checkLength(final String value, final int length) {
		if (value != null && value.length() > length) throw new IllegalArgumentException(
				"Maximum length allowed: " + length + ". Value: " + value);
	}

	public static void checkLength(final Iterable<String> values, final int length) {
		if (values == null) return;

		int i = 0;
		for (final String value : values) {
			if (value != null && value.length() > length) throw new IllegalArgumentException(
					"Invalid value for element at index " + i + ". Maximum length allowed: " + length + ". Value: " + value);
			i++;
		}
	}

	public static void checkLength(final String[] values, final int length) {
		if (values == null) return;

		int i = 0;
		for (final String value : values) {
			if (value != null && value.length() > length) throw new IllegalArgumentException(
					"Invalid value for element at index " + i + ". Maximum length allowed: " + length + ". Value: " + value);
			i++;
		}
	}
	
	public static boolean compareBigDecimal(final Iterable<BigDecimal> left, final Iterable<BigDecimal> right) {
		if (left == null && right == null) return true;
		if (left == null || right == null) return false;

		final Iterator<BigDecimal> leftIterator = left.iterator();
		final Iterator<BigDecimal> rightIterator = right.iterator();

		while (leftIterator.hasNext() && rightIterator.hasNext()) {
			final BigDecimal l = leftIterator.next();
			final BigDecimal r = rightIterator.next();
			if (!(l == r || l != null && r != null && l.compareTo(r) == 0)) return false;
		}

		return leftIterator.hasNext() == rightIterator.hasNext();
	}

	public static boolean compareBigDecimal(final BigDecimal[] left, final BigDecimal[] right) {
		if (left == null && right == null) return true;
		if (left == null || right == null) return false;

		if (left.length != right.length) return false;
		for (int i = 0; i < left.length; i++) {
			final BigDecimal l = left[i];
			final BigDecimal r = right[i];
			if (!(l == r || l != null && r != null && l.compareTo(r) == 0)) return false;
		}
		return true;
	}

	private static final Comparator<BigDecimal> bigDecimalComparator = new Comparator<BigDecimal>() {
		@Override
		public int compare(final BigDecimal left, final BigDecimal right) {
			return left == null && right == null ? 0
				 : left == null ? -1
				 : right == null? 1
				 : left.compareTo(right);
		}
	};

	public static boolean compareBigDecimal(final Set<BigDecimal> left, final Set<BigDecimal> right) {
		if (left == null && right == null) return true;
		if (left == null || right == null) return false;

		if (left.size() != right.size()) return false;

		final BigDecimal[] leftSorted = left.toArray(new BigDecimal[left.size()]);
		Arrays.sort(leftSorted, bigDecimalComparator);
		final BigDecimal[] rightSorted = right.toArray(new BigDecimal[right.size()]);
		Arrays.sort(rightSorted, bigDecimalComparator);

		return compareBigDecimal(leftSorted, rightSorted);
	}
	
	public static boolean compareBinary(final Iterable<byte[]> left, final Iterable<byte[]> right) {
		if (left == null && right == null) return true;
		if (left == null || right == null) return false;

		final Iterator<byte[]> leftIterator = left.iterator();
		final Iterator<byte[]> rightIterator = right.iterator();

		while (leftIterator.hasNext() && rightIterator.hasNext()) {
			final byte[] l = leftIterator.next();
			final byte[] r = rightIterator.next();
			if (!Arrays.equals(l, r)) return false;
		}

		return leftIterator.hasNext() == rightIterator.hasNext();
	}

	public static boolean compareBinary(final byte[][] left, final byte[][] right) {
		if (left == null && right == null) return true;
		if (left == null || right == null) return false;

		if (left.length != right.length) return false;

		for (int i = 0; i < left.length; i++) {
			if (!Arrays.equals(left[i], right[i])) return false;
		}
		return true;
	}

	public static boolean compareBinary(final Set<byte[]> left, final Set<byte[]> right) {
		if (left == null && right == null) return true;
		if (left == null || right == null) return false;

		if (left.size() != right.size()) return false;

		loop:
		for (final byte[] l : left) {
			for (final byte[] r : right) {
				if (Arrays.equals(l, r)) continue loop;
			}
			return false;
		}
		return true;
	}
}
