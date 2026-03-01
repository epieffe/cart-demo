package dev.epieffe.demo.cart.order;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderMapperTest {

	@Test
	void fromRequest_shouldRoundUpNetPrice_whenThirdDigitIs5() {
		var products = List.of(
				new ProductQueryDto(1L, "", new BigDecimal("100.05"), new BigDecimal("0.20"))
		);
		var request = new OrderRequest("", List.of(
				new OrderRequest.Product(1L, 1)
		));
		Order o = OrderMapper.fromRequest(request, products);
		assertEquals(new BigDecimal("100.05"), o.getTotalPrice());
		assertEquals(new BigDecimal("16.67"), o.getVatAmount());
		assertEquals(1, o.getProducts().size());

		OrderProduct p1 = o.getProducts().getFirst();
		assertEquals(1L, p1.getProductId());
		assertEquals(1, p1.getQuantity());
		assertEquals(new BigDecimal("0.20"), p1.getVatRate());
		assertEquals(new BigDecimal("100.05"), p1.getTotalPrice());
		assertEquals(new BigDecimal("16.67"), p1.getVatAmount());
	}

	@Test
	void fromRequest_shouldRoundUpNetPrice_whenThirdDigitIsGreaterThan5() {
		var products = List.of(
				new ProductQueryDto(1L, "", new BigDecimal("100.00"), new BigDecimal("0.22"))
		);
		var request = new OrderRequest("", List.of(
				new OrderRequest.Product(1L, 1)
		));
		Order o = OrderMapper.fromRequest(request, products);
		assertEquals(new BigDecimal("100.00"), o.getTotalPrice());
		assertEquals(new BigDecimal("18.03"), o.getVatAmount());
		assertEquals(1, o.getProducts().size());

		OrderProduct p1 = o.getProducts().getFirst();
		assertEquals(1L, p1.getProductId());
		assertEquals(1, p1.getQuantity());
		assertEquals(new BigDecimal("0.22"), p1.getVatRate());
		assertEquals(new BigDecimal("100.00"), p1.getTotalPrice());
		assertEquals(new BigDecimal("18.03"), p1.getVatAmount());
	}

	@Test
	void fromRequest_shouldRoundDownNetPrice_whenThirdDigitIsLessThan5() {
		var products = List.of(
				new ProductQueryDto(1L, "", new BigDecimal("100.02"), new BigDecimal("0.22"))
		);
		var request = new OrderRequest("", List.of(
				new OrderRequest.Product(1L, 1)
		));
		Order o = OrderMapper.fromRequest(request, products);
		assertEquals(new BigDecimal("100.02"), o.getTotalPrice());
		assertEquals(new BigDecimal("18.04"), o.getVatAmount());
		assertEquals(1, o.getProducts().size());

		OrderProduct p1 = o.getProducts().getFirst();
		assertEquals(1L, p1.getProductId());
		assertEquals(1, p1.getQuantity());
		assertEquals(new BigDecimal("0.22"), p1.getVatRate());
		assertEquals(new BigDecimal("100.02"), p1.getTotalPrice());
		assertEquals(new BigDecimal("18.04"), p1.getVatAmount());
	}

	@Test
	void fromRequest_shouldSumPrices() {
		var products = List.of(
				new ProductQueryDto(1L, "", new BigDecimal("33.35"), new BigDecimal("0.20")),
				new ProductQueryDto(2L, "", new BigDecimal("50.13"), new BigDecimal("0.44")),
				new ProductQueryDto(3L, "", new BigDecimal("100.00"), new BigDecimal("0.22")),
				new ProductQueryDto(4L, "", new BigDecimal("100.02"), new BigDecimal("0.22"))
		);
		var request = new OrderRequest("", List.of(
				new OrderRequest.Product(1L, 3),
				new OrderRequest.Product(2L, 2),
				new OrderRequest.Product(3L, 1),
				new OrderRequest.Product(4L, 1)
		));
		Order o = OrderMapper.fromRequest(request, products);
		assertEquals(new BigDecimal("400.33"), o.getTotalPrice());
		assertEquals(new BigDecimal("83.37"), o.getVatAmount());
		assertEquals(4, o.getProducts().size());

		OrderProduct p1 = o.getProducts().getFirst();
		assertEquals(1L, p1.getProductId());
		assertEquals(3, p1.getQuantity());
		assertEquals(new BigDecimal("0.20"), p1.getVatRate());
		assertEquals(new BigDecimal("100.05"), p1.getTotalPrice());
		assertEquals(new BigDecimal("16.67"), p1.getVatAmount());

		OrderProduct p2 = o.getProducts().get(1);
		assertEquals(2L, p2.getProductId());
		assertEquals(2, p2.getQuantity());
		assertEquals(new BigDecimal("0.44"), p2.getVatRate());
		assertEquals(new BigDecimal("100.26"), p2.getTotalPrice());
		assertEquals(new BigDecimal("30.63"), p2.getVatAmount());

		OrderProduct p3 = o.getProducts().get(2);
		assertEquals(3L, p3.getProductId());
		assertEquals(1, p3.getQuantity());
		assertEquals(new BigDecimal("0.22"), p3.getVatRate());
		assertEquals(new BigDecimal("100.00"), p3.getTotalPrice());
		assertEquals(new BigDecimal("18.03"), p3.getVatAmount());

		OrderProduct p4 = o.getProducts().get(3);
		assertEquals(4L, p4.getProductId());
		assertEquals(1, p4.getQuantity());
		assertEquals(new BigDecimal("0.22"), p4.getVatRate());
		assertEquals(new BigDecimal("100.02"), p4.getTotalPrice());
		assertEquals(new BigDecimal("18.04"), p4.getVatAmount());
	}
}
