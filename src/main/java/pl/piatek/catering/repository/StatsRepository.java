package pl.piatek.catering.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import pl.piatek.catering.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends Repository<Order, Long> {

    interface SummaryRow {
        Long getOrders();
        BigDecimal getRevenue();
        BigDecimal getCost();
        BigDecimal getPaidRevenue();
    }

    interface PeriodRow {
        String getPeriod();
        Long getOrders();
        BigDecimal getRevenue();
        BigDecimal getCost();
    }

    interface TopItemRow {
        String getName();
        Long getQuantity();
        BigDecimal getRevenue();
        BigDecimal getCost();
    }

    interface CountRow {
        String getLabel();
        Long getTotal();
    }

    @Query(nativeQuery = true, value = """
            SELECT COUNT(*) AS "orders",
                   COALESCE(SUM(total_price), 0) AS "revenue",
                   COALESCE(SUM(total_cost), 0) AS "cost",
                   COALESCE(SUM(CASE WHEN payment_status = 'PAID' THEN total_price ELSE 0 END), 0) AS "paidRevenue"
            FROM customer_order
            WHERE status <> 'CANCELLED'
              AND fulfillment_at >= :from AND fulfillment_at < :to
            """)
    SummaryRow summary(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(nativeQuery = true, value = """
            SELECT to_char(fulfillment_at, :format) AS "period",
                   COUNT(*) AS "orders",
                   COALESCE(SUM(total_price), 0) AS "revenue",
                   COALESCE(SUM(total_cost), 0) AS "cost"
            FROM customer_order
            WHERE status <> 'CANCELLED'
              AND fulfillment_at >= :from AND fulfillment_at < :to
            GROUP BY 1
            ORDER BY 1
            """)
    List<PeriodRow> timeline(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to,
                             @Param("format") String format);

    @Query(nativeQuery = true, value = """
            SELECT i.name AS "name",
                   SUM(b.quantity) AS "quantity",
                   SUM(i.price * b.quantity) AS "revenue",
                   SUM(i.cost * b.quantity) AS "cost"
            FROM order_box_item i
            JOIN order_box b ON b.id = i.order_box_id
            JOIN customer_order o ON o.id = b.order_id
            WHERE o.status <> 'CANCELLED'
              AND o.fulfillment_at >= :from AND o.fulfillment_at < :to
            GROUP BY i.name
            ORDER BY SUM(b.quantity) DESC, i.name
            LIMIT :limit
            """)
    List<TopItemRow> topItems(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to,
                              @Param("limit") int limit);

    @Query(nativeQuery = true, value = """
            SELECT status AS "label", COUNT(*) AS "total"
            FROM customer_order
            WHERE fulfillment_at >= :from AND fulfillment_at < :to
            GROUP BY status
            ORDER BY COUNT(*) DESC
            """)
    List<CountRow> countByStatus(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(nativeQuery = true, value = """
            SELECT source AS "label", COUNT(*) AS "total"
            FROM customer_order
            WHERE status <> 'CANCELLED'
              AND fulfillment_at >= :from AND fulfillment_at < :to
            GROUP BY source
            ORDER BY COUNT(*) DESC
            """)
    List<CountRow> countBySource(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}