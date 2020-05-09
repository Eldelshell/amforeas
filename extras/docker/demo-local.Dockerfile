# Before building the docker image run:
#
# cd amforeas-demo && mvn package -P dist && cd ..
#
# Then, build the image with:
#
# podman build -f extras/docker/demo-local.Dockerfile -t amforeas/amforeas-demo-openjdk-11 .
#
# Then run the container using:
#
# podman run -i --rm -p 8080:8080 -p 8443:8443 amforeas/amforeas-demo-openjdk-11

FROM openjdk:11-jre-slim-buster

ARG AMFOREAS_VERSION=1.2.0-SNAPSHOT
ARG RUN_JAVA_VERSION=1.3.7

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'

ADD amforeas-demo/target/amforeas-${AMFOREAS_VERSION}-demo.zip /

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
    && unzip /amforeas-${AMFOREAS_VERSION}-demo.zip -d /deployments/ \
    && rm /amforeas-${AMFOREAS_VERSION}-demo.zip
    
ENV JAVA_MAIN_CLASS="amforeas.demo.DemoJetty"
ENV JAVA_CLASSPATH="./amforeas-demo/lib/*:./amforeas-demo/etc"
ENV JAVA_OPTIONS=""

EXPOSE 8080 8443
USER 1001

ENTRYPOINT [ "/deployments/run-java.sh" ]