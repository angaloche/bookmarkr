FROM ubuntu:22.04

WORKDIR /app

RUN apt-get update && apt-get install -y \
    python3-pip \
    python3-dev \
    build-essential \
    libffi-dev \
    libssl-dev \
    && pip3 install --no-cache-dir pytubefix

# Copier ton JAR
ARG JAR_FILE
ADD target/${JAR_FILE} app.jar

EXPOSE 8091

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
