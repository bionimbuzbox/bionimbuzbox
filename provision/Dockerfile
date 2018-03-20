FROM openjdk:8-jre-alpine
LABEL maintainer "tiagoalves83@gmail.com"

EXPOSE 8080

COPY ./target/provision-latest.jar /opt/provision-latest.jar
COPY config.properties /opt/

WORKDIR /opt

ENTRYPOINT ["java", "-jar", "/opt/provision-latest.jar"]
