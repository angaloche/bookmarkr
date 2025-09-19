FROM ubuntu:22.04

WORKDIR /app

# Installer Python 3
RUN apt-get update && apt-get install -y python3 python3-pip && rm -rf /var/lib/apt/lists/*

# Installer les modules Python nécessaires
RUN pip3 install --break-system-packages pytubefix

# Copier ton JAR
ARG JAR_FILE
ADD target/${JAR_FILE} app.jar

EXPOSE 8091

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
