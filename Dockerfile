# Dockerfile

# jdk17 Image Start
FROM openjdk:17

ARG JAR_FILE=build/libs/ancmobility-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} ancmobility_Backend.jar
ENTRYPOINT ["java","-jar","-Duser.timezone=Asia/Seoul","ancmobility_Backend.jar"]