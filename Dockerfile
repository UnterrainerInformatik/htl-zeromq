# Base Alpine Linux based image with OpenJDK JRE only
FROM eclipse-temurin:21-jdk-alpine-3.21
# copy application (with libraries inside)
COPY target/application.jar /
COPY target/libs /libs/
EXPOSE 8080
EXPOSE 8443
# specify default command
CMD ["java", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-Djava.security.egd=file:/dev/./urandom", "-cp", "/libs/*.jar", "-jar", "application.jar"]