# Copyright 2022 Red Hat
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

# docker build --build-arg QUARKUS_PLATFORM_VERSION=2.13.0.Final -f Dockerfile -t quay.io/lmotta/kn-plugin-workflow:2.13.0.Final .

FROM maven:3.8.5-jdk-11 as create

ARG QUARKUS_PLATFORM_VERSION

WORKDIR /tmp/

RUN mvn io.quarkus.platform:quarkus-maven-plugin:${QUARKUS_PLATFORM_VERSION}:create \
  -DprojectGroupId=org.kie \
  -DprojectArtifactId=kn-plugin-workflow \
  -DnoCode \
  -DplatformVersion=${QUARKUS_PLATFORM_VERSION} \
  -Dextensions="kogito-quarkus-serverless-workflow,kogito-addons-quarkus-knative-eventing,quarkus-resteasy-jackson,quarkus-kubernetes"

RUN cd kn-plugin-workflow \
  && rm -rf src/main/docker/ src/main/java/ src/test/

COPY workflow.sw.json /tmp/kn-plugin-workflow/src/main/resources

FROM openjdk:11 as builder

COPY --from=create /root/.m2 /root/.m2
COPY --from=create /tmp/ /tmp/

WORKDIR /tmp/kn-plugin-workflow

RUN ./mvnw package -Dquarkus.kubernetes.deployment-target=knative \
  && rm -rf ./target \
  && rm ./src/main/resources/workflow.sw.json