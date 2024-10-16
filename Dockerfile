# Use the latest Ubuntu image as the base
FROM ubuntu:20.04
# Update the package repositories and install wget
RUN apt-get update && apt-get install -y wget
# Now, let's move on to installing Java 17
RUN apt-get install -y openjdk-17-jdk
# Set up environment variables for Java
ENV JAVA_HOME /usr/lib/jvm/java-17-openjdk-amd64
# Add Java to the PATH
ENV PATH $JAVA_HOME/bin:$PATH
RUN apt-get -y install libgtk-3-0
RUN apt-get -y install libdbus-glib-1-2
RUN apt-get -y install libgbm1
ARG JAR_FILE=target/Tetris-New-0.0.1-SNAPSHOT.war
WORKDIR /app
COPY ${JAR_FILE} app.war
COPY target/Tetris-New-0.0.1-SNAPSHOT/mongoPrepareShots/ /app/src/main/webapp/mongoPrepareShots/
COPY target/Tetris-New-0.0.1-SNAPSHOT/shots/ /app/src/main/webapp/shots/
ENTRYPOINT ["java","-jar","app.war"]