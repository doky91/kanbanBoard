# Stage 1: Build aplikacije (Maven)
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

# Kopiraj Maven config i izvorni kod
COPY pom.xml .
COPY src ./src

# Buildaj JAR (preskači testove za brži build)
RUN mvn clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Kopiraj JAR iz prethodnog stagea
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
