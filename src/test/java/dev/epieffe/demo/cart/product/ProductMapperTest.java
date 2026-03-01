package dev.epieffe.demo.cart.product;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductMapperTest {

	@Test
	void toResponse_shouldRoundUpNetPrice_whenThirdDigitIs5() {
		var p = new Product();
		p.setTotalPrice( new BigDecimal("100.05"));
		p.setVatRate( new BigDecimal("0.20"));
		ProductResponse response = ProductMapper.toResponse(p);
		assertEquals(new BigDecimal("83.38"), response.netPrice());
		assertEquals(new BigDecimal("16.67"), response.vatAmount());
	}

	@Test
	void toResponse_shouldRoundUpNetPrice_whenThirdDigitIsGreaterThan5() {
		var p = new Product();
		p.setTotalPrice( new BigDecimal("100.00"));
		p.setVatRate( new BigDecimal("0.22"));
		ProductResponse response = ProductMapper.toResponse(p);
		assertEquals(new BigDecimal("81.97"), response.netPrice());
		assertEquals(new BigDecimal("18.03"), response.vatAmount());
	}

	@Test
	void toResponse_shouldRoundDownNetPrice_whenThirdDigitIsLessThan5() {
		var p = new Product();
		p.setTotalPrice( new BigDecimal("100.02"));
		p.setVatRate( new BigDecimal("0.22"));
		ProductResponse response = ProductMapper.toResponse(p);
		assertEquals(new BigDecimal("81.98"), response.netPrice());
		assertEquals(new BigDecimal("18.04"), response.vatAmount());
	}
}
