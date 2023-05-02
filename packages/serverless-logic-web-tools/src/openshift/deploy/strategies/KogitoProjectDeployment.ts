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
import { BaseBuilder } from "../BaseContainerImages";
import { DeploymentStrategy } from "../DeploymentStrategy";
import { OpenShiftPipeline } from "../../OpenShiftPipeline";
import { DeploymentStrategyArgs } from "../types";
import { KnativeBuilderPipeline } from "../../pipelines/KnativeBuilderPipeline";
import { zipFiles } from "../../../zip";
import { KubernetesConnection } from "@kie-tools-core/kubernetes-bridge/dist/service";

interface CreateKogitoProjectDeploymentArgs {
  openShiftConnection: KubernetesConnection;
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

    const workspaceZipBlob = await zipFiles(filesToBeDeployed);

    return new KnativeBuilderPipeline({
      workspaceName: this.resolveWorkspaceName(filesToBeDeployed),
      workspaceZipBlob: workspaceZipBlob,
      resourceName: this.args.resourceName,
      targetUri: this.args.targetFile.relativePath,
      namespace: this.args.namespace,
      openShiftService: this.args.openShiftService,
    });
  }

  protected prepareDockerfileContent(): string {
    const projectPaths = buildProjectPaths(BaseBuilder.PROJECT_FOLDER);
    const clusterName = "user-cluster";
    const clusterCredentialsName = `${clusterName}-credentials`;
    const clusterContextName = `${clusterName}-context`;

    const steps = {
      importBaseImage: `FROM ${BaseBuilder.CONTAINER_IMAGE}`,
      setupEnvVars: `ENV ${BaseBuilder.ENV}`,
      createProjectFolder: `RUN mkdir ${projectPaths.folders.root}/`,
      copyFilesIntoContainer: `COPY . ${projectPaths.folders.root}/`,
      configCluster: {
        setServer: `${BaseBuilder.KUBECTL_PATH} config set-cluster ${clusterName} --server=${this.args.openShiftConnection.host}`,
        setCredentials: `${BaseBuilder.KUBECTL_PATH} config set-credentials ${clusterCredentialsName} --token=${this.args.openShiftConnection.token}`,
        setContext: `${BaseBuilder.KUBECTL_PATH} config set-context ${clusterContextName} --cluster=${clusterName} --user=${clusterCredentialsName} --namespace=${this.args.openShiftConnection.namespace}`,
        useContext: `${BaseBuilder.KUBECTL_PATH} config use ${clusterContextName}`,
      },
      buildProject: `mvn clean package -B -ntp -f ${projectPaths.files.pomXml} -Dquarkus.knative.name=${this.args.resourceName}`,
      applyKogitoYaml: `if [ -f ${projectPaths.files.kogitoYaml} ]; then ${BaseBuilder.KUBECTL_PATH} apply -n ${this.args.openShiftConnection.namespace} -f ${projectPaths.files.kogitoYaml} --insecure-skip-tls-verify=true; fi`,
      cleanUpM2Folder: "rm -fr ~/.m2",
      entrypoint: `ENTRYPOINT ["java", "-jar", "${projectPaths.files.quarkusRunJar}"]`,
    };

    return `
    ${steps.importBaseImage}
    ${steps.setupEnvVars}
    ${steps.createProjectFolder}
    ${steps.copyFilesIntoContainer}
    RUN ${steps.configCluster.setServer} \
      && ${steps.configCluster.setCredentials} \
      && ${steps.configCluster.setContext} \
      && ${steps.configCluster.useContext} \
      && ${steps.buildProject} \
      && ${steps.applyKogitoYaml} \
      && ${steps.cleanUpM2Folder}
    ${steps.entrypoint}
    `;
  }
}
