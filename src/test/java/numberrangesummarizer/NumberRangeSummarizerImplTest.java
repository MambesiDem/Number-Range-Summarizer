package numberrangesummarizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class NumberRangeSummarizerImplTest {

    private NumberRangeSummarizer summarizer;

    @BeforeEach
    void setUp() {
        summarizer = new NumberRangeSummarizerImpl();
    }

    private static List<Integer> list(Integer... values) {
        return new ArrayList<>(Arrays.asList(values));
    }

    private String summarize(String input) {
        return summarizer.summarizeCollection(summarizer.collect(input));
    }

    @Nested
    @DisplayName("collect")
    class Collect {

        @Test
        void parsesCommaSeparatedNumbers() {
            assertEquals(list(1, 3, 6, 7, 8), new ArrayList<>(summarizer.collect("1,3,6,7,8")));
        }

        @Test
        @DisplayName("Assumption: whitespace around numbers is ignored")
        void ignoresWhitespace() {
            assertEquals(list(1, 2, 3), new ArrayList<>(summarizer.collect(" 1 , 2,3  ")));
        }

        @Test
        @DisplayName("Assumption: null, empty and blank input contain no numbers")
        void emptyInputGivesEmptyCollection() {
            assertTrue(summarizer.collect(null).isEmpty());
            assertTrue(summarizer.collect("").isEmpty());
            assertTrue(summarizer.collect("   ").isEmpty());
        }

        @Test
        @DisplayName("Assumption: duplicates are collapsed and the result is ascending")
        void sortsAndRemovesDuplicates() {
            assertEquals(list(1, 2, 5), new ArrayList<>(summarizer.collect("5,1,2,2,1")));
        }

        @Test
        @DisplayName("Assumption: negative numbers are supported")
        void supportsNegativeNumbers() {
            assertEquals(list(-3, -1, 0), new ArrayList<>(summarizer.collect("-1,0,-3")));
        }

        @Test
        @DisplayName("Assumption: non-numeric tokens are rejected")
        void rejectsNonNumericTokens() {
            assertThrows(IllegalArgumentException.class, () -> summarizer.collect("1,two,3"));
            assertThrows(IllegalArgumentException.class, () -> summarizer.collect("1.5,2"));
        }

        @Test
        @DisplayName("Assumption: empty tokens (e.g. trailing or double commas) are rejected")
        void rejectsEmptyTokens() {
            assertThrows(IllegalArgumentException.class, () -> summarizer.collect("1,2,"));
            assertThrows(IllegalArgumentException.class, () -> summarizer.collect("1,,2"));
        }

        @Test
        @DisplayName("Assumption: values outside the int range are rejected")
        void rejectsOutOfRangeValues() {
            assertThrows(IllegalArgumentException.class, () -> summarizer.collect("2147483648"));
        }
    }

    @Nested
    @DisplayName("summarizeCollection")
    class Summarize {

        @Test
        void matchesTheSampleFromTheInterface() {
            assertEquals("1, 3, 6-8, 12-15, 21-24, 31",
                    summarize("1,3,6,7,8,12,13,14,15,21,22,23,24,31"));
        }

        @Test
        void emptyCollectionGivesEmptyString() {
            assertEquals("", summarizer.summarizeCollection(Collections.<Integer>emptyList()));
        }

        @Test
        @DisplayName("Assumption: a null collection summarizes to an empty string")
        void nullCollectionGivesEmptyString() {
            assertEquals("", summarizer.summarizeCollection(null));
        }

        @Test
        void singleNumberIsNotARange() {
            assertEquals("5", summarize("5"));
        }

        @Test
        void nonSequentialNumbersAreListedIndividually() {
            assertEquals("1, 3, 5, 7", summarize("1,3,5,7"));
        }

        @Test
        void entireInputCanBeOneRange() {
            assertEquals("1-5", summarize("1,2,3,4,5"));
        }

        @Test
        @DisplayName("Assumption: two consecutive numbers form a range")
        void twoConsecutiveNumbersFormARange() {
            assertEquals("1-2, 4", summarize("1,2,4"));
        }

        @Test
        void rangeAtTheEndOfTheInput() {
            assertEquals("1, 5-7", summarize("1,5,6,7"));
        }

        @Test
        @DisplayName("Assumption: unsorted input is sorted before summarizing")
        void unsortedInputIsSorted() {
            assertEquals("1-3, 9", summarize("9,3,1,2"));
        }

        @Test
        @DisplayName("Assumption: duplicates do not affect the result")
        void duplicatesAreIgnored() {
            assertEquals("1-3, 5", summarize("1,1,2,2,3,5,5"));
        }

        @Test
        @DisplayName("Assumption: duplicates and ordering are also handled for collections passed directly")
        void unsortedCollectionWithDuplicatesIsHandled() {
            assertEquals("1-3, 9", summarizer.summarizeCollection(list(9, 3, 3, 1, 2)));
        }

        @Test
        void rangesCanSpanZeroWithNegativeNumbers() {
            assertEquals("-2-1, 4", summarize("-2,-1,0,1,4"));
        }

        @Test
        void extremeIntValuesDoNotOverflow() {
            Collection<Integer> extremes = list(Integer.MIN_VALUE, Integer.MAX_VALUE);
            assertEquals(Integer.MIN_VALUE + ", " + Integer.MAX_VALUE,
                    summarizer.summarizeCollection(extremes));
        }

        @Test
        @DisplayName("Assumption: a collection containing null is rejected")
        void collectionWithNullIsRejected() {
            assertThrows(IllegalArgumentException.class,
                    () -> summarizer.summarizeCollection(list(1, null, 3)));
        }

        @Test
        void doesNotModifyTheCollectionPassedIn() {
            List<Integer> input = list(3, 1, 2, 2);
            summarizer.summarizeCollection(input);
            assertEquals(list(3, 1, 2, 2), input);
        }
    }
}
