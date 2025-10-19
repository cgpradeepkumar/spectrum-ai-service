FROM openjdk:17-jdk-slim

EXPOSE 8080

ADD target/spectrum-ai-server-*.jar spectrum-ai-server.jar

ENTRYPOINT ["java", "-jar", "spectrum-ai-server.jar"]