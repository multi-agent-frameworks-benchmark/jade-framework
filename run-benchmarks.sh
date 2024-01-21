source benchmark-settings.config

# Go to main directory
cd ./benchmark

# Create Docker image
docker build -t jade-benchmark-img .

# Run container for hello benchmark
docker run --name jade-hello -e BENCHMARK_NAME="hello" --cpus "$cpus" -m "$memory" -d jade-benchmark-img

# Run container for messaging benchmark
docker run --name jade-messaging -e BENCHMARK_NAME="messaging" --cpus "$cpus" -m "$memory" -d jade-benchmark-img

# Run container for contract-net-protocol benchmark
docker run --name jade-cnp -e BENCHMARK_NAME="contract-net-protocol" --cpus "$cpus" -m "$memory" -d jade-benchmark-img