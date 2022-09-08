ARG JDK_VERSION=17.0.1

FROM openjdk:${JDK_VERSION}-slim

ENV APPLICATION airbyte-e2e-testing-tool

COPY build/distributions/${APPLICATION}*.tar /tmp/${APPLICATION}.tar

WORKDIR /tmp

RUN tar xf ${APPLICATION}.tar --strip-components=1

ENTRYPOINT ["java", "-jar", "/tmp/lib/airbyte-e2e-testing-tool-0.2.0.jar"]