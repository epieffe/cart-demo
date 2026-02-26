#!/bin/sh

NETWORK_NAME="epieffe-cart-demo-network"

POSTGRES_IMAGE="postgres:18-alpine"
POSTGRES_CONTAINER="epieffe-cart-demo-postgres"
POSTGRES_VOLUME="epieffe-cart-demo-postgres-volume"

CART_DEMO_IMAGE="epieffe/cart-demo"
CART_DEMO_CONTAINER="epieffe-cart-demo-service"


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

# Build Docker image for Cart Demo Service
echo "Building Docker image for Cart Demo Service..."
docker build -t $CART_DEMO_IMAGE "${SCRIPT_DIR}/.."

# Create Docker network if it doesn't exist
if ! docker network ls | grep -q $NETWORK_NAME; then
    echo "Creating Docker network $NETWORK_NAME..."
    docker network create $NETWORK_NAME
fi

# Create Docker volume for Postgresql if it doesn't exist
if ! docker volume ls | grep -q $POSTGRES_VOLUME; then
    echo "Creating Docker volume for Postgresql $POSTGRES_VOLUME..."
    docker volume create $POSTGRES_VOLUME
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
    -v $POSTGRES_VOLUME:/var/lib/postgresql \
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
    $CART_DEMO_IMAGE

# Show logs for Cart Demo Service container
docker logs -f $CART_DEMO_CONTAINER

cleanup
