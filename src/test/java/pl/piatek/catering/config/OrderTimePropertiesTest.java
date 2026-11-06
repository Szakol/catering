package pl.piatek.catering.config;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTimePropertiesTest {

    private final OrderTimeProperties rules = new OrderTimeProperties(null, null, null, null);
    private final LocalDateTime now = LocalDateTime.of(2026, 11, 6, 15, 0);

    @Test
    void rejectsTomorrow() {
        assertThrows(IllegalArgumentException.class,
                () -> rules.validate(LocalDateTime.of(2026, 11, 7, 12, 0), now));
    }

    @Test
    void acceptsTwoDaysAheadWithinHours() {
        assertDoesNotThrow(() -> rules.validate(LocalDateTime.of(2026, 11, 8, 10, 0), now));
        assertDoesNotThrow(() -> rules.validate(LocalDateTime.of(2026, 11, 8, 18, 0), now));
    }

    @Test
    void rejectsOutsideHours() {
        assertThrows(IllegalArgumentException.class,
                () -> rules.validate(LocalDateTime.of(2026, 11, 8, 9, 59), now));
        assertThrows(IllegalArgumentException.class,
                () -> rules.validate(LocalDateTime.of(2026, 11, 8, 18, 1), now));
    }
}
