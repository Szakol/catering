package pl.piatek.catering.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pl.piatek.catering.dto.order.CreateOrderRequest;
import pl.piatek.catering.dto.order.FulfillmentType;
import pl.piatek.catering.dto.order.OrderAdminResponse;
import pl.piatek.catering.dto.order.OrderBoxRequest;
import pl.piatek.catering.dto.order.OrderSource;
import pl.piatek.catering.dto.order.OrderStatus;
import pl.piatek.catering.dto.order.PaymentStatus;
import pl.piatek.catering.dto.sms.OrderNotificationEvent;
import pl.piatek.catering.dto.sms.SmsType;
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
    private final ApplicationEventPublisher events;

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

    /** Wyszukiwanie po numerze z tytułu przelewu (wielkość liter i myślnik nie mają znaczenia). */
    @Transactional()
    public OrderAdminResponse findByCode(String code) {
        String normalized = OrderCodes.normalize(code);
        return repository.findByPublicCode(normalized)
                .map(OrderAdminResponse::from)
                .orElseThrow(() -> new NotFoundException("Zamówienie o numerze " + code + " nie istnieje"));
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

        SmsType smsType = SmsType.forStatus(target);
        if (smsType != null) {
            publish(order, smsType);
        }
        return OrderAdminResponse.from(order);
    }

    public OrderAdminResponse changePayment(Long id, PaymentStatus paymentStatus) {
        Order order = get(id);
        boolean newlyPaid = paymentStatus == PaymentStatus.PAID
                && order.getPaymentStatus() != PaymentStatus.PAID;

        order.setPaymentStatus(paymentStatus);
        order.setPaidAt(paymentStatus == PaymentStatus.PAID ? LocalDateTime.now() : null);

        if (newlyPaid) {
            publish(order, SmsType.PAYMENT_RECEIVED);
        }
        return OrderAdminResponse.from(order);
    }

    // ---------- pomocnicze ----------

    private Order get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Zamówienie " + id + " nie istnieje"));
    }

    private void publish(Order order, SmsType type) {
        events.publishEvent(new OrderNotificationEvent(
                order.getId(), OrderCodes.format(order.getPublicCode()),
                order.getCustomerPhone(), type,
                order.getTotalPrice(), order.getFulfillmentAt()));
    }

    /** Losuje numer, aż trafi na wolny. Unikalność dodatkowo pilnuje ograniczenie w bazie. */
    private String generateUniqueCode() {
        for (int attempt = 0; attempt < 10; attempt++) {
            String code = OrderCodes.generate();
            if (!repository.existsByPublicCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Nie udało się wygenerować unikalnego numeru zamówienia");
    }

    private Order create(CreateOrderRequest req, OrderSource source, OrderStatus initialStatus) {
        boolean delivery = req.fulfillmentType() == FulfillmentType.DELIVERY;
        if (delivery && (req.deliveryAddress() == null || req.deliveryAddress().isBlank())) {
            throw new IllegalArgumentException("Dla dostawy do domu wymagany jest adres");
        }

        Order order = new Order();
        order.setPublicCode(generateUniqueCode());
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

        Order saved = repository.save(order);
        publish(saved, initialStatus == OrderStatus.NEW ? SmsType.ORDER_RECEIVED : SmsType.ORDER_CONFIRMED);
        return saved;
    }
}