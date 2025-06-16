FROM openjdk:21-jdk

WORKDIR /app

RUN useradd -r -u 1001 mower

# Copier le JAR
COPY target/mower-*.jar /app/mower-java.jar

# Changer les permissions
RUN chown mower:mower /app/mower-java.jar

# Basculer vers l'utilisateur non-root
USER mower

# Point d'entrée
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "mower-java.jar"]
