package pl.piatek.catering.dto.order;

import pl.piatek.catering.entity.Order;
import pl.piatek.catering.service.OrderCodes;

import java.math.BigDecimal;

public record OrderConfirmationResponse(String orderCode, OrderStatus status,
                                        PaymentMethod paymentMethod, BigDecimal totalPrice) {

    public static OrderConfirmationResponse from(Order o) {
        return new OrderConfirmationResponse(OrderCodes.format(o.getPublicCode()),
                o.getStatus(), o.getPaymentMethod(), o.getTotalPrice());
    }
}