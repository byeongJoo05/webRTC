FROM amazoncorretto:17
VOLUME /tmp
EXPOSE 8080
CMD ["./mvnw", "clean", "package"]
ARG JAR_FILE=target/*.jar
COPY ./build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]