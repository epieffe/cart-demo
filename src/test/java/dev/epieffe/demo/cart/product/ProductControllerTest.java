package dev.epieffe.demo.cart.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	ProductService productService;

	@ParameterizedTest
	@MethodSource("productsProvider")
	void getExistingProduct_shouldReturnProduct(Product product) throws Exception {
		when(productService.getProductById(product.getId())).thenReturn(Optional.of(product));
		mockMvc.perform(get("/api/products/" + product.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(product.getId()))
				.andExpect(jsonPath("$.name").value(product.getName()))
				.andExpect(jsonPath("$.description").value(product.getDescription()))
				.andExpect(jsonPath("$.totalPrice").value(product.getTotalPrice().doubleValue()))
				.andExpect(jsonPath("$.vatRate").value(product.getVatRate().doubleValue()))
				.andExpect(jsonPath("$.netPrice").isNumber())
				.andExpect(jsonPath("$.vatAmount").isNumber());
	}

	@Test
	void getNonExistentProduct_shouldReturnNotFound() throws Exception {
		when(productService.getProductById(1L)).thenReturn(Optional.empty());
		mockMvc.perform(get("/api/products/1"))
				.andExpect(status().isNotFound());
	}

	@Test
	void deleteProduct_shouldReturnNoContent() throws Exception {
		doNothing().when(productService).deleteProductById(1L);
		mockMvc.perform(delete("/api/products/1"))
				.andExpect(status().isNoContent());
	}

	@Test
	void createProductWithInvalidPrice_shouldReturnBadRequest() throws Exception {
		String json = """
				{
					"name": "Expensive Keyboard",
					"description": "High quality mechanical keyboard",
					"totalPrice": 100.999,
					"vatRate": 0.22
				}
				""";
		mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Invalid totalPrice"));
	}

	@Test
	void createProductWithNegativePrice_shouldReturnBadRequest() throws Exception {
		String json = """
				{
					"name": "Expensive Keyboard",
					"description": "High quality mechanical keyboard",
					"totalPrice": -1.50,
					"vatRate": 0.22
				}
				""";
		mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Product totalPrice must greater than zero"));
	}

	@Test
	void createProductWithZeroPrice_shouldReturnBadRequest() throws Exception {
		String json = """
				{
					"name": "Expensive Keyboard",
					"description": "High quality mechanical keyboard",
					"totalPrice": 0.00,
					"vatRate": 0.22
				}
				""";
		mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Product totalPrice must greater than zero"));
	}

	@Test
	void createProductWithNegativeVatRate_shouldReturnBadRequest() throws Exception {
		String json = """
				{
					"name": "Expensive Keyboard",
					"description": "High quality mechanical keyboard",
					"totalPrice": 100,
					"vatRate": -0.22
				}
				""";
		mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Product vatRate must greater than zero"));
	}

	@Test
	void createProductWithZeroVatRate_shouldReturnBadRequest() throws Exception {
		String json = """
				{
					"name": "Expensive Keyboard",
					"description": "High quality mechanical keyboard",
					"totalPrice": 100,
					"vatRate": 0.00
				}
				""";
		mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Product vatRate must greater than zero"));
	}

	@Test
	void createProductWithMissingName_shouldReturnBadRequest() throws Exception {
		String json = """
				{
					"description": "High quality mechanical keyboard",
					"totalPrice": 100,
					"vatRate": 0.22
				}
				""";
		mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Product name must be provided"));
	}

	@Test
	void createProductWithMissingDescription_shouldReturnBadRequest() throws Exception {
		String json = """
				{
					"name": "Expensive Keyboard",
					"totalPrice": 100,
					"vatRate": 0.22
				}
				""";
		mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Product description must be provided"));
	}

	@Test
	void createProductWithMissingTotalPrice_shouldReturnBadRequest() throws Exception {
		String json = """
				{
					"name": "Expensive Keyboard",
					"description": "High quality mechanical keyboard",
					"vatRate": 0.22
				}
				""";
		mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Product totalPrice must be provided"));
	}

	@Test
	void createProductWithMissingVatRate_shouldReturnBadRequest() throws Exception {
		String json = """
				{
					"name": "Expensive Keyboard",
					"description": "High quality mechanical keyboard",
					"totalPrice": 100
				}
				""";
		mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Product vatRate must be provided"));
	}

	@Test
	void updateProductWithInvalidPrice_shouldReturnBadRequest() throws Exception {
		String json = """
				{
					"name": "Expensive Keyboard",
					"description": "High quality mechanical keyboard",
					"totalPrice": 100.999,
					"vatRate": 0.22
				}
				""";
		mockMvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Invalid totalPrice"));
	}

	@Test
	void updateProductWithNegativePrice_shouldReturnBadRequest() throws Exception {
		String json = """
				{
					"name": "Expensive Keyboard",
					"description": "High quality mechanical keyboard",
					"totalPrice": -1.50,
					"vatRate": 0.22
				}
				""";
		mockMvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Product totalPrice must greater than zero"));
	}

	@Test
	void updateProductWithZeroPrice_shouldReturnBadRequest() throws Exception {
		String json = """
				{
					"name": "Expensive Keyboard",
					"description": "High quality mechanical keyboard",
					"totalPrice": 0.00,
					"vatRate": 0.22
				}
				""";
		mockMvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Product totalPrice must greater than zero"));
	}

	@Test
	void updateProductWithMissingName_shouldReturnBadRequest() throws Exception {
		String json = """
				{
					"description": "High quality mechanical keyboard",
					"totalPrice": 100,
					"vatRate": 0.22
				}
				""";
		mockMvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Product name must be provided"));
	}

	@Test
	void updateProductWithMissingDescription_shouldReturnBadRequest() throws Exception {
		String json = """
				{
					"name": "Expensive Keyboard",
					"totalPrice": 100,
					"vatRate": 0.22
				}
				""";
		mockMvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Product description must be provided"));
	}

	@Test
	void updateProductWithMissingTotalPrice_shouldReturnBadRequest() throws Exception {
		String json = """
				{
					"name": "Expensive Keyboard",
					"description": "High quality mechanical keyboard",
					"vatRate": 0.22
				}
				""";
		mockMvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Product totalPrice must be provided"));
	}

	@Test
	void updateProductWithMissingVatRate_shouldReturnBadRequest() throws Exception {
		String json = """
				{
					"name": "Expensive Keyboard",
					"description": "High quality mechanical keyboard",
					"totalPrice": 100
				}
				""";
		mockMvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Product vatRate must be provided"));
	}

	static Stream<Product> productsProvider() {
		var p1 = new Product();
		p1.setId(1L);
		p1.setName("iPhone");
		p1.setDescription("Cool smartphone");
		p1.setTotalPrice(new java.math.BigDecimal("999.99"));
		p1.setVatRate(new java.math.BigDecimal("0.22"));

		var p2 = new Product();
		p2.setId(2L);
		p2.setName("Apple Watch");
		p2.setDescription("Nice watch");
		p2.setTotalPrice(new java.math.BigDecimal("150"));
		p2.setVatRate(new java.math.BigDecimal("0.21"));

		var p3 = new Product();
		p3.setId(3L);
		p3.setName("Keyboard");
		p3.setDescription("Keyboard description");
		p3.setTotalPrice(new java.math.BigDecimal("9.58"));
		p3.setVatRate(new java.math.BigDecimal("0.20"));

		return Stream.of(p1, p2, p3);
	}
}
