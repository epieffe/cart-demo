package dev.epieffe.demo.cart.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceUtil {

	private static final int SCALE = 2;

	public static BigDecimal computeNetPrice(BigDecimal totalPrice, BigDecimal vatRate) {
		return totalPrice.divide(BigDecimal.ONE.add(vatRate), SCALE, RoundingMode.HALF_UP);
	}

	public static BigDecimal computeVatAmount(BigDecimal totalPrice, BigDecimal vatRate) {
		BigDecimal netPrice = computeNetPrice(totalPrice, vatRate);
		return totalPrice.subtract(netPrice);
	}

	private PriceUtil() {}
}
