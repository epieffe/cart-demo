package dev.epieffe.demo.cart.product;

import io.swagger.v3.oas.annotations.Parameter;
import org.jspecify.annotations.Nullable;
import org.springdoc.core.annotations.ParameterObject;

import java.math.BigDecimal;

@ParameterObject
public record ProductSearch(
		@Parameter(description = "Name search", example = "Samsung")
		@Nullable String name,

		@Parameter(description = "Maximum price")
		@Nullable BigDecimal maxPrice,

		@Parameter(description = "Minimum price")
		@Nullable BigDecimal minPrice
) { }
