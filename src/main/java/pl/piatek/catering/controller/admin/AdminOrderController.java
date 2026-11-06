package pl.piatek.catering.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.piatek.catering.dto.order.CreateOrderRequest;
import pl.piatek.catering.dto.order.OrderAdminResponse;
import pl.piatek.catering.dto.order.OrderStatus;
import pl.piatek.catering.dto.order.UpdatePaymentRequest;
import pl.piatek.catering.dto.order.UpdateStatusRequest;
import pl.piatek.catering.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService service;

    @GetMapping
    public List<OrderAdminResponse> list(@RequestParam(required = false) OrderStatus status) {
        return service.findAll(status);
    }

    @GetMapping("/{id}")
    public OrderAdminResponse get(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/by-code/{code}")
    public OrderAdminResponse getByCode(@PathVariable String code) {
        return service.findByCode(code);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderAdminResponse create(@Valid @RequestBody CreateOrderRequest req) {
        return OrderAdminResponse.from(service.createByAdmin(req));
    }

    @PatchMapping("/{id}/status")
    public OrderAdminResponse changeStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest req) {
        return service.changeStatus(id, req.status());
    }

    @PatchMapping("/{id}/payment")
    public OrderAdminResponse changePayment(@PathVariable Long id, @Valid @RequestBody UpdatePaymentRequest req) {
        return service.changePayment(id, req.paymentStatus());
    }
}
