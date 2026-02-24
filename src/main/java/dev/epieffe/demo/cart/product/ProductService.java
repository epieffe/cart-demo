package dev.epieffe.demo.cart.product;

import dev.epieffe.demo.cart.util.Specifications;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public Product createProduct(ProductRequest productRequest) {
		Product entity = ProductMapper.fromRequest(productRequest);
		return productRepository.save(entity);
	}

	public Optional<Product> getProductById(Long id) {
		return productRepository.findById(id);
	}

	public Slice<Product> searchProducts(ProductSearch search, Pageable pageable) {
		Specification<Product> spec = buildSpecification(search);
		return productRepository.findBy(spec, q -> q.slice(pageable));
	}

	public Optional<Product> updateProduct(Long id, ProductRequest request) {
		return productRepository.findById(id)
				.map(p -> ProductMapper.updateFromRequest(p, request))
				.map(productRepository::save);
	}

	public void deleteProductById(Long id) {
		productRepository.deleteById(id);
	}

	private static Specification<Product> buildSpecification(ProductSearch search) {
		Specification<Product> spec = Specification.unrestricted();
		if (search.name() != null) {
			spec = spec.and(Specifications.stringContains(Product_.name, search.name()));
		}
		if (search.minPrice() != null) {
			spec = spec.and(Specifications.greaterThan(Product_.totalPrice, search.minPrice()));
		}
		if (search.maxPrice() != null) {
			spec = spec.and(Specifications.lessThan(Product_.totalPrice, search.maxPrice()));
		}
		return spec;
	}
}
