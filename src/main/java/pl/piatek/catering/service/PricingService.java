package pl.piatek.catering.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.piatek.catering.dto.priceSettings.PricingSettingsResponse;
import pl.piatek.catering.entity.MenuItem;
import pl.piatek.catering.entity.PricingSettings;
import pl.piatek.catering.repository.PricingSettingsRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Transactional
public class PricingService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final PricingSettingsRepository settingsRepository;

    @Transactional()
    public BigDecimal defaultMarginPercent() {
        return settingsRepository.findById(PricingSettings.SINGLETON_ID)
                .orElseThrow(() -> new IllegalStateException("Brak wiersza pricing_settings"))
                .getDefaultMarginPercent();
    }

    public PricingSettingsResponse updateDefaultMargin(BigDecimal percent) {
        PricingSettings s = settingsRepository.findById(PricingSettings.SINGLETON_ID)
                .orElseThrow(() -> new IllegalStateException("Brak wiersza pricing_settings"));
        s.setDefaultMarginPercent(percent);
        return new PricingSettingsResponse(s.getDefaultMarginPercent());
    }

    /** Marża docelowa potrawy: nadpisanie per potrawa albo domyślna. */
    public BigDecimal effectiveMarginPercent(MenuItem item, BigDecimal defaultMargin) {
        return item.getMarginPercent() != null ? item.getMarginPercent() : defaultMargin;
    }

    /** cena = koszt / (1 - marża%) */
    public BigDecimal suggestedPrice(BigDecimal cost, BigDecimal marginPercent) {
        BigDecimal divisor = BigDecimal.ONE.subtract(marginPercent.divide(HUNDRED, 6, RoundingMode.HALF_UP));
        return cost.divide(divisor, 2, RoundingMode.HALF_UP);
    }
}