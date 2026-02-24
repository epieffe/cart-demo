CREATE SEQUENCE product_seq INCREMENT BY 50;

CREATE TABLE product (
	id bigint NOT NULL,
	name varchar(255) NOT NULL,
	description text NOT NULL,
	total_price numeric(16,2) NOT NULL,
	vat_rate numeric(8,4) NOT NULL,

	CONSTRAINT product_pkey PRIMARY KEY (id)
);
