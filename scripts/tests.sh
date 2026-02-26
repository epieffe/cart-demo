#!/bin/sh

NETWORK_NAME="epieffe-cart-demo-tests-network"

POSTGRES_IMAGE="postgres:18-alpine"
POSTGRES_CONTAINER="epieffe-cart-demo-tests-postgres"

CART_DEMO_IMAGE="epieffe/cart-demo-base"
CART_DEMO_CONTAINER="epieffe-cart-demo-tests"


cleanup() {
    echo ""
    echo "Shutting down Docker containers..."
    docker stop $CART_DEMO_CONTAINER >/dev/null 2>&1
    docker stop $POSTGRES_CONTAINER >/dev/null 2>&1
    echo "Done"
    exit 0
}

trap cleanup INT

# Retrieve the absolute path of this script's directory
SCRIPT_PATH="$0"
case "$SCRIPT_PATH" in
    /*) SCRIPT_PATH="$SCRIPT_PATH" ;;
    *) SCRIPT_PATH="$(pwd)/$SCRIPT_PATH" ;;
esac
SCRIPT_DIR="$(dirname "$SCRIPT_PATH")"
SCRIPT_DIR="$(cd "$SCRIPT_DIR" 2>/dev/null && pwd)"

# Build Docker base layer image for Cart Demo Service
echo "Building base Docker image for Cart Demo Service..."
docker build -t $CART_DEMO_IMAGE --target base "${SCRIPT_DIR}/.."

# Create Docker network if it doesn't exist
if ! docker network ls | grep -q $NETWORK_NAME; then
    echo "Creating Docker network $NETWORK_NAME..."
    docker network create $NETWORK_NAME
fi

# Start Postgresql container
echo "Starting Postgresql Docker container..."
docker run -d \
    --name $POSTGRES_CONTAINER \
    --network $NETWORK_NAME \
    --rm \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_PASSWORD=mypassword \
    -e POSTGRES_DB=cart-demo \
    $POSTGRES_IMAGE

# Start Cart Demo Service Docker container
echo "Starting Cart Demo Service Docker container..."
docker run -d \
    --name $CART_DEMO_CONTAINER \
    --network $NETWORK_NAME \
    --rm \
    -e SPRING_PROFILES_ACTIVE=docker \
    -e DB_URL=jdbc:postgresql://${POSTGRES_CONTAINER}:5432/cart-demo \
    -e DB_USERNAME=postgres \
    -e DB_PASSWORD=mypassword \
    -p 8080:8080 \
    $CART_DEMO_IMAGE \
    mvn -e test

# Show logs for Cart Demo Service container
docker logs -f $CART_DEMO_CONTAINER

cleanup
