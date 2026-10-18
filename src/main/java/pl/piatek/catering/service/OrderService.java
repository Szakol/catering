package pl.piatek.catering.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.piatek.catering.dto.order.CreateOrderRequest;
import pl.piatek.catering.dto.order.FulfillmentType;
import pl.piatek.catering.dto.order.OrderAdminResponse;
import pl.piatek.catering.dto.order.OrderBoxRequest;
import pl.piatek.catering.dto.order.OrderSource;
import pl.piatek.catering.dto.order.OrderStatus;
import pl.piatek.catering.dto.order.PaymentStatus;
import pl.piatek.catering.entity.MenuItem;
import pl.piatek.catering.entity.Order;
import pl.piatek.catering.entity.OrderBox;
import pl.piatek.catering.entity.OrderBoxItem;
import pl.piatek.catering.exception.NotFoundException;
import pl.piatek.catering.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository repository;
    private final BoxPricingService pricingService;

    /** Zamówienie złożone przez klienta przez stronę. */
    public Order createFromWeb(CreateOrderRequest req) {
        return create(req, OrderSource.WEB, OrderStatus.NEW);
    }

    /** Zamówienie wpisane ręcznie przez właściciela (telefon, osobiście); od razu potwierdzone. */
    public Order createByAdmin(CreateOrderRequest req) {
        return create(req, OrderSource.ADMIN, OrderStatus.CONFIRMED);
    }

    @Transactional()
    public List<OrderAdminResponse> findAll(OrderStatus status) {
        List<Order> orders = status == null
                ? repository.findAllByOrderByCreatedAtDesc()
                : repository.findByStatusOrderByCreatedAtDesc(status);
        return orders.stream().map(OrderAdminResponse::from).toList();
    }

    @Transactional()
    public OrderAdminResponse findById(Long id) {
        return OrderAdminResponse.from(get(id));
    }

    public OrderAdminResponse changeStatus(Long id, OrderStatus target) {
        Order order = get(id);

        if (!order.getStatus().canTransitionTo(target)) {
            throw new IllegalArgumentException(
                    "Nie można zmienić statusu z " + order.getStatus() + " na " + target);
        }
        if (target == OrderStatus.READY_FOR_PICKUP && order.getFulfillmentType() != FulfillmentType.PICKUP) {
            throw new IllegalArgumentException("Status gotowe do odbioru dotyczy tylko odbioru osobistego");
        }
        if (target == OrderStatus.OUT_FOR_DELIVERY && order.getFulfillmentType() != FulfillmentType.DELIVERY) {
            throw new IllegalArgumentException("Status w drodze dotyczy tylko dostawy");
        }

        order.setStatus(target);
        // tutaj później podepniemy wysyłkę SMS o zmianie statusu
        return OrderAdminResponse.from(order);
    }

    public OrderAdminResponse changePayment(Long id, PaymentStatus paymentStatus) {
        Order order = get(id);
        order.setPaymentStatus(paymentStatus);
        order.setPaidAt(paymentStatus == PaymentStatus.PAID ? LocalDateTime.now() : null);
        return OrderAdminResponse.from(order);
    }

    // ---------- pomocnicze ----------

    private Order get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Zamówienie " + id + " nie istnieje"));
    }

    private Order create(CreateOrderRequest req, OrderSource source, OrderStatus initialStatus) {
        boolean delivery = req.fulfillmentType() == FulfillmentType.DELIVERY;
        if (delivery && (req.deliveryAddress() == null || req.deliveryAddress().isBlank())) {
            throw new IllegalArgumentException("Dla dostawy do domu wymagany jest adres");
        }

        Order order = new Order();
        order.setCustomerName(req.customerName().trim());
        order.setCustomerPhone(req.customerPhone().trim());
        order.setCustomerEmail(req.customerEmail() == null || req.customerEmail().isBlank()
                ? null : req.customerEmail().trim());
        order.setFulfillmentType(req.fulfillmentType());
        order.setDeliveryAddress(delivery ? req.deliveryAddress().trim() : null);
        order.setFulfillmentAt(req.fulfillmentAt());
        order.setNotes(req.notes());
        order.setPaymentMethod(req.paymentMethod());
        order.setSource(source);
        order.setStatus(initialStatus);

        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        int position = 1;

        for (OrderBoxRequest boxReq : req.boxes()) {
            // ta sama walidacja (5-6 pozycji, bez powtórzeń, tylko aktywne) co przy wycenie
            List<MenuItem> items = pricingService.resolveItems(boxReq.menuItemIds());

            OrderBox box = new OrderBox();
            box.setPosition(position++);
            box.setQuantity(boxReq.quantity());

            for (MenuItem item : items) {
                OrderBoxItem boxItem = new OrderBoxItem();
                boxItem.setMenuItemId(item.getId());
                boxItem.setName(item.getName());
                boxItem.setPrice(item.getPrice());          // snapshot ceny
                boxItem.setCost(item.calculateCost());      // snapshot kosztu
                box.addItem(boxItem);
            }
            order.addBox(box);

            BigDecimal quantity = BigDecimal.valueOf(boxReq.quantity());
            totalPrice = totalPrice.add(box.unitPrice().multiply(quantity));
            totalCost = totalCost.add(box.unitCost().multiply(quantity));
        }

        order.setTotalPrice(totalPrice);
        order.setTotalCost(totalCost);
        return repository.save(order);
    }
}