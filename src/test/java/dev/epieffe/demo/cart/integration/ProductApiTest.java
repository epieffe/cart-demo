package dev.epieffe.demo.cart.integration;

import com.jayway.jsonpath.JsonPath;
import dev.epieffe.demo.cart.UseDockerDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@UseDockerDatabase
class ProductApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void createProduct_shouldReturnProduct() throws Exception {
		// Create a product
		String json = """
				{
					"name": "Expensive Keyboard",
					"description": "High quality mechanical keyboard",
					"totalPrice": 100,
					"vatRate": 0.22
				}
				""";
		mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.name").value("Expensive Keyboard"))
				.andExpect(jsonPath("$.description").value("High quality mechanical keyboard"))
				.andExpect(jsonPath("$.totalPrice").value(100))
				.andExpect(jsonPath("$.vatRate").value(0.22))
				.andExpect(jsonPath("$.netPrice").isNumber())
				.andExpect(jsonPath("$.vatAmount").isNumber());
	}

	@Test
	void createProduct_shouldPersistProduct() throws Exception {
		// Create a product
		String postJson = """
				{
					"name": "Expensive Keyboard",
					"description": "High quality mechanical keyboard",
					"totalPrice": 100,
					"vatRate": 0.22
				}
				""";
		MvcResult result = mockMvc.perform(post("/api/products")
						.contentType(MediaType.APPLICATION_JSON)
						.content(postJson))
				.andExpect(status().isCreated())
				.andReturn();

		Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
		// Get the created product
		mockMvc.perform(get("/api/products/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.name").value("Expensive Keyboard"))
				.andExpect(jsonPath("$.description").value("High quality mechanical keyboard"))
				.andExpect(jsonPath("$.totalPrice").value(100))
				.andExpect(jsonPath("$.vatRate").value(0.22))
				.andExpect(jsonPath("$.netPrice").isNumber())
				.andExpect(jsonPath("$.vatAmount").isNumber());
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
				.andExpect(status().isBadRequest());
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
				.andExpect(status().isBadRequest());
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
				.andExpect(status().isBadRequest());
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
				.andExpect(status().isBadRequest());
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
				.andExpect(status().isBadRequest());
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
				.andExpect(status().isBadRequest());
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
				.andExpect(status().isBadRequest());
	}

	@Test
	void updateProduct_shouldReturnProduct() throws Exception {
		// Create a product
		String postJson = """
				{
					"name": "Expensive Keyboard",
					"description": "High quality mechanical keyboard",
					"totalPrice": 100,
					"vatRate": 0.22
				}
				""";
		MvcResult result = mockMvc.perform(post("/api/products")
						.contentType(MediaType.APPLICATION_JSON)
						.content(postJson))
				.andExpect(status().isCreated())
				.andReturn();

		Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
		// Update the product
		String putJson = """
				{
					"name": "Expensive Keyboard",
					"description": "High quality mechanical keyboard",
					"totalPrice": 100,
					"vatRate": 0.22
				}
				""";
		mockMvc.perform(put("/api/products/" + id).contentType(MediaType.APPLICATION_JSON).content(putJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.name").value("Expensive Keyboard"))
				.andExpect(jsonPath("$.description").value("High quality mechanical keyboard"))
				.andExpect(jsonPath("$.totalPrice").value(100))
				.andExpect(jsonPath("$.vatRate").value(0.22))
				.andExpect(jsonPath("$.netPrice").isNumber())
				.andExpect(jsonPath("$.vatAmount").isNumber());
	}

	@Test
	void updateProduct_shouldPersistProduct() throws Exception {
		// Create a product
		String postJson = """
				{
					"name": "Expensive Keyboard",
					"description": "High quality mechanical keyboard",
					"totalPrice": 100,
					"vatRate": 0.22
				}
				""";
		MvcResult result = mockMvc.perform(post("/api/products")
						.contentType(MediaType.APPLICATION_JSON)
						.content(postJson))
				.andExpect(status().isCreated())
				.andReturn();

		Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
		// Update the product
		String putJson = """
				{
					"name": "Expensive Keyboard",
					"description": "High quality mechanical keyboard",
					"totalPrice": 100,
					"vatRate": 0.22
				}
				""";
		mockMvc.perform(put("/api/products/" + id).contentType(MediaType.APPLICATION_JSON).content(putJson))
				.andExpect(status().isOk());

		// Get the updated product
		mockMvc.perform(get("/api/products/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.name").value("Expensive Keyboard"))
				.andExpect(jsonPath("$.description").value("High quality mechanical keyboard"))
				.andExpect(jsonPath("$.totalPrice").value(100))
				.andExpect(jsonPath("$.vatRate").value(0.22))
				.andExpect(jsonPath("$.netPrice").isNumber())
				.andExpect(jsonPath("$.vatAmount").isNumber());
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
				.andExpect(status().isBadRequest());
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
				.andExpect(status().isBadRequest());
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
				.andExpect(status().isBadRequest());
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
				.andExpect(status().isBadRequest());
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
				.andExpect(status().isBadRequest());
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
				.andExpect(status().isBadRequest());
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
				.andExpect(status().isBadRequest());
	}

	@Test
	void deleteUnexistingProduct_shouldReturnNoContent() throws Exception {
		mockMvc.perform(delete("/api/products/1"))
				.andExpect(status().isNoContent());
	}

	@Test
	void deleteProduct_shouldRemoveProduct() throws Exception {
		// Create a product
		String postJson = """
				{
					"name": "Expensive Keyboard",
					"description": "High quality mechanical keyboard",
					"totalPrice": 100,
					"vatRate": 0.22
				}
				""";
		MvcResult result = mockMvc.perform(post("/api/products")
						.contentType(MediaType.APPLICATION_JSON)
						.content(postJson))
				.andExpect(status().isCreated())
				.andReturn();

		Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
		// Get the product and expect 200 OK
		mockMvc.perform(get("/api/products/" + id))
				.andExpect(status().isOk());

		// Delete the product
		mockMvc.perform(delete("/api/products/" + id))
				.andExpect(status().isNoContent());

		// Get the product and expect 404 Not Found
		mockMvc.perform(get("/api/products/" + id))
				.andExpect(status().isNotFound());
	}
}
