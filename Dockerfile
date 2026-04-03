FROM maven:3.9-eclipse-temurin-21-jammy AS build_step
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
COPY frontend ./frontend
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

ARG IS_PULL_REQUEST=false
ENV APP_ENV_FROM_BUILD=${IS_PULL_REQUEST}
ENV APP_ENV=local
ENV OTEL_SERVICE_NAME=dfl-manager-online
ENV OTEL_LOGS_EXPORTER=none
ENV OTEL_METRICS_EXPORTER=none
ENV OTEL_TRACES_EXPORTER=none
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317

USER appuser
CMD ["sh", "-c", \
     "if [ \"${APP_ENV_FROM_BUILD}\" = \"true\" ]; then export APP_ENV=preview; fi; \
      BASE_ATTRS=\"app.name=dfl-manager-online,service.instance.id=${RENDER_INSTANCE_ID:-local},deployment.environment=${APP_ENV}\"; \
      export OTEL_RESOURCE_ATTRIBUTES=\"${OTEL_RESOURCE_ATTRIBUTES:+${OTEL_RESOURCE_ATTRIBUTES},}${BASE_ATTRS}\"; \
      exec java -javaagent:/app/opentelemetry-javaagent.jar -jar /app/app.jar"]
