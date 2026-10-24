package numberrangesummarizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class NumberRangeTest {

    @Test
    void singleValueRangeIsRenderedAsTheNumber() {
        assertEquals("7", new NumberRange(7, 7).toString());
    }

    @Test
    void multiValueRangeIsRenderedWithADash() {
        assertEquals("6-8", new NumberRange(6, 8).toString());
    }

    @Test
    void startGreaterThanEndIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new NumberRange(8, 6));
    }
}
