FROM openjdk:8-jre-alpine
LABEL maintainer "tiagoalves83@gmail.com"

EXPOSE 8080

COPY ./target/web-ui-latest.jar /opt/web-ui-latest.jar
COPY ./web /opt/web

WORKDIR /opt

ENTRYPOINT ["java", "-jar", "/opt/web-ui-latest.jar"]
