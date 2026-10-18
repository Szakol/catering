package pl.piatek.catering.dto.order;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderBoxRequest(
        @Min(1) @Max(1000) int quantity,
        @NotEmpty List<@NotNull Long> menuItemIds) {
}