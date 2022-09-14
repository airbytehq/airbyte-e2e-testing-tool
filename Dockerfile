ARG JDK_VERSION=17.0.1

FROM openjdk:${JDK_VERSION}-slim

ENV APPLICATION airbyte-e2e-testing-tool

COPY build/distributions/${APPLICATION}*.tar /app/${APPLICATION}.tar

WORKDIR /app

RUN tar xf ${APPLICATION}.tar --strip-components=1 && rm -rf ${APPLICATION}.tar

WORKDIR /app/lib

ENTRYPOINT ["java", "-jar", "airbyte-e2e-testing-tool-0.2.3.jar"]

#/run-scenario name="Update source version scenario" airte_1=tt_airbyte_dev2 source_1=tt_postgres_source_aws_1 destination_1=tt_postgres_destination_aws_1 old_version=1.0.1 new_version=1.0.2