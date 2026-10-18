package pl.piatek.catering.dto.order;

import java.math.BigDecimal;
import java.util.List;

public record OrderBoxResponse(int quantity, BigDecimal unitPrice, BigDecimal unitCost, List<OrderItemResponse> items) {
}