package pl.piatek.catering.dto.ingredient;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record IngredientRequest(
        @NotNull Long productId,
        @NotNull @DecimalMin("0.001") BigDecimal quantity) {
}