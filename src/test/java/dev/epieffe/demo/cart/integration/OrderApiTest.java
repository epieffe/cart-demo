package dev.epieffe.demo.cart.integration;

import com.jayway.jsonpath.JsonPath;
import dev.epieffe.demo.cart.UseDockerDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@UseDockerDatabase
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, scripts = "/sql/populate_products.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/sql/clean_orders.sql")
public class OrderApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void createOrder_shouldReturnOrder() throws Exception {
		String json = """
				{
				  "shippingAddress": "via Roma, 5",
				  "products": [
				    {
				      "productId": 1,
				      "quantity": 2
				    },
				    {
				      "productId": 2,
				      "quantity": 1
				    }
				  ]
				}
				""";
		mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.shippingAddress").value("via Roma, 5"))
				.andExpect(jsonPath("$.createdAt").exists())
				.andExpect(jsonPath("$.totalPrice").value("1799.99"))
				.andExpect(jsonPath("$.vatAmount").isNumber())
				.andExpect(jsonPath("$.products[0].productId").value(1))
				.andExpect(jsonPath("$.products[0].quantity").value(2))
				.andExpect(jsonPath("$.products[0].name").value("Samsung Galaxy S24"))
				.andExpect(jsonPath("$.products[0].totalPrice").value(1600))
				.andExpect(jsonPath("$.products[0].vatAmount").isNumber())
				.andExpect(jsonPath("$.products[0].vatRate").value("0.22"))
				.andExpect(jsonPath("$.products[1].productId").value(2))
				.andExpect(jsonPath("$.products[1].quantity").value(1))
				.andExpect(jsonPath("$.products[1].name").value("Apple Watch"))
				.andExpect(jsonPath("$.products[1].totalPrice").value(199.99))
				.andExpect(jsonPath("$.products[1].vatAmount").isNumber())
				.andExpect(jsonPath("$.products[1].vatRate").value("0.22"));
	}

	@Test
	void createOrder_shouldPersistOrder() throws Exception {
		// Create an order
		String json = """
				{
				  "shippingAddress": "via Roma, 5",
				  "products": [
				    {
				      "productId": 1,
				      "quantity": 2
				    },
				    {
				      "productId": 2,
				      "quantity": 1
				    }
				  ]
				}
				""";
		MvcResult result = mockMvc.perform(post("/api/orders")
						.contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated())
				.andReturn();

		Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
		// Get the created order
		mockMvc.perform(get("/api/orders/" + id).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.shippingAddress").value("via Roma, 5"))
				.andExpect(jsonPath("$.createdAt").exists())
				.andExpect(jsonPath("$.totalPrice").value("1799.99"))
				.andExpect(jsonPath("$.vatAmount").isNumber())
				.andExpect(jsonPath("$.products[0].productId").value(1))
				.andExpect(jsonPath("$.products[0].quantity").value(2))
				.andExpect(jsonPath("$.products[0].name").value("Samsung Galaxy S24"))
				.andExpect(jsonPath("$.products[0].totalPrice").value(1600))
				.andExpect(jsonPath("$.products[0].vatAmount").isNumber())
				.andExpect(jsonPath("$.products[0].vatRate").value("0.22"))
				.andExpect(jsonPath("$.products[1].productId").value(2))
				.andExpect(jsonPath("$.products[1].quantity").value(1))
				.andExpect(jsonPath("$.products[1].name").value("Apple Watch"))
				.andExpect(jsonPath("$.products[1].totalPrice").value(199.99))
				.andExpect(jsonPath("$.products[1].vatAmount").isNumber())
				.andExpect(jsonPath("$.products[1].vatRate").value("0.22"));
	}

	@Test
	void createOrderWithMissingProducts_shouldReturnBadRequest() throws Exception {
		String json = """
				{
				  "shippingAddress": "via Roma, 5"
				}
				""";
		mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Order must contain at least one product"));
	}

	@Test
	void createOrderWithEmptyProducts_shouldReturnBadRequest() throws Exception {
		String json = """
				{
				  "shippingAddress": "via Roma, 5",
				  "products": []
				}
				""";
		mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Order must contain at least one product"));
	}

	@Test
	void createOrderWithMissingProductId_shouldReturnBadRequest() throws Exception {
		String json = """
				{
				  "shippingAddress": "via Roma, 5",
				  "products": [
				    {
				      "quantity": 2
				    },
				    {
				      "productId": 2,
				      "quantity": 1
				    }
				  ]
				}
				""";
		mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Missing productId for product"));
	}

	@Test
	void createOrderWithMissingQuantity_shouldReturnBadRequest() throws Exception {
		String json = """
				{
				  "shippingAddress": "via Roma, 5",
				  "products": [
				    {
				      "productId": 1
				    },
				    {
				      "productId": 2,
				      "quantity": 1
				    }
				  ]
				}
				""";
		mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Missing quantity for product"));
	}

	@Test
	void createOrderWithZeroQuantity_shouldReturnBadRequest() throws Exception {
		String json = """
				{
				  "shippingAddress": "via Roma, 5",
				  "products": [
				    {
				      "productId": 1,
				      "quantity": 0
				    },
				    {
				      "productId": 2,
				      "quantity": 1
				    }
				  ]
				}
				""";
		mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Product quantity must greater than zero"));
	}

	@Test
	void createOrderWithNegativeQuantity_shouldReturnBadRequest() throws Exception {
		String json = """
				{
				  "shippingAddress": "via Roma, 5",
				  "products": [
				    {
				      "productId": 1,
				      "quantity": -1
				    },
				    {
				      "productId": 2,
				      "quantity": 1
				    }
				  ]
				}
				""";
		mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Product quantity must greater than zero"));
	}

	@Test
	void createOrderForNotExistingProduct_shouldReturnBadRequest() throws Exception {
		String json = """
				{
				  "shippingAddress": "via Roma, 5",
				  "products": [
				    {
				      "productId": 999,
				      "quantity": 1
				    },
				    {
				      "productId": 2,
				      "quantity": 1
				    }
				  ]
				}
				""";
		mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Product not found: 999"));
	}

	@Test
	void createOrderWithDuplicateProducts_shouldReturnBadRequest() throws Exception {
		String json = """
				{
				  "shippingAddress": "via Roma, 5",
				  "products": [
				    {
				      "productId": 1,
				      "quantity": 1
				    },
				    {
				      "productId": 1,
				      "quantity": 1
				    }
				  ]
				}
				""";
		mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Duplicate product: 1"));
	}
}
