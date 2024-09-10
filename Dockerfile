FROM openjdk:17-alpine
LABEL key="news"
ARG JAR_FILE=target/news.jar
COPY ${JAR_FILE} news.jar
EXPOSE 7070
ENTRYPOINT ["java" ,"-jar" ,"/news.jar"]