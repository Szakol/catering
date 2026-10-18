package pl.piatek.catering.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record CreateOrderRequest(
        @NotBlank @Size(max = 150) String customerName,
        @NotBlank @Pattern(regexp = "^[+0-9 ()-]{9,20}$", message = "Niepoprawny numer telefonu") String customerPhone,
        @Email @Size(max = 200) String customerEmail,
        @NotNull FulfillmentType fulfillmentType,
        @Size(max = 300) String deliveryAddress,
        @NotNull @Future LocalDateTime fulfillmentAt,
        @Size(max = 1000) String notes,
        @NotNull PaymentMethod paymentMethod,
        @NotEmpty @Valid List<OrderBoxRequest> boxes) {
}
