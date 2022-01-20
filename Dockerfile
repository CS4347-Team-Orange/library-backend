FROM gradle:7.1.1-jdk11-hotspot AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:11-jre-slim
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/library-0.0.1-SNAPSHOT.jar /app/spring-boot-application.jar
RUN sudo apt-get -y install curl
ENTRYPOINT ["java","-Dspring.datasource.url=jdbc:postgresql://db:5432/library","-jar","/app/spring-boot-application.jar"]