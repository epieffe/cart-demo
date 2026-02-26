package dev.epieffe.demo.cart.order;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Order response payload")
public record OrderResponse(
		@Schema(description = "Order id", example = "101")
		Long id,

		@Schema(description = "Shipping address", example = "via Roma, 5")
		String shippingAddress,

		@Schema(description = "Creation time in UTC")
		LocalDateTime createdAt,

		@Schema(description = "Total order price (including VAT)", example = "999.99")
		BigDecimal totalPrice,

		@Schema(description = "VAT amount for the order", example = "180.33")
		BigDecimal vatAmount,

		@Schema(description = "List of ordered products")
		List<Product> products
) {
	@Schema(name = "OrderProductResponse")
	public record Product(
			@Schema(description = "Product id", example = "101")
			Long productId,

			@Schema(description = "Number of products ordered", example = "1")
			Integer quantity,

			@Schema(description = "Product name", example = "Samsung Galaxy S21")
			String name,

			@Schema(description = "Total price (including VAT)", example = "999.99")
			BigDecimal totalPrice,

			@Schema(description = "VAT amount", example = "180.33")
			BigDecimal vatAmount,

			@Schema(description = "VAT percentage", example = "0.22")
			BigDecimal vatRate
	) { }
}
