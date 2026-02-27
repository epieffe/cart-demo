# Cart Demo
 Simplified version of a purchase cart service.

 **Technologies:**
 - Java 21
 - Spring Boot 4
 - PostgreSQL 18

**Table of contents:**
- [Documentation](#documentation)
    - [Data model](#data-model)
    - [Vat amount computation](#vat-amount-computation)
    - [API endpoints](#api-endpoints)
- [How to run](#how-to-run)
    - [Run using Docker Compose](#run-using-docker-compose)
    - [Run using the run script](#run-using-the-run-script)
    - [Run in development mode](#run-in-development-mode)
- [Running tests](#running-tests)
    - [Run tests in Docker container](#run-tests-in-docker-container)
    - [Run tests using Java](#run-tests-using-java)

## Documentation
This section contains documentation for the Cart Demo service, as well as the considerations taken during the design phase.

### Data model
The following ER diagram describes how data is modeled in the database:

![cart-demo-er](https://github.com/user-attachments/assets/29157d77-dd9a-4e55-b1ca-b4d6dd74caaa)

For products, we store the total price (including VAT) and the VAT rate in the `product` table.

When an order is created for a product, the relevant product infos are copied in the `order_product` table.
Product data might change over time, while the `order_product` table contains product data at purchase time.
The `product_id` field on the `order_product` table does not have a foreign key constraint, because a product might be
deleted in the future, but we still want to keep the original product id for an order.

If a product is purchased in quantity greater than one, the `total_price` field on the `order_product` table contains the
cumulative price of all purchased units. The VAT amount is calculated on the cumulative total price to reduce the impact of
decimal rounding as much as possible. The computed VAT amount is stored in the `order_product` table for future reference.

### Vat amount computation
The Cart Demo service only supports currencies with two decimal digits, and it is specifically designed to comply with
Italian VAT calculation rules.

Given a total price for an order, thus including VAT, the net price for the order is calculated as:

`total_price / (1 + vat_rate)`

If the third decimal digit is 5 or higher, the resulting net price is rounded up to the next cent, otherwise it is rounded down.

The VAT amount for the order is simply the net price subtracted to the total price.

### API endpoints
Here we describe the most relevant API endpoints exposed by the Cart Demo service. Full API documentation is available in the
Swagger UI exposed by the Cart Demo service itself.

#### Create a product
To add a new product, call `POST /api/products` and pass the product data in the request body.

Example curl:
```bash
curl --location 'http://127.0.0.1:8080/api/products' \
--header 'Content-Type: application/json' \
--data '{
  "name": "Samsung Galaxy S21",
  "description": "Powerful smartphone with 5G connectivity and advanced camera features.",
  "totalPrice": 999.99,
  "vatRate": 0.22
}'
```

Example response body:
```json
{
  "id": 53,
  "name": "Samsung Galaxy S21",
  "description": "Powerful smartphone with 5G connectivity and advanced camera features.",
  "totalPrice": 999.99,
  "netPrice": 819.66,
  "vatAmount": 180.33,
  "vatRate": 0.22
}
```

#### Search products
To search products call `GET /api/products`.

Optional query parameters are supported for filtering, sorting and pagination:
- `name`: filter products that contain this value in their name, case-insensitive.
- `maxPrice`: filter products with price lower that this value.
- `minPrice`: filter products with price higher that this value.
- `size`: max number of results (default 20).
- `page`: page number.
- `sort`: Sorting criteria in the format: property,(asc|desc).

Example curl:
```bash
curl 'http://127.0.0.1:8080/api/products?minPrice=100&sort=totalPrice,desc'
```

Example response body:
```json
[
  {
    "id": 53,
    "name": "Samsung Galaxy S21",
    "description": "Powerful smartphone with 5G connectivity and advanced camera features.",
    "totalPrice": 999.99,
    "netPrice": 819.66,
    "vatAmount": 180.33,
    "vatRate": 0.22
  },
  {
    "id": 54,
    "name": "Apple Watch",
    "description": "Cool smartwatch",
    "totalPrice": 150,
    "netPrice": 122.95,
    "vatAmount": 27.05,
    "vatRate": 0.22
  }
]
```

#### Create an order
To add a new order, call `POST /api/orders` and pass the order data in the request body.

Example curl:
```bash
curl --location 'http://127.0.0.1:8080/api/orders' \
--header 'Content-Type: application/json' \
--data '{
  "shippingAddress": "via Roma, 5",
  "products": [
    {
      "productId": 53,
      "quantity": 2
    },
    {
      "productId": 54,
      "quantity": 1
    }
  ]
}'
```

Example response body:
```json
{
  "id": 52,
  "shippingAddress": "via Roma, 5",
  "createdAt": "2026-02-27T00:30:16.173208024",
  "totalPrice": 2149.98,
  "vatAmount": 387.70,
  "products": [
    {
      "productId": 53,
      "quantity": 2,
      "name": "Samsung Galaxy S21",
      "totalPrice": 1999.98,
      "vatAmount": 360.65,
      "vatRate": 0.22
    },
    {
      "productId": 54,
      "quantity": 1,
      "name": "Apple Watch",
      "totalPrice": 150.00,
      "vatAmount": 27.05,
      "vatRate": 0.22
    }
  ]
}
```

#### Get an order by id
To get an order by its id call `GET /api/orders/{id}`.

Example curl:
```bash
curl 'http://127.0.0.1:8080/api/orders/52'
```

Example response body:
```json
{
  "id": 52,
  "shippingAddress": "via Roma, 5",
  "createdAt": "2026-02-27T00:30:16.173208",
  "totalPrice": 2149.98,
  "vatAmount": 387.70,
  "products": [
    {
      "productId": 53,
      "quantity": 2,
      "name": "Samsung Galaxy S21",
      "totalPrice": 1999.98,
      "vatAmount": 360.65,
      "vatRate": 0.22
    },
    {
      "productId": 54,
      "quantity": 1,
      "name": "Apple Watch",
      "totalPrice": 150.00,
      "vatAmount": 27.05,
      "vatRate": 0.22
    }
  ]
}
```

## How to run
The Cart Demo service requires a PostgreSQL instance to run. When running using Docker Compose
or the provided run script a PostgreSQL instance is automatically started in a Docker container.

When running in production or development, connection details for PostgreSQL can be provided
through the following environment variables:
- `DB_URL`: JDBC URL for the database
- `DB_USERNAME`: database username
- `DB_PASSWORD`: database password

When you have the Cart Demo service running, visit the following url to access the API documentation in the Swagger UI:

http://127.0.0.1:8080/swagger-ui.html

### Run using Docker Compose
This is the simplest way to run the Cart Demo service:
```bash
docker compose up -d
```
This will start the following containers:
- Cart Demo service exposing port 8080
- PostgreSQL exposing port 15432

### Run using the run script
If you don't have Docker Compose, you can run the Cart Demo service using the provided run script:
```bash
./scripts/run.sh
```
This script will:
- Build the Cart Demo service image from Dockerfile
- Create a Docker network if it doesn't exist
- Create a Docker volume for PostgreSQL data if it doesn't exist
- Run a container with PostgreSQL exposing port 15432
- Run a container with the Cart Demo service exposing port 8080
- Attach to the Cart Demo service container logs

When the script terminates, all the created containers are deleted, while the Docker volume still remains on your system.

### Run in development mode
This requires Java 21 or higher.

First start a Docker container with PostgreSQL:
```bash
docker compose up -d postgres
```
Or, if you don't have Docker Compose:
```bash
docker run -d \
    -p 15432:5432 \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_PASSWORD=mypassword \
    -e POSTGRES_DB=cart-demo \
    postgres:18-alpine
```
if you use the default connection parameters, there is no need to set environment variables.
Simply run the Cart Demo service in development mode:
```bash
./mvnw spring-boot:run
```

## Running tests
Running the test suite for the Cart Demo service requires a PostgreSQL instance. If the `local` Spring profile
is active, the application will use the Docker socket to automatically start a PostgreSQL container,
otherwise it will connect to an external PostgreSQL instance as usual.


### Run tests in Docker container
You can use the provided tests script to run the test suite in a Docker container:
```bash
./scripts/tests.sh
```
This script will:
- Build the base Cart Demo Docker image from Dockerfile
- Create a Docker network if it doesn't exist
- Run a container with PostgreSQL
- Run a container that executes the Cart Demo test suite
- Attach to the Cart Demo tests logs

When the script terminates, all the created containers are deleted.

### Run tests using Java
This requires Java 21 or higher:
```bash
./mvnw test
```
This will use the Docker socket to automatically start a PostgreSQL container.
