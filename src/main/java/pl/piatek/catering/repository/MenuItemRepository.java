package pl.piatek.catering.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.piatek.catering.entity.MenuItem;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    @EntityGraph(attributePaths = {"ingredients", "ingredients.product"})
    List<MenuItem> findAllByOrderBySortOrderAsc();

    List<MenuItem> findByActiveTrueOrderBySortOrderAsc();

    @EntityGraph(attributePaths = {"ingredients", "ingredients.product"})
    Optional<MenuItem> findWithIngredientsById(Long id);
}