ARG JDK_VERSION=17.0.1

FROM openjdk:${JDK_VERSION}-slim

ENV APPLICATION airbyte-e2e-testing-tool

COPY build/libs/${APPLICATION}*.jar ${APPLICATION}.jar

CMD java -jar ${APPLICATION}.jar