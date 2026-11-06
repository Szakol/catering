package pl.piatek.catering.dto.sms;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderNotificationEvent(Long orderId, String orderCode, String phone, SmsType type,
                                     BigDecimal total, LocalDateTime fulfillmentAt) {
}
