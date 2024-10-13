FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/EcomSite-0.0.1-SNAPSHOT.jar /app/ecom-site.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/ecom-site.jar"]

# docker run --network=mynetwork --env-file staging.env -p 8000:8000 tranhuy105/ecommerce-admin:latest