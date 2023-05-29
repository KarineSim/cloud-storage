FROM openjdk:17-alpine

EXPOSE 8090

COPY target/cloud-storage-0.0.1-SNAPSHOT.jar cloudstorage.jar

CMD ["java","-jar","cloudstorage.jar"]