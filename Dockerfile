FROM maven:3.9-eclipse-temurin AS DEPENDENCIES
WORKDIR /build
COPY control/pom.xml control/pom.xml
COPY web-test-agent/pom.xml web-test-agent/pom.xml
COPY mobile-test-agent/pom.xml mobile-test-agent/pom.xml
COPY pom.xml .
ARG MODULE
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -DskipTests -f pom.xml -pl ${MODULE} -am dependency:go-offline

FROM maven:3.9-eclipse-temurin AS BUILDER
WORKDIR /build
COPY . .
COPY --from=DEPENDENCIES /root/.m2 /root/.m2
ARG MODULE
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -DskipTests -f pom.xml -pl ${MODULE} -am clean package


FROM eclipse-temurin:21-jre
WORKDIR /app
ARG MODULE
COPY --from=BUILDER /build/${MODULE}/target/*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]