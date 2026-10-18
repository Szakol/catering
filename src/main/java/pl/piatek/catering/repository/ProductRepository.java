package pl.piatek.catering.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.piatek.catering.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}