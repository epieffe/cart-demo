package dev.epieffe.demo.cart.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

	@Query("""
			SELECT new dev.epieffe.demo.cart.order.ProductQueryDto(
				p.id as id,
				p.name as name,
				p.totalPrice as totalPrice,
				p.vatRate as vatRate
			)
			FROM Product p
			WHERE p.id IN (:ids)
			""")
	List<ProductQueryDto> findProductsByIdIn(List<Long> ids);

	@Query("""
			SELECT o
			FROM Order o JOIN FETCH o.products
			WHERE o.id = :id
			""")
	Optional<Order> findByIdWithEagerProducts(Long id);
}
