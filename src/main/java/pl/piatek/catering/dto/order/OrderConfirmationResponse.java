package pl.piatek.catering.dto.order;

import pl.piatek.catering.entity.Order;

import java.math.BigDecimal;

public record OrderConfirmationResponse(Long id, OrderStatus status, PaymentMethod paymentMethod, BigDecimal totalPrice) {

    public static OrderConfirmationResponse from(Order o) {
        return new OrderConfirmationResponse(o.getId(), o.getStatus(), o.getPaymentMethod(), o.getTotalPrice());
    }
}
