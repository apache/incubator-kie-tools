FROM quay.io/mdessi/kogito-swf-builder:latest AS builder
#FROM quay.io/ricardozanini/kogito-serverless-workflow-base:latest AS builder

# Kogito User
USER 1001

# User home from base image
WORKDIR /home/kogito/kogito-base

# Copy from build context to skeleton resources project
COPY * ./src/main/resources/

# Maven vars enhirited from the base image
RUN ${MAVEN_HOME}/bin/mvn -U -B ${MAVEN_ARGS_APPEND} -s ${MAVEN_SETTINGS_PATH} clean install -DskipTests

#=============================
# Runtime Run
#=============================
FROM registry.access.redhat.com/ubi8/openjdk-11:1.11

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'

# We make four distinct layers so if there are application changes the library layers can be re-used
COPY --from=builder --chown=185 /home/kogito/kogito-base/target/quarkus-app/lib/ /deployments/lib/
COPY --from=builder --chown=185 /home/kogito/kogito-base/target/quarkus-app/*.jar /deployments/
COPY --from=builder --chown=185 /home/kogito/kogito-base/target/quarkus-app/app/ /deployments/app/
COPY --from=builder --chown=185 /home/kogito/kogito-base/target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080
USER 185
ENV AB_JOLOKIA_OFF=""
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"
