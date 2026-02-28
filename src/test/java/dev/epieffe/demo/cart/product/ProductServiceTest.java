package dev.epieffe.demo.cart.product;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(
		classes = ProductService.class,
		webEnvironment = SpringBootTest.WebEnvironment.NONE
)
public class ProductServiceTest {

	@Autowired
	ProductService productService;

	@MockitoBean
	ProductRepository productRepository;

	@ParameterizedTest
	@MethodSource("productRequestsProvider")
	void createProduct_shouldMapProduct(ProductRequest request) {
		when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
		Product p = productService.createProduct(request);
		assertEquals(request.name(), p.getName());
		assertEquals(request.description(), p.getDescription());
		assertEquals(request.totalPrice(), p.getTotalPrice());
		assertEquals(request.vatRate(), p.getVatRate());
	}

	@ParameterizedTest
	@MethodSource("productRequestsProvider")
	void updateProduct_shouldMapProduct(ProductRequest request) {
		when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
		when(productRepository.findById(eq(1L))).thenReturn(Optional.of(new Product()));
		Product p = productService.updateProduct(1L, request).orElse(null);
		assertNotNull(p);
		assertEquals(request.name(), p.getName());
		assertEquals(request.description(), p.getDescription());
		assertEquals(request.totalPrice(), p.getTotalPrice());
		assertEquals(request.vatRate(), p.getVatRate());
	}

	static Stream<ProductRequest> productRequestsProvider() {
		return Stream.of(
				new ProductRequest("iPhone", "Cool smartphone", new BigDecimal("999.99"), new BigDecimal("0.22")),
				new ProductRequest("Apple Watch", "Nice watch", new BigDecimal("150"), new BigDecimal("0.21")),
				new ProductRequest("Keyboard", "Keyboard description", new BigDecimal("9.58"), new BigDecimal("0.20"))
		);
	}
}
