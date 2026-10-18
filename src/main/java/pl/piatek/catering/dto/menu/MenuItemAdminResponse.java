package pl.piatek.catering.dto.menu;

import pl.piatek.catering.dto.ingredient.IngredientResponse;

import java.math.BigDecimal;
import java.util.List;

public record MenuItemAdminResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        boolean active,
        int sortOrder,
        BigDecimal cost,
        BigDecimal margin,               // price - cost
        BigDecimal marginPercent,        // realna marża w % ceny
        BigDecimal targetMarginPercent,  // obowiązująca marża docelowa
        boolean customTargetMargin,      // true = nadpisana przy potrawie
        BigDecimal suggestedPrice,
        List<IngredientResponse> ingredients) {
}