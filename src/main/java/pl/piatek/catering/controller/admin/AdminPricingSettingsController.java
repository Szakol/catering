package pl.piatek.catering.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.piatek.catering.dto.priceSettings.PricingSettingsRequest;
import pl.piatek.catering.dto.priceSettings.PricingSettingsResponse;
import pl.piatek.catering.service.PricingService;

@RestController
@RequestMapping("/api/admin/settings/pricing")
@RequiredArgsConstructor
public class AdminPricingSettingsController {

    private final PricingService pricingService;

    @GetMapping
    public PricingSettingsResponse get() {
        return new PricingSettingsResponse(pricingService.defaultMarginPercent());
    }

    @PutMapping
    public PricingSettingsResponse update(@Valid @RequestBody PricingSettingsRequest req) {
        return pricingService.updateDefaultMargin(req.defaultMarginPercent());
    }
}