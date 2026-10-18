package pl.piatek.catering.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pl.piatek.catering.entity.Unit;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull Unit unit,
        @NotNull @DecimalMin("0.00") BigDecimal costPerUnit,
        boolean active) {
}
