package numberrangesummarizer;

/**
 * An immutable, inclusive range of consecutive integers.
 * A range with a single member is rendered as that number on its own.
 */
final class NumberRange {

    private static final String RANGE_SEPARATOR = "-";

    private final int start;
    private final int end;

    NumberRange(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException(
                    "Range start (" + start + ") must not be greater than end (" + end + ")");
        }
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return start == end
                ? String.valueOf(start)
                : start + RANGE_SEPARATOR + end;
    }
}
