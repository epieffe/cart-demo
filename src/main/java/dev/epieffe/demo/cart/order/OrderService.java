package dev.epieffe.demo.cart.order;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

	private final OrderRepository orderRepository;

	public OrderService(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	public Order createOrder(OrderRequest request) throws InvalidOrderException {
		checkDuplicateProducts(request);
		List<Long> productIds = request.products().stream().map(OrderRequest.Product::productId).toList();
		List<ProductQueryDto> products = orderRepository.findProductsByIdIn(productIds);
		checkAllProductsExist(productIds, products);
		Order order = OrderMapper.fromRequest(request, products);
		return orderRepository.save(order);
	}

	public Optional<Order> getOrderById(Long id) {
		return orderRepository.findByIdWithEagerProducts(id);
	}

	private static void checkDuplicateProducts(OrderRequest request) throws InvalidOrderException {
		for (int i = 0; i < request.products().size(); i++) {
			long p1 = request.products().get(i).productId();
			for (int j = i + 1; j < request.products().size(); j++) {
				long p2 = request.products().get(j).productId();
				if (p1 == p2) {
					throw new InvalidOrderException("Duplicate product: " + p1);
				}
			}
		}
	}

	private static void checkAllProductsExist(List<Long> productIds, List<ProductQueryDto> products) throws InvalidOrderException {
		for (Long id : productIds) {
			boolean present = products.stream().map(ProductQueryDto::id).anyMatch(id::equals);
			if (!present) {
				throw new InvalidOrderException("Product not found: " + id);
			}
		}
	}
}
