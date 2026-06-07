FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

ENV MAVEN_OPTS="-Xmx256m"

RUN mvn clean package -DskipTests


FROM eclipse-temurin:17-jre

RUN groupadd -r odinapp && useradd -r -g odinapp -d /app -s /sbin/nologin odinapp

WORKDIR /app

COPY --from=build /app/target/odin-devops-api.jar app.jar

RUN chown -R odinapp:odinapp /app

USER odinapp

ENV JAVA_OPTS="-Xms128m -Xmx256m"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
