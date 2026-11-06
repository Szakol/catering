package pl.piatek.catering.service.sender;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.piatek.catering.config.BusinessProperties;
import pl.piatek.catering.dto.sms.OrderNotificationEvent;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class SmsTemplates {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM 'o' HH:mm");

    private final BusinessProperties business;

    public String render(OrderNotificationEvent e) {
        String code = e.orderCode();
        String total = e.total().setScale(2, RoundingMode.HALF_UP).toPlainString().replace('.', ',');
        String when = e.fulfillmentAt().format(DATE);

        return switch (e.type()) {
            case ORDER_RECEIVED -> "Dziekujemy za zamowienie nr " + code + " na kwote " + total
                    + " zl. Damy znac, gdy je potwierdzimy. " + business.name();
            case ORDER_CONFIRMED -> "Zamowienie nr " + code + " potwierdzone. Termin: " + when
                    + ". Do zaplaty: " + total + " zl. W tytule wpisz nr " + code + ". " + business.paymentInfo();
            case ORDER_IN_PROGRESS -> "Zamowienie nr " + code + " jest w trakcie przygotowania. " + business.name();
            case ORDER_READY_FOR_PICKUP -> "Zamowienie nr " + code + " jest gotowe do odbioru. " + business.name();
            case ORDER_OUT_FOR_DELIVERY -> "Zamowienie nr " + code + " jest w drodze do Ciebie. " + business.name();
            case ORDER_CANCELLED -> "Zamowienie nr " + code + " zostalo anulowane. " + business.name();
            case PAYMENT_RECEIVED -> "Otrzymalismy wplate za zamowienie nr " + code + ". Dziekujemy! " + business.name();
            case PAYMENT_REMINDER -> "Przypominamy o platnosci " + total + " zl za zamowienie nr " + code
                    + ". W tytule wpisz nr " + code + ". " + business.paymentInfo();
        };
    }
}