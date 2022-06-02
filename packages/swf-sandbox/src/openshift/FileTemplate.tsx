/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const BASE_IMAGE = process.env.WEBPACK_REPLACE__swfSandbox_baseImageFullUrl;
const DEPLOYMENTS_FOLDER = "/deployments";
const SANDBOX_FOLDER = "/tmp/sandbox";
const PROJECT_FOLDER = `${SANDBOX_FOLDER}/serverless-logic-sandbox`;
const PROJECT_MAIN_RESOURCES = `${PROJECT_FOLDER}/src/main/resources`;
const PROJECT_METAINF_RESOURCES = `${PROJECT_MAIN_RESOURCES}/META-INF/resources`;
const QUARKUS_APP_FOLDER = `${PROJECT_FOLDER}/target/quarkus-app`;
const POM_PATH = `${PROJECT_FOLDER}/pom.xml`;
const MVNW_PATH = `${SANDBOX_FOLDER}/mvnw`;

export function createDockerfileContent(): string {
  return `
  FROM ${BASE_IMAGE}
  ENV MAVEN_OPTS="-Xmx352m -Xms128m" JAVA_OPTS="-Xmx352m -Xms128m"
  COPY . ${PROJECT_METAINF_RESOURCES}/
  RUN ${MVNW_PATH} clean package -B -ntp -f ${POM_PATH} \
    && cp ${QUARKUS_APP_FOLDER}/*.jar ${DEPLOYMENTS_FOLDER} \
    && cp -R ${QUARKUS_APP_FOLDER}/lib/ ${DEPLOYMENTS_FOLDER} \
    && cp -R ${QUARKUS_APP_FOLDER}/app/ ${DEPLOYMENTS_FOLDER} \
    && cp -R ${QUARKUS_APP_FOLDER}/quarkus/ ${DEPLOYMENTS_FOLDER} \
    && rm -fr ~/.m2
  `;
}
