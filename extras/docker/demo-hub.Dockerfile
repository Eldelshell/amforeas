# Before building the docker image run:
#
# cd amforeas-demo && mvn package -P dist && cd ..
#
# Then, build the image with:
#
# podman build -f extras/docker/demo-hub.Dockerfile -t amforeas/amforeas-demo-openjdk-11 .
#
# Then run the container using:
#
# podman run -i --rm -p 8080:8080 -p 8443:8443 amforeas/amforeas-demo-openjdk-11

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
    && curl https://github.com/Eldelshell/amforeas/releases/download/v${AMFOREAS_VERSION}/amforeas-${AMFOREAS_VERSION}-demo.zip -o /deployments/amforeas.zip \
    && unzip /deployments/amforeas.zip -d /deployments/ \
    && rm /deployments/amforeas.zip

ENV JAVA_MAIN_CLASS="amforeas.demo.DemoJetty"
ENV JAVA_CLASSPATH="./amforeas-demo/lib/*:./amforeas-demo/etc"
ENV JAVA_OPTIONS=""

EXPOSE 8080 8443
USER 1001

ENTRYPOINT [ "/deployments/run-java.sh" ]