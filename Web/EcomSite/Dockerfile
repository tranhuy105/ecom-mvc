FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/EcomSite-0.0.1-SNAPSHOT.jar /app/ecom-site.jar
COPY run_ecommerce.sh /app/run_ecommerce.sh
RUN chmod +x /app/run_ecommerce.sh

EXPOSE 8000

CMD ["sh", "/app/run_ecommerce.sh"]

# docker run --network=mynetwork -p 8000:8000 tranhuy105/ecommerce-site:latest