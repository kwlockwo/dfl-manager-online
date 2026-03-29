FROM maven:3.9-eclipse-temurin-21-jammy AS build_step
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:21-jre-jammy
RUN useradd -r -u 1001 -g root appuser
WORKDIR /app
COPY --from=build_step /build/target/dflmngr-online-1.0-SNAPSHOT.jar app.jar
USER appuser
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
