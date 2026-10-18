package pl.piatek.catering.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.piatek.catering.dto.sms.OrderNotificationEvent;
import pl.piatek.catering.service.sender.SmsTemplates;

@Component
@RequiredArgsConstructor
public class OrderNotificationListener {

    private final SmsService smsService;
    private final SmsTemplates templates;

    /** Wysyła SMS dopiero po zatwierdzeniu transakcji zamówienia. */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(OrderNotificationEvent event) {
        smsService.send(event.orderId(), event.phone(), event.type(), templates.render(event));
    }
}