# Copyright 2021 Red Hat
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

FROM registry.access.redhat.com/openjdk/openjdk-11-rhel7:1.1

ARG KOGITO_VERSION=1.7.0.Final

RUN mkdir /tmp/kogito/

WORKDIR /tmp/kogito/

COPY assets/mvnw mvnw
COPY assets/.mvn/ .mvn
COPY assets/dmn-form-schema-generator.jar dmn-form-schema-generator.jar

RUN ./mvnw archetype:generate -B \
        -DarchetypeGroupId=org.kie.kogito \
        -DarchetypeArtifactId=kogito-quarkus-archetype \
        -DarchetypeVersion=${KOGITO_VERSION} \
        -DgroupId=org.kie.kogito \
        -DartifactId=project \
    && rm project/src/main/resources/*.bpmn2 \
    && rm project/src/main/resources/*.dmn \
    && rm project/src/test/java/org/kie/kogito/*.java \
    && mkdir -p project/src/main/resources/META-INF

COPY --chown=185:root assets/dmn-form-webapp/ project/src/main/resources/META-INF/resources/

RUN echo -e '\nquarkus.http.cors=true' >> project/src/main/resources/application.properties \
    && ./mvnw clean package -f project/pom.xml \
    && cp project/target/quarkus-app/*.jar /deployments/ \
    && cp -R project/target/quarkus-app/lib/ /deployments/ \
    && cp -R project/target/quarkus-app/app/ /deployments/ \
    && cp -R project/target/quarkus-app/quarkus/ /deployments/ \
    && rm -fr project/target
