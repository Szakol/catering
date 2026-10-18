package pl.piatek.catering.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "pricing_settings")
@Getter
@Setter
@NoArgsConstructor
public class PricingSettings {

    public static final long SINGLETON_ID = 1L;

    @Id
    private Long id = SINGLETON_ID;

    @Column(name = "default_margin_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal defaultMarginPercent;
}