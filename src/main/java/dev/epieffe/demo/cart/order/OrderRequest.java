package dev.epieffe.demo.cart.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

@Schema(description = "Order request payload")
public record OrderRequest(
		@Schema(description = "Shipping address", example = "via Roma, 5")
		String shippingAddress,

		@Schema(description = "List of products to order")
		@NotEmpty(message = "Order must contain at least one product")
		@Valid List<Product> products
) {
	@Schema(name = "OrderProductRequest")
	public record Product(
			@Schema(description = "Product id", example = "101")
			@NotNull(message = "Missing productId for product")
			Long productId,

			@Schema(description = "Number of products ordered", example = "1")
			@Positive(message = "Product quantity must be greater than zero")
			@NotNull(message = "Missing quantity for product")
			Integer quantity
	) { }
}
