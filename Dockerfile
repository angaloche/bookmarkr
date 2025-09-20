FROM python:3.12-slim

WORKDIR /app

RUN apt-get update && apt-get install -y \
    default-jdk \
    build-essential \
    libffi-dev \
    libssl-dev \
    curl \
    wget \
    && pip install --no-cache-dir pytubefix \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV PATH="$JAVA_HOME/bin:$PATH"

RUN java -version && python --version

ARG JAR_FILE
COPY target/${JAR_FILE} app.jar

EXPOSE 8091

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]