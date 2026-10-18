package pl.piatek.catering.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.piatek.catering.dto.ingredient.IngredientRequest;
import pl.piatek.catering.dto.ingredient.IngredientResponse;
import pl.piatek.catering.dto.menu.MenuItemAdminResponse;
import pl.piatek.catering.dto.menu.MenuItemPublicResponse;
import pl.piatek.catering.dto.menu.MenuItemRequest;
import pl.piatek.catering.entity.MenuItem;
import pl.piatek.catering.entity.MenuItemIngredient;
import pl.piatek.catering.entity.Product;
import pl.piatek.catering.exception.NotFoundException;
import pl.piatek.catering.repository.MenuItemRepository;
import pl.piatek.catering.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuItemService {

    private final MenuItemRepository repository;
    private final ProductRepository productRepository;
    private final PricingService pricingService;

    // ---------- odczyt ----------

    @Transactional(readOnly = true)
    public List<MenuItemAdminResponse> findAll() {
        BigDecimal defaultMargin = pricingService.defaultMarginPercent(); // jedno zapytanie na całą listę
        return repository.findAllByOrderBySortOrderAsc().stream()
                .map(m -> toAdminResponse(m, defaultMargin))
                .toList();
    }

    @Transactional(readOnly = true)
    public MenuItemAdminResponse findById(Long id) {
        return toAdminResponse(get(id), pricingService.defaultMarginPercent());
    }

    @Transactional(readOnly = true)
    public List<MenuItemPublicResponse> findActive() {
        return repository.findByActiveTrueOrderBySortOrderAsc().stream()
                .map(MenuItemPublicResponse::from)
                .toList();
    }

    // ---------- zapis ----------

    public MenuItemAdminResponse create(MenuItemRequest req) {
        MenuItem item = new MenuItem();
        applyBasics(item, req);
        syncIngredients(item, req.ingredients());
        return toAdminResponse(repository.save(item), pricingService.defaultMarginPercent());
    }

    public MenuItemAdminResponse update(Long id, MenuItemRequest req) {
        MenuItem item = get(id);
        applyBasics(item, req);
        syncIngredients(item, req.ingredients());
        return toAdminResponse(item, pricingService.defaultMarginPercent());
    }

    public void delete(Long id) {
        repository.delete(get(id));
    }

    // ---------- pomocnicze ----------

    private MenuItem get(Long id) {
        return repository.findWithIngredientsById(id)
                .orElseThrow(() -> new NotFoundException("Potrawa " + id + " nie istnieje"));
    }

    private void applyBasics(MenuItem item, MenuItemRequest req) {
        item.setName(req.name());
        item.setDescription(req.description());
        item.setPrice(req.price());
        item.setMarginPercent(req.marginPercent()); // null = użyj marży domyślnej
        item.setActive(req.active());
        item.setSortOrder(req.sortOrder());
    }

    /**
     * Synchronizuje przepis "w miejscu": usuwa zbędne składniki, aktualizuje istniejące, dodaje nowe.
     * Dzięki temu nie łamie unikalnego klucza (menu_item_id, product_id).
     */
    private void syncIngredients(MenuItem item, List<IngredientRequest> requested) {
        Map<Long, BigDecimal> wanted = new LinkedHashMap<>();
        for (IngredientRequest r : requested) {
            if (wanted.put(r.productId(), r.quantity()) != null) {
                throw new IllegalArgumentException(
                        "Produkt " + r.productId() + " występuje w przepisie więcej niż raz");
            }
        }

        // usuń składniki, których nie ma już w przepisie
        item.getIngredients().removeIf(i -> !wanted.containsKey(i.getProduct().getId()));

        // zaktualizuj ilości istniejących
        Set<Long> existing = new HashSet<>();
        for (MenuItemIngredient i : item.getIngredients()) {
            existing.add(i.getProduct().getId());
            i.setQuantity(wanted.get(i.getProduct().getId()));
        }

        // dodaj nowe
        wanted.forEach((productId, quantity) -> {
            if (!existing.contains(productId)) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new NotFoundException("Produkt " + productId + " nie istnieje"));
                item.addIngredient(product, quantity);
            }
        });
    }

    private MenuItemAdminResponse toAdminResponse(MenuItem m, BigDecimal defaultMargin) {
        BigDecimal cost = m.calculateCost();
        BigDecimal targetMargin = pricingService.effectiveMarginPercent(m, defaultMargin);

        return new MenuItemAdminResponse(
                m.getId(), m.getName(), m.getDescription(), m.getPrice(),
                m.isActive(), m.getSortOrder(),
                cost,
                m.calculateMargin(),
                m.calculateMarginPercent(),
                targetMargin,
                m.getMarginPercent() != null,
                pricingService.suggestedPrice(cost, targetMargin),
                m.getIngredients().stream()
                        .map(i -> new IngredientResponse(
                                i.getProduct().getId(), i.getProduct().getName(),
                                i.getProduct().getUnit(), i.getQuantity()))
                        .toList());
    }
}