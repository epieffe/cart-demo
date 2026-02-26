package dev.epieffe.demo.cart.order;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.jspecify.annotations.NullUnmarked;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@NullUnmarked
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
	@SequenceGenerator(name = "order_seq")
	@Column(name = "id")
	private Long id;

	@Column(name = "shipping_address")
	private String shippingAddress;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "total_price")
	private BigDecimal totalPrice;

	@Column(name = "vat_amount")
	private BigDecimal vatAmount;

	@OneToMany(mappedBy = OrderProduct_.ORDER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderProduct> products = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
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

	public List<OrderProduct> getProducts() {
		return products;
	}

	public void setProducts(List<OrderProduct> products) {
		this.products = products;
	}

	public void addProduct(OrderProduct orderProduct) {
		this.products.add(orderProduct);
		orderProduct.setOrder(this);
	}

	public void removeProduct(OrderProduct orderProduct) {
		this.products.remove(orderProduct);
		orderProduct.setOrder(null);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Order ord)) return false;
		return id != null && id.equals(ord.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public String toString() {
		return "Order{" +
				"id=" + id +
				", shippingAddress=" + shippingAddress +
				", createdAt=" + createdAt +
				", totalPrice=" + totalPrice +
				", vatAmount=" + vatAmount +'}';
	}

	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now(ZoneOffset.UTC);
	}
}
