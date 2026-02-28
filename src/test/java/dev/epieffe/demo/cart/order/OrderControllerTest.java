package dev.epieffe.demo.cart.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	OrderService orderService;

	@ParameterizedTest
	@MethodSource("ordersProvider")
	void getExistingProduct_shouldReturnProduct(Order order) throws Exception {
		when(orderService.getOrderById(order.getId())).thenReturn(Optional.of(order));
		var result = mockMvc.perform(get("/api/orders/" + order.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(order.getId()))
				.andExpect(jsonPath("$.shippingAddress").value(order.getShippingAddress()))
				.andExpect(jsonPath("$.createdAt").value(order.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME)))
				.andExpect(jsonPath("$.totalPrice").value(order.getTotalPrice().doubleValue()))
				.andExpect(jsonPath("$.vatAmount").value(order.getVatAmount().doubleValue()));
		for (int i = 0; i < order.getProducts().size(); i++) {
			var p = order.getProducts().get(i);
			result.andExpect(jsonPath("$.products[" + i + "].productId").value(p.getProductId()))
					.andExpect(jsonPath("$.products[" + i + "].quantity").value(p.getQuantity()))
					.andExpect(jsonPath("$.products[" + i + "].name").value(p.getName()))
					.andExpect(jsonPath("$.products[" + i + "].totalPrice").value(p.getTotalPrice().doubleValue()))
					.andExpect(jsonPath("$.products[" + i + "].vatAmount").value(p.getVatAmount().doubleValue()))
					.andExpect(jsonPath("$.products[" + i + "].vatRate").value(p.getVatRate().doubleValue()));
		}
	}

	@Test
	void getNonExistentOrder_shouldReturnNotFound() throws Exception {
		when(orderService.getOrderById(1L)).thenReturn(Optional.empty());
		mockMvc.perform(get("/api/orders/1"))
				.andExpect(status().isNotFound());
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
				.andExpect(jsonPath("$.detail").value("Product quantity must be greater than zero"));
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
				.andExpect(jsonPath("$.detail").value("Product quantity must be greater than zero"));
	}

	static Stream<Order> ordersProvider() {
		var o1 = new Order();
		o1.setId(1L);
		o1.setShippingAddress("via Roma, 1");
		o1.setCreatedAt(LocalDateTime.of(2024, 6, 1, 12, 16, 40));
		o1.setTotalPrice(new BigDecimal("999.99"));
		o1.setVatAmount(new BigDecimal("180.33"));

		var p1 = new OrderProduct();
		p1.setId(2L);
		p1.setProductId(1L);
		p1.setQuantity(1);
		p1.setName("iPhone");
		p1.setTotalPrice(new BigDecimal("999.99"));
		p1.setVatAmount(new BigDecimal("180.33"));
		p1.setVatRate(new BigDecimal("0.22"));

		o1.addProduct(p1);

		var o2 = new Order();
		o2.setId(2L);
		o2.setShippingAddress("via Milano, 2");
		o2.setCreatedAt(LocalDateTime.of(2026, 1, 26, 13, 18, 55));
		o2.setTotalPrice(new BigDecimal("309.99"));
		o2.setVatAmount(new BigDecimal("55.90"));

		var p2 = new OrderProduct();
		p2.setId(3L);
		p2.setProductId(2L);
		p2.setQuantity(2);
		p2.setName("Apple Watch");
		p2.setTotalPrice(new BigDecimal("300.00"));
		p2.setVatAmount(new BigDecimal("54.10"));
		p2.setVatRate(new BigDecimal("0.22"));

		var p3 = new OrderProduct();
		p3.setId(4L);
		p3.setProductId(3L);
		p3.setQuantity(1);
		p3.setName("Keyboard");
		p3.setTotalPrice(new BigDecimal("9.99"));
		p3.setVatAmount(new BigDecimal("1.80"));
		p3.setVatRate(new BigDecimal("0.22"));

		o2.addProduct(p2);
		o2.addProduct(p3);

		return Stream.of(o1, o2);
	}
}
