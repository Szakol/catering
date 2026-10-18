package pl.piatek.catering.dto.order;

import pl.piatek.catering.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderAdminResponse(
        Long id,
        OrderStatus status,
        PaymentStatus paymentStatus,
        PaymentMethod paymentMethod,
        LocalDateTime paidAt,
        OrderSource source,
        LocalDateTime createdAt,
        String customerName,
        String customerPhone,
        String customerEmail,
        FulfillmentType fulfillmentType,
        String deliveryAddress,
        LocalDateTime fulfillmentAt,
        String notes,
        BigDecimal totalPrice,
        BigDecimal totalCost,
        BigDecimal margin,
        List<OrderBoxResponse> boxes) {

    public static OrderAdminResponse from(Order o) {
        return new OrderAdminResponse(
                o.getId(), o.getStatus(), o.getPaymentStatus(), o.getPaymentMethod(), o.getPaidAt(),
                o.getSource(), o.getCreatedAt(),
                o.getCustomerName(), o.getCustomerPhone(), o.getCustomerEmail(),
                o.getFulfillmentType(), o.getDeliveryAddress(), o.getFulfillmentAt(), o.getNotes(),
                o.getTotalPrice(), o.getTotalCost(), o.getTotalPrice().subtract(o.getTotalCost()),
                o.getBoxes().stream()
                        .map(b -> new OrderBoxResponse(
                                b.getQuantity(), b.unitPrice(), b.unitCost(),
                                b.getItems().stream()
                                        .map(i -> new OrderItemResponse(i.getMenuItemId(), i.getName(), i.getPrice(), i.getCost()))
                                        .toList()))
                        .toList());
    }
}