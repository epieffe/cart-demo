package dev.epieffe.demo.cart.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriceUtilTest {

	@Test
	public void computeNetPrice_shouldRoundUp_whenThirdDigitIs5() {
		BigDecimal totalPrice = new BigDecimal("100.05");
		BigDecimal vatRate = new BigDecimal("0.20");
		BigDecimal net = PriceUtil.computeNetPrice(totalPrice, vatRate);
		assertEquals(new BigDecimal("83.38"), net);
	}

	@Test
	public void computeVatAmount_shouldRoundUp_whenThirdDigitIs5() {
		BigDecimal totalPrice = new BigDecimal("100.05");
		BigDecimal vatRate = new BigDecimal("0.20");
		BigDecimal net = PriceUtil.computeVatAmount(totalPrice, vatRate);
		assertEquals(new BigDecimal("16.67"), net);
	}

	@Test
	public void computeNetPrice_shouldRoundUp_whenThirdDigitGreaterThan5() {
		BigDecimal totalPrice = new BigDecimal("100.00");
		BigDecimal vatRate = new BigDecimal("0.22");
		BigDecimal net = PriceUtil.computeNetPrice(totalPrice, vatRate);
		assertEquals(new BigDecimal("81.97"), net);
	}

	@Test
	public void computeVatAmount_shouldRoundUp_whenThirdDigitGreaterThan5() {
		BigDecimal totalPrice = new BigDecimal("100.00");
		BigDecimal vatRate = new BigDecimal("0.22");
		BigDecimal net = PriceUtil.computeVatAmount(totalPrice, vatRate);
		assertEquals(new BigDecimal("18.03"), net);
	}

	@Test
	public void computeNetPrice_shouldRoundDown_whenThirdDigitLessThan5() {
		BigDecimal totalPrice = new BigDecimal("100.02");
		BigDecimal vatRate = new BigDecimal("0.22");
		BigDecimal net = PriceUtil.computeNetPrice(totalPrice, vatRate);
		assertEquals(new BigDecimal("81.98"), net);
	}

	@Test
	public void computeVatAmount_shouldRoundDown_whenThirdDigitLessThan5() {
		BigDecimal totalPrice = new BigDecimal("100.02");
		BigDecimal vatRate = new BigDecimal("0.22");
		BigDecimal net = PriceUtil.computeVatAmount(totalPrice, vatRate);
		assertEquals(new BigDecimal("18.04"), net);
	}
}
