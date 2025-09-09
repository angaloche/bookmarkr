FROM gcr.io/distroless/java21

ARG JAR_FILE

WORKDIR /app

EXPOSE 8080

ADD target/${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]