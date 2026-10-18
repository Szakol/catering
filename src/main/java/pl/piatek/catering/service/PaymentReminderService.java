package pl.piatek.catering.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.piatek.catering.config.SmsProperties;
import pl.piatek.catering.dto.order.OrderStatus;
import pl.piatek.catering.dto.order.PaymentStatus;
import pl.piatek.catering.dto.sms.OrderNotificationEvent;
import pl.piatek.catering.dto.sms.SmsStatus;
import pl.piatek.catering.dto.sms.SmsType;
import pl.piatek.catering.entity.Order;
import pl.piatek.catering.repository.OrderRepository;
import pl.piatek.catering.repository.SmsLogRepository;
import pl.piatek.catering.service.sender.SmsTemplates;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentReminderService {

    private final OrderRepository orderRepository;
    private final SmsLogRepository smsLogRepository;
    private final SmsService smsService;
    private final SmsTemplates templates;
    private final SmsProperties properties;

    /** Przypomina raz o zamówieniach potwierdzonych, nieopłaconych, jeszcze niezrealizowanych. */
    @Transactional()
    public int sendDueReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<Order> candidates = orderRepository
                .findByPaymentStatusAndStatusInAndCreatedAtBeforeAndFulfillmentAtAfter(
                        PaymentStatus.UNPAID,
                        List.of(OrderStatus.CONFIRMED, OrderStatus.IN_PROGRESS),
                        now.minusHours(properties.paymentReminderAfterHours()),
                        now);

        int sent = 0;
        for (Order order : candidates) {
            if (smsLogRepository.existsByOrderIdAndTypeAndStatus(
                    order.getId(), SmsType.PAYMENT_REMINDER, SmsStatus.SENT)) {
                continue;
            }
            OrderNotificationEvent event = new OrderNotificationEvent(
                    order.getId(), order.getCustomerPhone(), SmsType.PAYMENT_REMINDER,
                    order.getTotalPrice(), order.getFulfillmentAt());
            smsService.send(order.getId(), order.getCustomerPhone(),
                    SmsType.PAYMENT_REMINDER, templates.render(event));
            sent++;
        }
        return sent;
    }
}