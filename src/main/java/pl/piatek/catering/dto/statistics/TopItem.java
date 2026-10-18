package pl.piatek.catering.dto.statistics;

import java.math.BigDecimal;

public record TopItem(String name, long quantity, BigDecimal revenue, BigDecimal cost, BigDecimal margin) {
}