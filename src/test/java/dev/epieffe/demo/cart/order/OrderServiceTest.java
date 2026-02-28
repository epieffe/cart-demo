package dev.epieffe.demo.cart.order;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(
		classes = OrderService.class,
		webEnvironment = SpringBootTest.WebEnvironment.NONE
)
public class OrderServiceTest {

	@Autowired
	private OrderService orderService;

	@MockitoBean
	private OrderRepository orderRepository;

	@Test
	void createOrder_shouldMapOrder() throws InvalidOrderException {
		OrderRequest request = new OrderRequest("via Roma, 5", List.of(
				new OrderRequest.Product(1L, 2),
				new OrderRequest.Product(2L, 1)));
		when(orderRepository.findProductsByIdIn(List.of(1L, 2L))).thenReturn(List.of(
				new ProductQueryDto(1L, "iPhone", new BigDecimal("999.99"), new BigDecimal("0.22")),
				new ProductQueryDto(2L, "Apple Watch", new BigDecimal("150"), new BigDecimal("0.21"))
		));
		when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));
		Order o = orderService.createOrder(request);
		assertEquals("via Roma, 5", o.getShippingAddress());
		assertEquals(2, o.getProducts().size());

		OrderProduct p1 = o.getProducts().getFirst();
		assertSame(o, p1.getOrder());
		assertEquals(1L, p1.getProductId());
		assertEquals(2, p1.getQuantity());
		assertEquals("iPhone", p1.getName());
		assertEquals(1999.98, p1.getTotalPrice().doubleValue());
		assertEquals(360.65, p1.getVatAmount().doubleValue());
		assertEquals(0.22, p1.getVatRate().doubleValue());

		OrderProduct p2 = o.getProducts().get(1);
		assertSame(o, p2.getOrder());
		assertEquals(2L, p2.getProductId());
		assertEquals(1, p2.getQuantity());
		assertEquals("Apple Watch", p2.getName());
		assertEquals(150, p2.getTotalPrice().doubleValue());
		assertEquals(26.03, p2.getVatAmount().doubleValue());
		assertEquals(0.21, p2.getVatRate().doubleValue());
	}

	@Test
	void createOrderWithDuplicateProducts_shouldThrowException() {
		OrderRequest request = new OrderRequest("via Roma, 5", List.of(
				new OrderRequest.Product(1L, 2),
				new OrderRequest.Product(1L, 1)));
		assertThrows(
				InvalidOrderException.class,
				() -> orderService.createOrder(request),
				"Duplicate product: 1"
		);
	}

	@Test
	void createOrderWithNonExistingProduct_shouldThrowException() {
		OrderRequest request = new OrderRequest("via Roma, 5", List.of(
				new OrderRequest.Product(1L, 2),
				new OrderRequest.Product(2L, 1),
				new OrderRequest.Product(3L, 4)));
		when(orderRepository.findProductsByIdIn(List.of(1L, 2L))).thenReturn(List.of(
				new ProductQueryDto(1L, "iPhone", new BigDecimal("999.99"), new BigDecimal("0.22")),
				new ProductQueryDto(3L, "Apple Watch", new BigDecimal("150"), new BigDecimal("0.21"))
		));
		assertThrows(
				InvalidOrderException.class,
				() -> orderService.createOrder(request),
				"Product not found: 1"
		);
	}
}
