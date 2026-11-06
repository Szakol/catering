package pl.piatek.catering.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * Zasady terminu odbioru dla zamówień ze strony: minimalne wyprzedzenie (w dniach kalendarzowych)
 * oraz godziny, w których można odebrać zamówienie (włącznie z krańcami).
 */
@ConfigurationProperties(prefix = "orders")
public record OrderTimeProperties(Integer minDaysAhead, LocalTime pickupFrom, LocalTime pickupTo, ZoneId zone) {

    public OrderTimeProperties {
        if (minDaysAhead == null) minDaysAhead = 2;
        if (pickupFrom == null) pickupFrom = LocalTime.of(10, 0);
        if (pickupTo == null) pickupTo = LocalTime.of(18, 0);
        if (zone == null) zone = ZoneId.of("Europe/Warsaw");
    }

    public void validate(LocalDateTime fulfillmentAt) {
        validate(fulfillmentAt, LocalDateTime.now(zone));
    }

    /** Wersja z podanym "teraz": ułatwia testy. */
    public void validate(LocalDateTime fulfillmentAt, LocalDateTime now) {
        if (fulfillmentAt.toLocalDate().isBefore(now.toLocalDate().plusDays(minDaysAhead))) {
            throw new IllegalArgumentException(
                    "Zamówienie można złożyć z minimum " + minDaysAhead + " dniami wyprzedzenia. "
                            + "Wybierz późniejszy termin odbioru.");
        }
        LocalTime time = fulfillmentAt.toLocalTime();
        if (time.isBefore(pickupFrom) || time.isAfter(pickupTo)) {
            throw new IllegalArgumentException(
                    "Odbiór jest możliwy w godzinach " + pickupFrom + " – " + pickupTo + ".");
        }
    }
}
