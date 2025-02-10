FROM gradle:jdk21
COPY . /src
WORKDIR /src
RUN gradle build

FROM eclipse-temurin:21
COPY --from=0 /src/build/libs/DiscordSRV-Heads.jar /
VOLUME /storage
CMD ["java", "-jar", "/DiscordSRV-Heads.jar"]
