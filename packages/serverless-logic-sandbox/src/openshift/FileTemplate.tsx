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

import { PROJECT_FILES } from "../project";
import { OpenShiftConnection } from "@kie-tools-core/openshift/dist/service/OpenShiftConnection";

const CONTAINER_IMAGES = {
  baseQuarkusProject: process.env.WEBPACK_REPLACE__serverlessLogicSandbox_baseImageFullUrl,
  jdk11Mvn: process.env.WEBPACK_REPLACE__serverlessLogicSandbox_openJdk11MvnImageFullUrl,
};

const DEFAULT_ENV = 'ENV MAVEN_OPTS="-Xmx352m -Xms128m" JAVA_OPTS="-Xmx352m -Xms128m"';
const DEPLOYMENTS_FOLDER = "/deployments";
const SANDBOX_FOLDER = "/tmp/sandbox";
const MVNW_PATH = `${SANDBOX_FOLDER}/mvnw`;
const OC_PATH = `${SANDBOX_FOLDER}/oc`;

const resolveProjectPaths = (projectFolder: string) => ({
  root: `${projectFolder}`,
  mainResources: `${projectFolder}/src/main/resources`,
  metaInfResources: `${projectFolder}/src/main/resources/META-INF/resources`,
  kubernetes: `${projectFolder}/target/kubernetes`,
  quarkusApp: `${projectFolder}/target/quarkus-app`,
  pom: `${projectFolder}/${PROJECT_FILES.pomXml}`,
});

export function createDockerfileContentForBaseQuarkusProjectImage(): string {
  const projectFolder = `${SANDBOX_FOLDER}/serverless-logic-sandbox`;
  const projectPaths = resolveProjectPaths(projectFolder);
  return `
  FROM ${CONTAINER_IMAGES.baseQuarkusProject}
  ${DEFAULT_ENV}
  COPY . ${projectPaths.metaInfResources}/
  RUN ${MVNW_PATH} clean package -B -ntp -f ${projectPaths.pom} \
    && cp ${projectPaths.quarkusApp}/*.jar ${DEPLOYMENTS_FOLDER} \
    && cp -R ${projectPaths.quarkusApp}/lib/ ${DEPLOYMENTS_FOLDER} \
    && cp -R ${projectPaths.quarkusApp}/app/ ${DEPLOYMENTS_FOLDER} \
    && cp -R ${projectPaths.quarkusApp}/quarkus/ ${DEPLOYMENTS_FOLDER} \
    && rm -fr ~/.m2
  `;
}

export function createDockerfileContentForBaseJdk11MvnImage(args: {
  deploymentResourceName: string;
  projectName: string;
  openShiftConnection: OpenShiftConnection;
}): string {
  const sanitizedProjectName = args.projectName.replace(/[^A-Z0-9]/gi, "_"); // Replace whitespaces and special chars
  const projectFolder = `${SANDBOX_FOLDER}/${sanitizedProjectName}`;
  const projectPaths = resolveProjectPaths(projectFolder);
  return `
  FROM ${CONTAINER_IMAGES.jdk11Mvn}
  ${DEFAULT_ENV}
  RUN mkdir ${projectPaths.root}/
  COPY --chown=185:root . ${projectPaths.root}/
  RUN ${OC_PATH} login --token=${args.openShiftConnection.token} --server=${args.openShiftConnection.host} --insecure-skip-tls-verify \
    && ${MVNW_PATH} clean package -B -ntp -f ${projectPaths.pom} -Dquarkus.knative.name=${args.deploymentResourceName} \
    && if [ -f ${projectPaths.kubernetes}/kogito.yml ]; then ${OC_PATH} apply -n ${args.openShiftConnection.namespace} -f ${projectPaths.kubernetes}/kogito.yml; fi \
    && cp ${projectPaths.quarkusApp}/*.jar ${DEPLOYMENTS_FOLDER} \
    && cp -R ${projectPaths.quarkusApp}/lib/ ${DEPLOYMENTS_FOLDER} \
    && cp -R ${projectPaths.quarkusApp}/app/ ${DEPLOYMENTS_FOLDER} \
    && cp -R ${projectPaths.quarkusApp}/quarkus/ ${DEPLOYMENTS_FOLDER} \
    && rm -fr ~/.m2
  `;
}

export function createDockerIgnoreContent(): string {
  return `
  ${PROJECT_FILES.dockerFile}
  `;
}
