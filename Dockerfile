FROM eclipse-temurin:21-jre-alpine
LABEL authors="nathaniel"

# set working directory
WORKDIR /app

# copy the built jar
COPY ./target/leaderboard-service-0.0.1-SNAPSHOT.jar app.jar

# verify the jar is available
RUN ls -la

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]