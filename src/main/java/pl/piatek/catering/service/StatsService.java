package pl.piatek.catering.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.piatek.catering.dto.statistics.CountByLabel;
import pl.piatek.catering.dto.statistics.Granularity;
import pl.piatek.catering.dto.statistics.OrdersBreakdown;
import pl.piatek.catering.dto.statistics.StatsSummary;
import pl.piatek.catering.dto.statistics.TimelinePoint;
import pl.piatek.catering.dto.statistics.TopItem;
import pl.piatek.catering.repository.StatsRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional()
public class StatsService {

    private final StatsRepository repository;

    private record Range(LocalDate from, LocalDate to, LocalDateTime start, LocalDateTime endExclusive) {
    }

    public StatsSummary summary(LocalDate from, LocalDate to) {
        Range r = range(from, to);
        StatsRepository.SummaryRow row = repository.summary(r.start(), r.endExclusive());

        long orders = row.getOrders();
        BigDecimal revenue = row.getRevenue();
        BigDecimal cost = row.getCost();
        BigDecimal margin = revenue.subtract(cost);
        BigDecimal average = orders == 0
                ? BigDecimal.ZERO
                : revenue.divide(BigDecimal.valueOf(orders), 2, RoundingMode.HALF_UP);

        return new StatsSummary(r.from(), r.to(), orders, revenue, cost, margin,
                percent(margin, revenue), average,
                row.getPaidRevenue(), revenue.subtract(row.getPaidRevenue()));
    }

    public List<TimelinePoint> timeline(LocalDate from, LocalDate to, Granularity granularity) {
        Range r = range(from, to);
        return repository.timeline(r.start(), r.endExclusive(), granularity.format()).stream()
                .map(p -> new TimelinePoint(p.getPeriod(), p.getOrders(), p.getRevenue(), p.getCost(),
                        p.getRevenue().subtract(p.getCost())))
                .toList();
    }

    public List<TopItem> topItems(LocalDate from, LocalDate to, int limit) {
        Range r = range(from, to);
        int safeLimit = Math.max(1, Math.min(limit, 100));
        return repository.topItems(r.start(), r.endExclusive(), safeLimit).stream()
                .map(i -> new TopItem(i.getName(), i.getQuantity(), i.getRevenue(), i.getCost(),
                        i.getRevenue().subtract(i.getCost())))
                .toList();
    }

    public OrdersBreakdown breakdown(LocalDate from, LocalDate to) {
        Range r = range(from, to);
        return new OrdersBreakdown(
                repository.countByStatus(r.start(), r.endExclusive()).stream()
                        .map(c -> new CountByLabel(c.getLabel(), c.getTotal())).toList(),
                repository.countBySource(r.start(), r.endExclusive()).stream()
                        .map(c -> new CountByLabel(c.getLabel(), c.getTotal())).toList());
    }

    // ---------- pomocnicze ----------

    private Range range(LocalDate from, LocalDate to) {
        LocalDate end = to != null ? to : LocalDate.now();
        LocalDate start = from != null ? from : end.minusDays(29);
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Data początkowa jest późniejsza niż końcowa");
        }
        return new Range(start, end, start.atStartOfDay(), end.plusDays(1).atStartOfDay());
    }

    private BigDecimal percent(BigDecimal part, BigDecimal whole) {
        if (whole.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return part.multiply(BigDecimal.valueOf(100)).divide(whole, 2, RoundingMode.HALF_UP);
    }
}