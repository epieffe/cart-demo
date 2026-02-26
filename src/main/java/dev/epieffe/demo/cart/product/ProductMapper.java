package dev.epieffe.demo.cart.product;

import dev.epieffe.demo.cart.util.PriceUtil;

import java.math.BigDecimal;

public class ProductMapper {

	public static Product fromRequest(ProductRequest request) {
		var product = new Product();
		return updateFromRequest(product, request);
	}

	public static ProductResponse toResponse(Product product) {
		BigDecimal netPrice = PriceUtil.computeNetPrice(product.getTotalPrice(), product.getVatRate());
		BigDecimal vatAmount = product.getTotalPrice().subtract(netPrice);
		return new ProductResponse(
				product.getId(),
				product.getName(),
				product.getDescription(),
				product.getTotalPrice(),
				netPrice,
				vatAmount,
				product.getVatRate());
	}

	public static Product updateFromRequest(Product product, ProductRequest request) {
		product.setName(request.name());
		product.setDescription(request.description());
		product.setTotalPrice(request.totalPrice());
		product.setVatRate(request.vatRate());
		return product;
	}
}
