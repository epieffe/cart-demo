package dev.epieffe.demo.cart.order;

import dev.epieffe.demo.cart.util.CartBusinessException;

public class InvalidOrderException extends CartBusinessException {

	public InvalidOrderException(String msg) {
		super(msg);
	}
}
