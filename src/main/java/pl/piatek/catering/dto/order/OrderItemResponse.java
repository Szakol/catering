package pl.piatek.catering.dto.order;

import java.math.BigDecimal;

public record OrderItemResponse(Long menuItemId, String name, BigDecimal price, BigDecimal cost) {
}
