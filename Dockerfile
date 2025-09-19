FROM 10.0.0.119:5000/java21-python-base

ARG JAR_FILE

WORKDIR /app

EXPOSE 8080

ADD target/${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]