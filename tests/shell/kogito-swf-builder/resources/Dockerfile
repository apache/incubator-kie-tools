FROM quay.io/kiegroup/kogito-swf-builder:2.0 AS builder

# Copy from build context to resources directory
COPY * ./resources/

# Build app with given resources
RUN "${KOGITO_HOME}"/launch/build-app.sh './resources'

#=============================
# Runtime Run
#=============================
FROM registry.access.redhat.com/ubi8/openjdk-11:latest

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'
# Default name is 'serverless-workflow-project'
ARG PROJECT_ARTIFACT_ID='serverless-workflow-project'

# We make four distinct layers so if there are application changes the library layers can be re-used
COPY --from=builder --chown=185 /home/kogito/${PROJECT_ARTIFACT_ID}/target/quarkus-app/lib/ /deployments/lib/
COPY --from=builder --chown=185 /home/kogito/${PROJECT_ARTIFACT_ID}/target/quarkus-app/*.jar /deployments/
COPY --from=builder --chown=185 /home/kogito/${PROJECT_ARTIFACT_ID}/target/quarkus-app/app/ /deployments/app/
COPY --from=builder --chown=185 /home/kogito/${PROJECT_ARTIFACT_ID}/target/quarkus-app/quarkus/ /deployments/quarkus/

HEALTHCHECK --interval=2s --timeout=4s CMD curl --fail http://localhost:8080 || exit 1

EXPOSE 8080
USER 185
ENV AB_JOLOKIA_OFF=""
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"
