# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

FROM quay.io/kiegroup/kogito-swf-builder-nightly:latest AS builder

# Kogito User
USER 1001

# User home from base image
WORKDIR /home/kogito/kogito-sw-base

# Copy from build context to skeleton project
COPY * ./src/main/resources

# Maven vars enhirited from the base image
RUN ${MAVEN_HOME}/bin/mvn -U -B ${MAVEN_ARGS_APPEND} -s ${MAVEN_SETTINGS_PATH} clean install -DskipTests

#=============================
# Runtime Run
#=============================
FROM registry.access.redhat.com/ubi8/openjdk-17:1.18

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'

# We make four distinct layers so if there are application changes the library layers can be re-used
COPY --from=builder --chown=185 /home/kogito/kogito-sw-base/target/quarkus-app/lib/ /deployments/lib/
COPY --from=builder --chown=185 /home/kogito/kogito-sw-base/target/quarkus-app/*.jar /deployments/
COPY --from=builder --chown=185 /home/kogito/kogito-sw-base/target/quarkus-app/app/ /deployments/app/
COPY --from=builder --chown=185 /home/kogito/kogito-sw-base/target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080
USER 185
ENV AB_JOLOKIA_OFF=""
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"