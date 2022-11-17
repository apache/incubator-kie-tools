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

// These values come from `serverless-logic-sandbox-base-image` and `openjdk11-mvn-image`
const DeployDefaults = {
  PROJECT_NAME: "serverless-logic-sandbox",
  SANDBOX_FOLDER: "/tmp/sandbox",
  DEPLOYMENTS_FOLDER: "/deployments",
  ENV: 'MAVEN_OPTS="-Xmx352m -Xms128m" JAVA_OPTS="-Xmx352m -Xms128m"',
};

export const BaseSwfTemplateProject = {
  CONTAINER_IMAGE: process.env.WEBPACK_REPLACE__serverlessLogicSandbox_baseImageFullUrl,
  ENV: DeployDefaults.ENV,
  PROJECT_FOLDER: `${DeployDefaults.SANDBOX_FOLDER}/${DeployDefaults.PROJECT_NAME}`,
  MVNW_PATH: `${DeployDefaults.SANDBOX_FOLDER}/mvnw`,
  DEPLOYMENTS_FOLDER: DeployDefaults.DEPLOYMENTS_FOLDER,
};

export const BaseJdk11MvnOc = {
  CONTAINER_IMAGE: process.env.WEBPACK_REPLACE__serverlessLogicSandbox_openJdk11MvnImageFullUrl,
  ENV: DeployDefaults.ENV,
  PROJECT_FOLDER: `${DeployDefaults.SANDBOX_FOLDER}/${DeployDefaults.PROJECT_NAME}`,
  MVNW_PATH: `${DeployDefaults.SANDBOX_FOLDER}/mvnw`,
  OC_PATH: `${DeployDefaults.SANDBOX_FOLDER}/oc`,
  DEPLOYMENTS_FOLDER: DeployDefaults.DEPLOYMENTS_FOLDER,
};
