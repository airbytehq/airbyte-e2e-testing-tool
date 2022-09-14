ARG JDK_VERSION=17.0.1

FROM openjdk:${JDK_VERSION}-slim

ENV APPLICATION airbyte-e2e-testing-tool

#ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005
COPY build/distributions/${APPLICATION}*.tar /app/${APPLICATION}.tar

WORKDIR /app

RUN tar xf ${APPLICATION}.tar --strip-components=1 && rm -rf ${APPLICATION}.tar

WORKDIR /app/lib
#VOLUME /app/lib/secrets
ENTRYPOINT ["java", "-jar", "airbyte-e2e-testing-tool-0.2.0.jar"]
#ENTRYPOINT ["/bin/bash", "-c", "./entrypoint.sh"]