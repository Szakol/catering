package pl.piatek.catering.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.piatek.catering.dto.order.CreateOrderRequest;
import pl.piatek.catering.dto.order.OrderConfirmationResponse;
import pl.piatek.catering.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderConfirmationResponse create(@Valid @RequestBody CreateOrderRequest req) {
        return OrderConfirmationResponse.from(service.createFromWeb(req));
    }
}