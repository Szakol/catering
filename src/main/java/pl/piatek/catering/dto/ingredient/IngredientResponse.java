package pl.piatek.catering.dto.ingredient;

import pl.piatek.catering.entity.Unit;

import java.math.BigDecimal;

public record IngredientResponse(Long productId, String productName, Unit unit, BigDecimal quantity) {
}
