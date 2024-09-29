FROM openjdk:17-jdk-slim AS build
ARG JAR_FILE=target/Tetris-New-0.0.1-SNAPSHOT.war
WORKDIR /app
COPY ${JAR_FILE} app.war

COPY target/Tetris-New-0.0.1-SNAPSHOT/mongoPrepareShots/ /app/src/main/webapp/mongoPrepareShots/
COPY target/Tetris-New-0.0.1-SNAPSHOT/shots/ /app/src/main/webapp/shots/

ENTRYPOINT ["java","-jar","app.war"]