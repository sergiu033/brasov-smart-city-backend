# Use Maven to build the application
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Use Eclipse Temurin for the runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# The transport data is expected in a sibling directory as per current config
# but we can mount it via docker-compose
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
