package pl.piatek.catering.dto.box;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BoxPriceRequest(@NotEmpty List<@NotNull Long> menuItemIds) {
}
