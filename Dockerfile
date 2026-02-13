FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime
FROM amazoncorretto:21-alpine

RUN apk --no-cache add curl

#USER root
#COPY certificates.sh /certificates.sh
#RUN chmod +x /certificates.sh
#RUN /certificates.sh

# ARG PROFILE=dev
ARG APP_VERSION=0.0.1

WORKDIR /app
COPY --from=build /build/target/customer-grpc-server-*.jar /app/app.jar

EXPOSE 8050

# ENV ACTIVE_PROFILE=${PROFILE}

CMD ["java", "-jar", "app.jar"]