FROM openjdk:8-jre-alpine
LABEL maintainer "tiagoalves83@gmail.com"

EXPOSE 8080

COPY ./target/scheduling-latest.jar /opt/scheduling-latest.jar
COPY config.properties /opt/

WORKDIR /opt

ENTRYPOINT ["java", "-jar", "/opt/scheduling-latest.jar"]
