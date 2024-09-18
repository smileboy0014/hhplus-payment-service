FROM bellsoft/liberica-openjdk-alpine:17
CMD ["./gradlew", "clean", "build"]
VOLUME /tmp
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app.jar"]
# docker run -d --name [app name] -p 8082:8082 -e SPRING_PROFILES_ACTIVE=[active profile] [image name]