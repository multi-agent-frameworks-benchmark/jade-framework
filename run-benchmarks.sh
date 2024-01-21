source benchmark-settings.config

# Go to main directory
cd ./benchmark

# Create Docker image
docker build -t jade-benchmark-img .

# Run container for hello benchmark
docker run --name jade-hello -e BENCHMARK_NAME="hello" --cpus "$CPUS" -m "$MEMORY" -d jade-benchmark-img

# Run container for messaging benchmark
docker run --name jade-messaging -e BENCHMARK_NAME="messaging" --cpus "$CPUS" -m "$MEMORY" -d jade-benchmark-img

# Run container for contract-net-protocol benchmark
docker run --name jade-cnp -e BENCHMARK_NAME="contract-net-protocol" --cpus "$CPUS" -m "$MEMORY" -d jade-benchmark-img