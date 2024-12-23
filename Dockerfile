FROM openjdk:17

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

ARG SPRING_PROFILES_ACTIVE


RUN echo $SPRING_PROFILES_ACTIVE
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE
ENV JAVA_OPTS="-Dfile.encoding=UTF-8"

ENTRYPOINT exec java $JAVA_OPTS -jar .jar

