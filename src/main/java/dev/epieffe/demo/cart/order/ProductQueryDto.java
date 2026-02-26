package dev.epieffe.demo.cart.order;

import java.math.BigDecimal;

public record ProductQueryDto(
		Long id,
		String name,
		BigDecimal totalPrice,
		BigDecimal vatRate
) { }
