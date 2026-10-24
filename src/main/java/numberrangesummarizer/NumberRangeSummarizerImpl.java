package numberrangesummarizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Summarizes a list of integers by collapsing runs of sequential numbers into ranges.
 *
 * <p>Assumptions (each one is covered by a unit test):
 * <ul>
 *   <li>Input numbers are separated by commas; whitespace around a number is ignored.</li>
 *   <li>A null, empty or blank input string contains no numbers.</li>
 *   <li>Any other token that is not a valid {@code int} (including an empty token such as
 *       the one after a trailing comma) is an error: {@link IllegalArgumentException}.</li>
 *   <li>Duplicates are collapsed and the result is in ascending order.</li>
 *   <li>A run of two or more consecutive numbers is written as a range, e.g. "1-2".</li>
 *   <li>Negative numbers are supported.</li>
 *   <li>A null collection summarizes to an empty string; a collection containing
 *       null is an error: {@link IllegalArgumentException}.</li>
 * </ul>
 */
public class NumberRangeSummarizerImpl implements NumberRangeSummarizer {

    private static final String INPUT_DELIMITER = ",";
    private static final String OUTPUT_DELIMITER = ", ";

    @Override
    public Collection<Integer> collect(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new TreeSet<>();
        }
        return splitTokens(input).stream()
                .map(this::parseNumber)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String summarizeCollection(Collection<Integer> input) {
        if (input == null) {
            return "";
        }
        return toRanges(sortedDistinct(input)).stream()
                .map(NumberRange::toString)
                .collect(Collectors.joining(OUTPUT_DELIMITER));
    }

    private List<String> splitTokens(String input) {
        //A limit of -1 keeps trailing empty tokens so that "1,2," is rejected, not silently accepted.
        return java.util.Arrays.stream(input.split(INPUT_DELIMITER, -1))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    private int parseNumber(String token) {
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not a valid integer: '" + token + "'", e);
        }
    }

    private List<Integer> sortedDistinct(Collection<Integer> numbers) {
        if (numbers.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Collection must not contain null elements");
        }
        return numbers.stream().distinct().sorted().collect(Collectors.toList());
    }

    private List<NumberRange> toRanges(List<Integer> sortedNumbers) {
        List<NumberRange> ranges = new ArrayList<>();
        int index = 0;
        while (index < sortedNumbers.size()) {
            int start = sortedNumbers.get(index);
            int end = start;
            while (index + 1 < sortedNumbers.size() && isNext(end, sortedNumbers.get(index + 1))) {
                end = sortedNumbers.get(++index);
            }
            ranges.add(new NumberRange(start, end));
            index++;
        }
        return ranges;
    }

    /** Uses long arithmetic so that Integer.MAX_VALUE / MIN_VALUE cannot overflow. */
    private boolean isNext(int current, int candidate) {
        return (long) candidate - current == 1L;
    }
}
