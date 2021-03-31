FROM openjdk:15-jdk-alpine
ARG JAR_FILE=target/authentication-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} authentication.jar
ENTRYPOINT ["java","-jar","/authentication.jar"]