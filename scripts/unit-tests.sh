CART_DEMO_IMAGE="epieffe/cart-demo-base"

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

# Start Cart Demo Service Docker container
echo "Starting Cart Demo Service Docker container..."
docker run --rm $CART_DEMO_IMAGE mvn -e test
