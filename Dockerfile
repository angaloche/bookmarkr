FROM python:3.12-slim

WORKDIR /app

# Installer Java + dépendances nécessaires
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    build-essential \
    libffi-dev \
    libssl-dev \
    curl \
    wget \
    && pip install --no-cache-dir pytubefix \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Définir JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH="$JAVA_HOME/bin:$PATH"

# Vérifier que Java et Python sont installés
RUN java -version && python --version

# Copier le JAR
ARG JAR_FILE
ADD target/${JAR_FILE} app.jar

EXPOSE 8091

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
