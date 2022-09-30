ARG JDK_VERSION=17.0.1

FROM openjdk:${JDK_VERSION}-slim

ENV APPLICATION airbyte-e2e-testing-tool

COPY build/distributions/${APPLICATION}*.tar /${APPLICATION}.tar

RUN tar xf ${APPLICATION}.tar --strip-components=1 && rm -rf ${APPLICATION}.tar

ENTRYPOINT ["sh","bin/airbyte-e2e-testing-tool"]
