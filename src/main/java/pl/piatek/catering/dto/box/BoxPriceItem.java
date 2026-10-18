package pl.piatek.catering.dto.box;

import java.math.BigDecimal;

public record BoxPriceItem(Long id, String name, BigDecimal price) {
}
