TRUNCATE TABLE product CASCADE;

INSERT INTO product (id, name, description, total_price, vat_rate) VALUES
(1, 'Samsung Galaxy S24', 'Powerful Android smartphone', 800, 0.22),
(2, 'Apple Watch', 'Cool smartwatch', 199.99, 0.22),
(3, 'Playstation 5', 'Powerful gaming console', 499.99, 0.22),
(4, 'iPhone 16', 'Apple smartphone with excellent camera', 1099, 0.22),
(5, 'Samsung A17', 'Medium budget smartphone', 400, 0.22),
(6, 'iPhone 15', 'Apple smartphone with good camera', 899.99, 0.22);
