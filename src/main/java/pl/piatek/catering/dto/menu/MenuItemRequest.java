package pl.piatek.catering.dto.menu;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pl.piatek.catering.dto.ingredient.IngredientRequest;

import java.math.BigDecimal;
import java.util.List;

public record MenuItemRequest(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 1000) String description,
        @NotNull @DecimalMin("0.00") BigDecimal price,
        @DecimalMin("0.00") @DecimalMax("99.99") BigDecimal marginPercent,
        boolean active,
        int sortOrder,
        @NotNull @Valid List<IngredientRequest> ingredients) {
}