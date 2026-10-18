package pl.piatek.catering.dto.statistics;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StatsSummary(
        LocalDate from, LocalDate to,
        long orders,
        BigDecimal revenue, BigDecimal cost,
        BigDecimal margin, BigDecimal marginPercent,
        BigDecimal averageOrderValue,
        BigDecimal paidRevenue, BigDecimal unpaidRevenue) {
}