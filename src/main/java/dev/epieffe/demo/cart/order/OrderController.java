package dev.epieffe.demo.cart.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
	private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@Operation(
			summary = "Create a new order",
			description = "Create a new order with the provided products and quantities and returns the created order.")
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "201",
							description = "Order created successfully",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = OrderResponse.class))),
					@ApiResponse(
							responseCode = "400",
							description = "Invalid order data",
							content = @Content(mediaType = "application/json"))})
	@PostMapping
	public ResponseEntity<OrderResponse> createOrder(
			@RequestBody @Valid OrderRequest request
	) throws InvalidOrderException {
		LOG.info("Received request to create order: {}", request);
		Order order = orderService.createOrder(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(OrderMapper.toResponse(order));
	}

	@Operation(
			summary = "Get order by id",
			description = "Retrieve an order by its ID.")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "Order found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = OrderResponse.class))),
			@ApiResponse(
					responseCode = "404",
					description = "Order not found",
					content = @Content(mediaType = "application/json"))})
	@GetMapping("/{id}")
	public ResponseEntity<OrderResponse> getProductById(
			@Parameter(description = "Order id", example = "1") @PathVariable Long id
	) {
		LOG.info("Received request to get order by id: {}", id);
		return orderService.getOrderById(id)
				.map(OrderMapper::toResponse)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
}
