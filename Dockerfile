FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY settings.xml /root/.m2/settings.xml
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

ARG SERVER_PORT=8080

ENV TZ=Asia/Shanghai \
    JAVA_OPTS="" \
    SERVER_PORT=${SERVER_PORT}

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE ${SERVER_PORT}

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar --server.port=${SERVER_PORT}"]
