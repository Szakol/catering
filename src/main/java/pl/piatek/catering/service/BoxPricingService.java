package pl.piatek.catering.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.piatek.catering.config.BoxProperties;
import pl.piatek.catering.dto.box.BoxPriceItem;
import pl.piatek.catering.dto.box.BoxPriceResponse;
import pl.piatek.catering.entity.MenuItem;
import pl.piatek.catering.repository.MenuItemRepository;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional()
public class BoxPricingService {

    private final MenuItemRepository menuItemRepository;
    private final BoxProperties properties;

    public BoxPriceResponse calculate(List<Long> menuItemIds) {
        List<MenuItem> items = resolveItems(menuItemIds);

        BigDecimal total = items.stream()
                .map(MenuItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BoxPriceItem> lines = items.stream()
                .map(i -> new BoxPriceItem(i.getId(), i.getName(), i.getPrice()))
                .toList();

        return new BoxPriceResponse(items.size(), lines, total);
    }

    /** Waliduje wybór i zwraca potrawy w kolejności wybranej przez klienta. */
    public List<MenuItem> resolveItems(List<Long> menuItemIds) {
        int count = menuItemIds.size();
        if (count < properties.minItems() || count > properties.maxItems()) {
            throw new IllegalArgumentException(
                    "Box musi zawierać od " + properties.minItems() + " do "
                            + properties.maxItems() + " pozycji (wybrano " + count + ")");
        }
        if (new HashSet<>(menuItemIds).size() != count) {
            throw new IllegalArgumentException("Ta sama potrawa nie może być wybrana więcej niż raz");
        }

        Map<Long, MenuItem> byId = menuItemRepository.findAllById(menuItemIds).stream()
                .collect(Collectors.toMap(MenuItem::getId, Function.identity()));

        return menuItemIds.stream()
                .map(id -> {
                    MenuItem item = byId.get(id);
                    if (item == null || !item.isActive()) {
                        throw new IllegalArgumentException("Potrawa " + id + " jest niedostępna");
                    }
                    return item;
                })
                .toList();
    }
}
