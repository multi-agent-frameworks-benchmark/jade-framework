FROM eclipse-temurin:17

WORKDIR /app

COPY . /app

RUN ls

RUN apt-get update
RUN yes | apt install maven

RUN mvn clean install

CMD cd ./messaging; java -cp "../jade.jar:./target/messaging-1.0-SNAPSHOT.jar" jade.Boot -agents "CounterAgent:com.jade.CounterAgent;SenderAgent:com.jade.SenderAgent"