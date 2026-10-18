package pl.piatek.catering.dto.priceSettings;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PricingSettingsRequest(
        @NotNull @DecimalMin("0.00") @DecimalMax("99.99") BigDecimal defaultMarginPercent) {
}
