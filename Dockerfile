FROM openjdk:11-jre-slim
VOLUME /tmp
ADD target/crawler.jar crawler.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","crawler.jar"]


