package dev.epieffe.demo.cart.util;

import jakarta.persistence.metamodel.SingularAttribute;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

public class Specifications {

	public static <T> Specification<T>  stringContains(SingularAttribute<T, String> attribute, String value) {
		return (r, q, cb) ->
				cb.like(cb.lower(r.get(attribute)), "%" + value.toLowerCase() + "%");
	}

	public static <T, R extends Comparable<? super R>> Specification<T>  greaterThan(SingularAttribute<T, R> attribute, R value) {
		return (r, q, cb) -> cb.greaterThan(r.get(attribute), value);
	}

	public static <T, R extends Comparable<? super R>> Specification<T>  lessThan(SingularAttribute<T, R> attribute, R value) {
		return (r, q, cb) -> cb.lessThan(r.get(attribute), value);
	}

	private Specifications() {}
}
