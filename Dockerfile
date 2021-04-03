FROM openjdk:15-jdk-alpine

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} authentication.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/authentication.jar"]