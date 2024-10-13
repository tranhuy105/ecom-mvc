FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/EcomAdmin-0.0.1-SNAPSHOT.jar /app/ecom-admin.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "/app/ecom-admin.jar"]