package pl.piatek.catering.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_box")
@Getter
@Setter
@NoArgsConstructor
public class OrderBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false)
    private int quantity = 1;

    @OneToMany(mappedBy = "orderBox", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<OrderBoxItem> items = new ArrayList<>();

    public void addItem(OrderBoxItem item) {
        items.add(item);
        item.setOrderBox(this);
    }

    /** Cena jednego boxa (suma pozycji). */
    public BigDecimal unitPrice() {
        return items.stream().map(OrderBoxItem::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Koszt jednego boxa (suma kosztów pozycji). */
    public BigDecimal unitCost() {
        return items.stream().map(OrderBoxItem::getCost).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
