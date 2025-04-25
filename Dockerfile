FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew && ./gradlew build -x test

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

RUN apk add --no-cache curl

RUN curl -LO https://github.com/prometheus/node_exporter/releases/download/v1.7.0/node_exporter-1.7.0.linux-amd64.tar.gz && \
    tar xzf node_exporter-1.7.0.linux-amd64.tar.gz && \
    mv node_exporter-1.7.0.linux-amd64/node_exporter /usr/local/bin/ && \
    rm -rf node_exporter-1.7.0.linux-amd64*

COPY --from=builder /app/build/libs/*.jar app.jar

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]