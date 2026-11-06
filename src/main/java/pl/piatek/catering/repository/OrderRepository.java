package pl.piatek.catering.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.piatek.catering.dto.order.OrderStatus;
import pl.piatek.catering.dto.order.PaymentStatus;
import pl.piatek.catering.entity.Order;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByOrderByCreatedAtDesc();

    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    boolean existsByPublicCode(String publicCode);

    Optional<Order> findByPublicCode(String publicCode);

    List<Order> findByPaymentStatusAndStatusInAndCreatedAtBeforeAndFulfillmentAtAfter(
            PaymentStatus paymentStatus, Collection<OrderStatus> statuses,
            LocalDateTime createdBefore, LocalDateTime fulfillmentAfter);
}