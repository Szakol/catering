package pl.piatek.catering.dto.menu;

import pl.piatek.catering.entity.MenuItem;

import java.math.BigDecimal;

public record MenuItemPublicResponse(Long id, String name, String description, BigDecimal price) {

    public static MenuItemPublicResponse from(MenuItem m) {
        return new MenuItemPublicResponse(m.getId(), m.getName(), m.getDescription(), m.getPrice());
    }
}