CREATE SEQUENCE order_seq INCREMENT BY 50;

CREATE TABLE orders (
	id bigint NOT NULL,
	shipping_address varchar(255) NOT NULL,
	created_at timestamp NOT NULL,
	total_price numeric(16,2) NOT NULL,
    vat_amount numeric(16,2) NOT NULL,

	CONSTRAINT order_pkey PRIMARY KEY (id)
);

CREATE TABLE order_product (
	id bigint NOT NULL,
	order_id bigint NOT NULL,
	product_id bigint NOT NULL,
	quantity int NOT NULL,
	name varchar(255) NOT NULL,
	total_price numeric(16,2) NOT NULL,
	vat_amount numeric(16,2) NOT NULL,
	vat_rate numeric(8,4) NOT NULL,

	CONSTRAINT order_product_pkey PRIMARY KEY (id),
	CONSTRAINT order_product_unique_product_id_order_id UNIQUE (product_id, order_id),
	CONSTRAINT order_product_fk_order_id FOREIGN KEY (order_id) REFERENCES orders(id)
);
