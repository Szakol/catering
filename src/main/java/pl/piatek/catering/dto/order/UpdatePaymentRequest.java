package pl.piatek.catering.dto.order;

import jakarta.validation.constraints.NotNull;

public record UpdatePaymentRequest(@NotNull PaymentStatus paymentStatus) {
}