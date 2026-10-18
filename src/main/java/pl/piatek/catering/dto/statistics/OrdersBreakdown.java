package pl.piatek.catering.dto.statistics;

import java.util.List;

public record OrdersBreakdown(List<CountByLabel> byStatus, List<CountByLabel> bySource) {
}