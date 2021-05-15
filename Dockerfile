FROM openjdk:15-jdk-alpine

ARG JAR_FILE=target/*.jar

ARG DOMAIN_NAME=http://localhost:8090

ENV BASE_URL=$DOMAIN_NAME

COPY ${JAR_FILE} authentication.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/authentication.jar"]