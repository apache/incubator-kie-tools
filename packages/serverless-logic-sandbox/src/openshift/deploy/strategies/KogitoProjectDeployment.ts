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

import { GLOB_PATTERN } from "../../../extension";
import { buildProjectPaths } from "../../../project";
import { BaseJdk11MvnOc } from "../BaseContainerImages";
import { DeploymentStrategy } from "../DeploymentStrategy";
import { OpenShiftPipeline } from "../../OpenShiftPipeline";
import { DeploymentStrategyArgs } from "../types";
import { KNativeBuilderPipeline } from "../../pipelines/KNativeBuilderPipeline";
import { OpenShiftConnection } from "@kie-tools-core/openshift/dist/service/OpenShiftConnection";

interface CreateKogitoProjectDeploymentArgs {
  openShiftConnection: OpenShiftConnection;
}

export class KogitoProjectDeployment extends DeploymentStrategy {
  constructor(protected readonly args: DeploymentStrategyArgs & CreateKogitoProjectDeploymentArgs) {
    super(args);
  }

  public async buildPipeline(): Promise<OpenShiftPipeline> {
    const filesToBeDeployed = await this.args.getFiles({
      workspaceId: this.args.workspace.workspaceId,
      globPattern: GLOB_PATTERN.allExceptDockerfiles,
    });

    const dockerfileFile = await this.createDockerfileFile();
    const dockerIgnoreFile = await this.createDockerignoreFile();

    filesToBeDeployed.push(dockerfileFile, dockerIgnoreFile);

    const workspaceZipBlob = await this.createZipBlob(filesToBeDeployed);

    return new KNativeBuilderPipeline({
      workspaceName: this.resolveWorkspaceName(filesToBeDeployed),
      workspaceZipBlob: workspaceZipBlob,
      resourceName: this.args.resourceName,
      targetUri: this.args.targetFile.relativePath,
      namespace: this.args.namespace,
      openShiftService: this.args.openShiftService,
      kafkaSourceArgs: this.args.kafkaSourceArgs,
    });
  }

  protected prepareDockerfileContent(): string {
    const projectPaths = buildProjectPaths(BaseJdk11MvnOc.PROJECT_FOLDER);

    const steps = {
      importBaseImage: `FROM ${BaseJdk11MvnOc.CONTAINER_IMAGE}`,
      setupEnvVars: `ENV ${BaseJdk11MvnOc.ENV}`,
      createProjectFolder: `RUN mkdir ${projectPaths.folders.root}/`,
      copyFilesIntoContainer: `COPY --chown=185:root . ${projectPaths.folders.root}/`,
      doOcLogin: `${BaseJdk11MvnOc.OC_PATH} login --token=${this.args.openShiftConnection.token} --server=${this.args.openShiftConnection.host} --insecure-skip-tls-verify`,
      buildProject: `${BaseJdk11MvnOc.MVNW_PATH} clean package -B -ntp -f ${projectPaths.files.pomXml} -Dquarkus.knative.name=${this.args.resourceName}`,
      applyKogitoYaml: `if [ -f ${projectPaths.files.kogitoYaml} ]; then ${BaseJdk11MvnOc.OC_PATH} apply -n ${this.args.openShiftConnection.namespace} -f ${projectPaths.files.kogitoYaml}; fi`,
      copyTargetJarsToDeployments: `cp ${projectPaths.folders.quarkusApp}/*.jar ${BaseJdk11MvnOc.DEPLOYMENTS_FOLDER}`,
      copyTargetLibToDeployments: `cp -R ${projectPaths.folders.quarkusApp}/lib/ ${BaseJdk11MvnOc.DEPLOYMENTS_FOLDER}`,
      copyTargetAppToDeployments: `cp -R ${projectPaths.folders.quarkusApp}/app/ ${BaseJdk11MvnOc.DEPLOYMENTS_FOLDER}`,
      copyTargetQuarkusToDeployments: `cp -R ${projectPaths.folders.quarkusApp}/quarkus/ ${BaseJdk11MvnOc.DEPLOYMENTS_FOLDER}`,
      cleanUpM2Folder: `rm -fr ~/.m2`,
    };

    return `
    ${steps.importBaseImage}
    ${steps.setupEnvVars}
    ${steps.createProjectFolder}
    ${steps.copyFilesIntoContainer}
    RUN ${steps.doOcLogin} \
      && ${steps.buildProject} \
      && ${steps.applyKogitoYaml} \
      && ${steps.copyTargetJarsToDeployments} \
      && ${steps.copyTargetLibToDeployments} \
      && ${steps.copyTargetAppToDeployments} \
      && ${steps.copyTargetQuarkusToDeployments} \
      && ${steps.cleanUpM2Folder}
    `;
  }
}
