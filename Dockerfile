FROM maven:3.9-eclipse-temurin-21-jammy AS build_step
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:21-jre-jammy
RUN useradd -r -u 1001 -g root appuser
WORKDIR /app

RUN apt-get update && \
    apt-get install -y wget && \
    wget -q "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.25.0/opentelemetry-javaagent.jar" \
        -O /app/opentelemetry-javaagent.jar && \
    apt-get remove -y wget && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build_step /build/target/dflmngr-online-1.0-SNAPSHOT.jar app.jar

ENV OTEL_SERVICE_NAME=dfl-manager-online
ENV OTEL_LOGS_EXPORTER=none
ENV OTEL_METRICS_EXPORTER=none
ENV OTEL_TRACES_EXPORTER=none
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317

USER appuser
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "/app/app.jar"]
