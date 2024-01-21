FROM eclipse-temurin:17

WORKDIR /app

COPY . /app

RUN apt-get update
RUN yes | apt install maven

RUN mvn clean install

CMD cd ./"$BENCHMARK_NAME"; java -jar "./target/$BENCHMARK_NAME-1.0-SNAPSHOT.jar"