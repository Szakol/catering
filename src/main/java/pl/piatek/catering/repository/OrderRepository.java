package pl.piatek.catering.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.piatek.catering.dto.order.OrderStatus;
import pl.piatek.catering.entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByOrderByCreatedAtDesc();

    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);
}
