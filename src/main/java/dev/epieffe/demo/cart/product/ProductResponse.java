package dev.epieffe.demo.cart.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Product response payload")
public record ProductResponse(
		@Schema(description = "Product id", example = "101")
		Long id,

		@Schema(description = "Product name", example = "Samsung Galaxy S21")
		String name,

		@Schema(description = "Product description", example = "Powerful smartphone with 5G connectivity and advanced camera features.")
		String description,

		@Schema(description = "Total price (including VAT)", example = "999.99")
		BigDecimal totalPrice,

		@Schema(description = "Net price", example = "819.66")
		BigDecimal netPrice,

		@Schema(description = "VAT amount", example = "180.33")
		BigDecimal vatAmount,

		@Schema(description = "VAT percentage", example = "0.22")
		BigDecimal vatRate
) { }
