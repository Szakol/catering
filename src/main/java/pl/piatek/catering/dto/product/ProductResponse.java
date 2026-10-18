package pl.piatek.catering.dto.product;

import pl.piatek.catering.entity.Product;
import pl.piatek.catering.entity.Unit;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, Unit unit, BigDecimal costPerUnit, boolean active) {

    public static ProductResponse from(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getUnit(), p.getCostPerUnit(), p.isActive());
    }
}