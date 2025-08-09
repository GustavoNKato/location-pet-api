# Use uma imagem base que suporta múltiplas arquiteturas
FROM amazoncorretto:17-alpine as build
WORKDIR /workspace/app

# Copy maven executable to the image
COPY mvnw .
COPY .mvn .mvn

# Copy the pom.xml file
COPY pom.xml .

# Build all dependencies for offline use
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Package the application
RUN ./mvnw package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Production stage
FROM amazoncorretto:17-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency

# Install curl for health check
RUN apk add --no-cache curl

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# Set environment variables
ENV POSITION_STACK_API_KEY=""

# Run the application
ENTRYPOINT ["java","-cp","app:app/lib/*","com.itau.challenge_localization_api.ChallengeLocalizationApiApplication"]
