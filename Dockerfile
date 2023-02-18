FROM openjdk:17
WORKDIR /app
COPY target/reactive-catalogue-crud-0.0.1-SNAPSHOT.jar reactive-catalogue-crud-0.0.1-SNAPSHOT.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/reactive-catalogue-crud-0.0.1-SNAPSHOT.jar"]