#
# Build stage
#

FROM maven:3.9.2 AS build

WORKDIR usr/src/app

COPY . ./

RUN mvn clean package

#
# Package stage
#

FROM openjdk:19

ARG JAR_NAME="Admin-Service-0.0.1-SNAPSHOT"

WORKDIR /usr/src/app

COPY --from=build /usr/src/app/target/${JAR_NAME}.jar ./app.jar

CMD ["java","-jar", "./app.jar"]