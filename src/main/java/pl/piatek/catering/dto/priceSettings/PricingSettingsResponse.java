package pl.piatek.catering.dto.priceSettings;

import java.math.BigDecimal;

public record PricingSettingsResponse(BigDecimal defaultMarginPercent) {
}