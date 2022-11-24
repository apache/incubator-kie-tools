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

import { buildProjectPaths } from "../../../project";
import { BaseSwfTemplateProject } from "../BaseContainerImages";
import { DeploymentStrategy } from "../DeploymentStrategy";
import { OpenShiftPipeline } from "../../OpenShiftPipeline";
import { KNativeBuilderPipeline } from "../../pipelines/KNativeBuilderPipeline";
import { GLOB_PATTERN } from "../../../extension";

export class KogitoSwfModelDeployment extends DeploymentStrategy {
  public async buildPipeline(): Promise<OpenShiftPipeline> {
    const filesToBeDeployed = await this.args.getFiles({
      workspaceId: this.args.workspace.workspaceId,
      globPattern: GLOB_PATTERN.sw,
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
    const projectPaths = buildProjectPaths(BaseSwfTemplateProject.PROJECT_FOLDER);

    const steps = {
      importBaseImage: `FROM ${BaseSwfTemplateProject.CONTAINER_IMAGE}`,
      setupEnvVars: `ENV ${BaseSwfTemplateProject.ENV}`,
      copyFilesIntoContainer: `COPY . ${projectPaths.folders.metaInfResources}/`,
      buildProject: `${BaseSwfTemplateProject.MVNW_PATH} clean package -B -ntp -f ${projectPaths.files.pomXml}`,
      copyTargetJarsToDeployments: `cp ${projectPaths.folders.quarkusApp}/*.jar ${BaseSwfTemplateProject.DEPLOYMENTS_FOLDER}`,
      copyTargetLibToDeployments: `cp -R ${projectPaths.folders.quarkusApp}/lib/ ${BaseSwfTemplateProject.DEPLOYMENTS_FOLDER}`,
      copyTargetAppToDeployments: `cp -R ${projectPaths.folders.quarkusApp}/app/ ${BaseSwfTemplateProject.DEPLOYMENTS_FOLDER}`,
      copyTargetQuarkusToDeployments: `cp -R ${projectPaths.folders.quarkusApp}/quarkus/ ${BaseSwfTemplateProject.DEPLOYMENTS_FOLDER}`,
      cleanUpM2Folder: `rm -fr ~/.m2`,
    };

    return `
    ${steps.importBaseImage}
    ${steps.setupEnvVars}
    ${steps.copyFilesIntoContainer}
    RUN ${steps.buildProject} \
      && ${steps.copyTargetJarsToDeployments} \
      && ${steps.copyTargetLibToDeployments} \
      && ${steps.copyTargetAppToDeployments} \
      && ${steps.copyTargetQuarkusToDeployments} \
      && ${steps.cleanUpM2Folder}
    `;
  }
}
