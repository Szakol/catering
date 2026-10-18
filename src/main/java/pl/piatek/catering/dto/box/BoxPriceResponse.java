package pl.piatek.catering.dto.box;

import java.math.BigDecimal;
import java.util.List;

public record BoxPriceResponse(int itemCount, List<BoxPriceItem> items, BigDecimal total) {
}