package pl.piatek.catering.dto.sms;

import pl.piatek.catering.dto.order.OrderStatus;

public enum SmsType {
    ORDER_RECEIVED, ORDER_CONFIRMED, ORDER_IN_PROGRESS, ORDER_READY_FOR_PICKUP,
    ORDER_OUT_FOR_DELIVERY, ORDER_CANCELLED, PAYMENT_RECEIVED, PAYMENT_REMINDER;

    /** SMS dla danego statusu zamówienia albo null, jeśli status nie wysyła wiadomości. */
    public static SmsType forStatus(OrderStatus status) {
        return switch (status) {
            case CONFIRMED -> ORDER_CONFIRMED;
            case IN_PROGRESS -> ORDER_IN_PROGRESS;
            case READY_FOR_PICKUP -> ORDER_READY_FOR_PICKUP;
            case OUT_FOR_DELIVERY -> ORDER_OUT_FOR_DELIVERY;
            case CANCELLED -> ORDER_CANCELLED;
            case NEW, COMPLETED -> null;
        };
    }
}