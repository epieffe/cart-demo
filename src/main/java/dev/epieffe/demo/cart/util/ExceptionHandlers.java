package dev.epieffe.demo.cart.util;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlers {

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorDto handleValidationError(MethodArgumentNotValidException ex) {
		FieldError error = ex.getBindingResult().getFieldError();
		String msg = error != null && error.getDefaultMessage() != null ? error.getDefaultMessage() : "Validation error";
		return new ErrorDto(msg);
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorDto handleBusinessException(CartBusinessException ex) {
		return new ErrorDto(ex.getMessage());
	}
}
