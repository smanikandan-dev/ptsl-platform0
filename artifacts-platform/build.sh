# Build the Docker image
echo "Building Docker image..."
docker build -t dockerregistry:5000/httpapi:latest -f interface-http/Dockerfile .

# Check if the build was successful
if [ $? -eq 0 ]; then
  echo "Docker image built successfully."
else
  echo "Error: Docker build failed."
  exit 1
fi
