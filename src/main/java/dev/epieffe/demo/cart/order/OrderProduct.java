package dev.epieffe.demo.cart.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.jspecify.annotations.NullUnmarked;

import java.math.BigDecimal;

@Entity
@Table(name = "order_product")
@NullUnmarked
public class OrderProduct {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Order order;

	@Column(name = "product_id")
	private Long productId;

	@Column(name = "quantity")
	private Integer quantity;

	@Column(name = "name")
	private String name;

	@Column(name = "total_price")
	private BigDecimal totalPrice;

	@Column(name = "vat_amount")
	private BigDecimal vatAmount;

	@Column(name = "vat_rate")
	private BigDecimal vatRate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public BigDecimal getVatAmount() {
		return vatAmount;
	}

	public void setVatAmount(BigDecimal vatAmount) {
		this.vatAmount = vatAmount;
	}

	public BigDecimal getVatRate() {
		return vatRate;
	}

	public void setVatRate(BigDecimal vatRate) {
		this.vatRate = vatRate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof OrderProduct po)) return false;
		return id != null && id.equals(po.id);
	}

	@Override
	public String toString() {
		return "OrderProduct{" +
				"id=" + id +
				", orderId=" + (order != null ? order.getId() : null) +
				", productId=" + productId +
				", quantity=" + quantity +
				", name='" + name + '\'' +
				", totalPrice=" + totalPrice +
				", vatAmount=" + vatAmount +
				", vatRate=" + vatRate + '}';
	}
}
