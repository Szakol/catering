package pl.piatek.catering.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.piatek.catering.dto.product.ProductRequest;
import pl.piatek.catering.dto.product.ProductResponse;
import pl.piatek.catering.entity.Product;
import pl.piatek.catering.exception.NotFoundException;
import pl.piatek.catering.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository repository;

    @Transactional()
    public List<ProductResponse> findAll() {
        return repository.findAll(Sort.by("name")).stream().map(ProductResponse::from).toList();
    }

    @Transactional()
    public ProductResponse findById(Long id) {
        return ProductResponse.from(get(id));
    }

    public ProductResponse create(ProductRequest req) {
        Product p = new Product();
        apply(p, req);
        return ProductResponse.from(repository.save(p));
    }

    public ProductResponse update(Long id, ProductRequest req) {
        Product p = get(id);
        apply(p, req);
        return ProductResponse.from(p);
    }

    public void delete(Long id) {
        repository.delete(get(id));
        repository.flush(); // żeby naruszenie FK wyszło tutaj, a nie przy commicie
    }

    private Product get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produkt " + id + " nie istnieje"));
    }

    private void apply(Product p, ProductRequest req) {
        p.setName(req.name());
        p.setUnit(req.unit());
        p.setCostPerUnit(req.costPerUnit());
        p.setActive(req.active());
    }
}