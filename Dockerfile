FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /build/

COPY ./pom.xml ./pom.xml
RUN mvn dependency:go-offline -B

COPY ./src ./src
RUN mvn package -DskipTests

FROM azul/zulu-openjdk-alpine:17.0.0
COPY --from=build /build/target/metrics-playground-1.0-SNAPSHOT-jar-with-dependencies.jar /app/metrics-playground.jar
ENTRYPOINT ["java","-jar","/app/metrics-playground.jar"]