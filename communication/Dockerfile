FROM openjdk:8-jre-alpine
LABEL maintainer "tiagoalves83@gmail.com"

EXPOSE 8080

COPY ./target/communication-latest.jar /opt/communication-latest.jar

WORKDIR /opt

ENTRYPOINT ["java", "-jar", "/opt/communication-latest.jar"]
