FROM openjdk:17
VOLUME /temp
ADD ./build/libs/trainingProject-all.jar .
EXPOSE 8080
CMD ["java", "-jar", "trainingProject-all.jar"]