package dev.epieffe.demo.cart.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
	private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@Operation(
			summary = "Create a new product",
			description = "Create a new product with the provided details and returns the created product.")
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "201",
							description = "Product created successfully",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = ProductResponse.class))),
					@ApiResponse(
							responseCode = "400",
							description = "Invalid product data",
							content = @Content(mediaType = "application/json"))})
	@PostMapping
	public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest request) {
		LOG.info("Received request to create product: {}", request);
		Product product = productService.createProduct(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ProductMapper.toResponse(product));
	}

	@Operation(
			summary = "Get product by id",
			description = "Retrieve a product by its ID.")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "Product found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ProductResponse.class))),
			@ApiResponse(
					responseCode = "404",
					description = "Product not found",
					content = @Content(mediaType = "application/json"))})
	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> getProductById(
			@Parameter(description = "Product id", example = "1") @PathVariable Long id
	) {
		LOG.info("Received request to get product by id: {}", id);
		return productService.getProductById(id)
				.map(ProductMapper::toResponse)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@Operation(
			summary = "Update an existing product",
			description = "Update an existing product with the provided details and returns the updated product.")
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "Product updated successfully",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = ProductResponse.class))),
					@ApiResponse(
							responseCode = "400",
							description = "Invalid product data",
							content = @Content(mediaType = "application/json"))})
	@PutMapping("/{id}")
	public ResponseEntity<ProductResponse> updateProduct(
			@Parameter(description = "Product id", example = "1") @PathVariable Long id,
			@RequestBody @Valid ProductRequest request
	) {
		LOG.info("Received request to update product {}: {}", id, request);
		return productService.updateProduct(id, request)
				.map(ProductMapper::toResponse)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@Operation(
			summary = "Delete product by id",
			description = "Delete a product by its ID.")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "204",
					description = "Product deleted successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ProductResponse.class)))})
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProductById(
			@Parameter(description = "Product id", example = "1") @PathVariable Long id
	) {
		LOG.info("Received request to delete product {}", id);
		productService.deleteProductById(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(
			summary = "Search products",
			description = "Search products with pagination and optional filter criteria.")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "List of matching products",
					content = @Content(
							mediaType = "application/json",
							array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class))))})
	@GetMapping
	public ResponseEntity<List<ProductResponse>> searchProducts(
			ProductSearch search,
			@ParameterObject Pageable pageable
	) {
		LOG.info("Received request to search products: {}, {}", search, pageable);
		Slice<ProductResponse> result = productService.searchProducts(search, pageable)
				.map(ProductMapper::toResponse);
		return ResponseEntity.ok(result.getContent());
	}
}
