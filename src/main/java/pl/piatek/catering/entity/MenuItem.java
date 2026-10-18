package pl.piatek.catering.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu_item")
@Getter
@Setter
@NoArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuItemIngredient> ingredients = new ArrayList<>();

    @Column(name = "margin_percent", precision = 5, scale = 2)
    private BigDecimal marginPercent;

    public void addIngredient(Product product, BigDecimal quantity) {
        MenuItemIngredient ingredient = new MenuItemIngredient();
        ingredient.setMenuItem(this);
        ingredient.setProduct(product);
        ingredient.setQuantity(quantity);
        ingredients.add(ingredient);
    }

    public void removeIngredient(MenuItemIngredient ingredient) {
        ingredients.remove(ingredient);
        ingredient.setMenuItem(null);
    }

    public BigDecimal calculateCost() {
        return ingredients.stream()
                .map(i -> i.getQuantity().multiply(i.getProduct().getCostPerUnit()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateMargin() {
        return price.subtract(calculateCost());
    }

    public BigDecimal calculateMarginPercent() {
        if (price.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return calculateMargin().multiply(BigDecimal.valueOf(100))
                .divide(price, 2, RoundingMode.HALF_UP);
    }
}