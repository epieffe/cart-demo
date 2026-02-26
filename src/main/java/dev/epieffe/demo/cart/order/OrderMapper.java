package dev.epieffe.demo.cart.order;

import dev.epieffe.demo.cart.util.PriceUtil;

import java.math.BigDecimal;
import java.util.List;

public class OrderMapper {

	public static Order fromRequest(OrderRequest request, List<ProductQueryDto> products) {
		var order = new Order();
		order.setShippingAddress(request.shippingAddress());
		// Add products to order
		for (ProductQueryDto product : products) {
			Integer quantity = request.products().stream()
					.filter(p -> product.id().equals(p.productId()))
					.findFirst()
					.map(OrderRequest.Product::quantity)
					.orElseThrow();

			order.addProduct(fromQueryDto(product, quantity));
		}
		// Compute total price for the order
		BigDecimal totalPrice = order.getProducts().stream()
				.map(OrderProduct::getTotalPrice)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		// Compute VAT amount for the order
		BigDecimal vatAmount = order.getProducts().stream()
				.map(OrderProduct::getVatAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		order.setTotalPrice(totalPrice);
		order.setVatAmount(vatAmount);
		return order;
	}

	public static OrderResponse toResponse(Order order) {
		List<OrderResponse.Product> products = order.getProducts().stream()
				.map(OrderMapper::toProductResponse)
				.toList();
		return new OrderResponse(
				order.getId(),
				order.getShippingAddress(),
				order.getCreatedAt(),
				order.getTotalPrice(),
				order.getVatAmount(),
				products);
	}

	private static OrderProduct fromQueryDto(ProductQueryDto product, Integer quantity) {
		var orderProduct = new OrderProduct();
		orderProduct.setProductId(product.id());
		orderProduct.setQuantity(quantity);
		orderProduct.setName(product.name());
		orderProduct.setTotalPrice(product.totalPrice().multiply(BigDecimal.valueOf(quantity)));
		orderProduct.setVatAmount(PriceUtil.computeVatAmount(orderProduct.getTotalPrice(), product.vatRate()));
		orderProduct.setVatRate(product.vatRate());
		return orderProduct;
	}

	private static OrderResponse.Product toProductResponse(OrderProduct orderProduct) {
		return new OrderResponse.Product(
				orderProduct.getProductId(),
				orderProduct.getQuantity(),
				orderProduct.getName(),
				orderProduct.getTotalPrice(),
				orderProduct.getVatAmount(),
				orderProduct.getVatRate());
	}

	private OrderMapper() { }
}
