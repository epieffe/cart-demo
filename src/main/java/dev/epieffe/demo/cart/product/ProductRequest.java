package dev.epieffe.demo.cart.product;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Payload per la creazione di un prodotto")
public record ProductRequest(
		@Schema(description = "Product name", example = "Samsung Galaxy S21")
		@NotNull(message = "Product name must be provided")
		String name,

		@Schema(description = "Product description", example = "Powerful smartphone with 5G connectivity and advanced camera features.")
		@NotNull(message = "Product description must be provided")
		String description,

		@Schema(description = "Total price (including VAT)", example = "999.99")
		@Digits(integer = Integer.MAX_VALUE, fraction = 2, message = "Invalid totalPrice")
		@Positive(message = "Product totalPrice must greater than zero")
		@NotNull(message = "Product totalPrice must be provided")
		BigDecimal totalPrice,

		@Schema(description = "VAT percentage", example = "0.22")
		@Digits(integer = Integer.MAX_VALUE, fraction = 2, message = "Invalid vatRate")
		@Positive(message = "Product vatRate must greater than zero")
		@NotNull(message = "Product vatRate must be provided")
		BigDecimal vatRate
) { }
