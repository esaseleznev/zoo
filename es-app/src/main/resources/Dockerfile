FROM openjdk:8-jdk-alpine

MAINTAINER  Alexey Seleznev  <es.aseleznev@gmail.com>

COPY target/*.jar /deployments/
COPY *.jar /deployments/app.jar

ENV JAVA_OPTS="$JAVA_OPTS -Xms500m -Xmx1024m"

ENTRYPOINT ["sh", "-c", "java -cp *.jar $JAVA_OPTS -jar /deployments/app.jar" ]