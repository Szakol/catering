package pl.piatek.catering.dto.statistics;

import java.math.BigDecimal;

public record TimelinePoint(String period, long orders, BigDecimal revenue, BigDecimal cost, BigDecimal margin) {
}