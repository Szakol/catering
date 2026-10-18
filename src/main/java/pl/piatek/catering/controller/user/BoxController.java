package pl.piatek.catering.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.piatek.catering.dto.box.BoxPriceRequest;
import pl.piatek.catering.dto.box.BoxPriceResponse;
import pl.piatek.catering.service.BoxPricingService;

@RestController
@RequestMapping("/api/box")
@RequiredArgsConstructor
public class BoxController {

    private final BoxPricingService pricingService;

    @PostMapping("/price")
    public BoxPriceResponse price(@Valid @RequestBody BoxPriceRequest request) {
        return pricingService.calculate(request.menuItemIds());
    }
}