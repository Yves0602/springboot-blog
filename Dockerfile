FROM java:openjdk-8u111-alpine

RUN mkdir /app

WORKDIR /app

COPY target/springboot-1.0-SNAPSHOT.jar /app

EXPOSE 8080

CMD [ "java", "-jar", "springboot-1.0-SNAPSHOT.jar" ]