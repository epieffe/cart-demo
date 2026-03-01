package dev.epieffe.demo.cart.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlers {
	private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlers.class);

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorDto handleValidationError(MethodArgumentNotValidException ex) {
		FieldError error = ex.getBindingResult().getFieldError();
		String msg = error != null && error.getDefaultMessage() != null ? error.getDefaultMessage() : "Validation error";
		LOG.warn("Handling bad request due to failed validation of a method argument: {}", msg, ex);
		return new ErrorDto(msg);
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorDto handleBusinessException(CartBusinessException ex) {
		LOG.warn("Handling bad request due to business exception {}: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
		return new ErrorDto(ex.getMessage());
	}
}
