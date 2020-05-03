###
# Customizable Dockefile.
#
# Since Amphoreas requires a JDBC driver for each database and we can't ship them
# on the distribution, it's required that you create your own amphoreas images.
#
# You can also set the environment variables to configure your images.
#
# When deploying the image to Kubernetes/OpenShift, you can then use secrets or config maps.
#
# To build the image:
#
# podman build -f extras/docker/Dockerfile -t amforeas/amforeas-openjdk-11 .
#
# Then run the container using:
#
# podman run -i --rm -p 8080:8080 -p 8443:8443 amforeas/amforeas-openjdk-11
#
###

FROM openjdk:11-jre-slim-buster

ARG AMFOREAS_VERSION=1.0.0
ARG RUN_JAVA_VERSION=1.3.7

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl unzip \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir /deployments \
    && chown 1001 /deployments \
    && chmod "g+rwX" /deployments \
    && chown 1001:root /deployments \
    && curl https://repo1.maven.org/maven2/io/fabric8/run-java-sh/${RUN_JAVA_VERSION}/run-java-sh-${RUN_JAVA_VERSION}-sh.sh -o /deployments/run-java.sh \
    && chown 1001 /deployments/run-java.sh \
    && chmod 540 /deployments/run-java.sh \
    && curl https://github.com/Eldelshell/amforeas/releases/download/v${AMFOREAS_VERSION}/amforeas-${AMFOREAS_VERSION}-distribution.zip -o /deployments/amforeas.zip \
    && unzip /deployments/amforeas.zip -d /deployments/ \
    && rm /deployments/amforeas.zip

###
#
# JDBC Driver (it has to be the .jar file)
#
# ADD ./postgresql-42.2.12.jar /deployments/amforeas/lib/
#
###

ENV JAVA_MAIN_CLASS="amforeas.AmforeasJetty"
ENV JAVA_CLASSPATH="./amforeas/lib/*:./amforeas/etc"
ENV JAVA_OPTIONS=""

###
#
# Override default configuration here
#
# ENV AMFOREAS_SERVER_ROOT=/myApp/*
# ENV AMFOREAS_SERVER_HOST=0.0.0.0
# ENV AMFOREAS_SERVER_HTTP_PORT=8080
# ENV AMFOREAS_SERVER_THREADS_MIN=10
# ENV AMFOREAS_SERVER_THREADS_MAX=100
#
# Setup SSL (optional)
# ENV AMFOREAS_SERVER_HTTPS_PORT=8443
# ENV AMFOREAS_SERVER_HTTPS_JKS=/deployment/etc/mycerts.jks
# ENV AMFOREAS_SERVER_HTTPS_JKS_PASSWORD=123456 (can be a secret)
# ADD /local/source/mycerts.jks ${AMFOREAS_SERVER_HTTPS_JKS}
#
# For example for PostgreSQL:
#
# ENV AMFOREAS_ALIAS_LIST=alias5
# ENV AMFOREAS_ALIAS5_JDBC_DRIVER=POSTGRESQL
# ENV AMFOREAS_ALIAS5_JDBC_USERNAME=sa2 (can be a secret)
# ENV AMFOREAS_ALIAS5_JDBC_PASSWORD=sa2 (can be a secret)
# ENV AMFOREAS_ALIAS5_JDBC_DATABASE=foo_database
# ENV AMFOREAS_ALIAS5_JDBC_HOST=my_postgresql_service
# ENV AMFOREAS_ALIAS5_JDBC_PORT=5432
#
###

EXPOSE 8080 8443
USER 1001

ENTRYPOINT [ "/deployments/run-java.sh" ]