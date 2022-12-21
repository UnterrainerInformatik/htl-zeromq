# Base Alpine Linux based image with OpenJDK JRE only
FROM openjdk:18-alpine
# make the image smaller by deleting some JDK-only files
RUN rm -rf /opt/openjdk-18/jmods && rm -rf /opt/openjdk-18/lib/src.zip
# copy application (with libraries inside)
COPY target/application.jar /
COPY target/libs /libs/
EXPOSE 8080
EXPOSE 8443
# specify default command
CMD ["java", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-Djava.security.egd=file:/dev/./urandom", "-cp", "/libs/*.jar", "-jar", "application.jar"]
