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

import { DeploymentStrategy } from "../DeploymentStrategy";
import { OpenShiftPipeline } from "../../OpenShiftPipeline";
import { KnativeBuilderPipeline } from "../../pipelines/KnativeBuilderPipeline";
import { DashbuilderViewer } from "../BaseContainerImages";
import { createDashbuilderViewerAppDataFile } from "../DashbuilderViewerAppData";
import { zipFiles } from "../../../zip";

export class DashboardSingleModelDeployment extends DeploymentStrategy {
  public async buildPipeline(): Promise<OpenShiftPipeline> {
    const dockerfileFile = await this.createDockerfileFile();
    const dockerIgnoreFile = await this.createDockerignoreFile();

    const appDataFile = createDashbuilderViewerAppDataFile({
      workspaceId: this.args.workspace.workspaceId,
      primary: this.args.targetFile,
      secondary: [],
    });

    const filesToBeDeployed = [this.args.targetFile, dockerfileFile, dockerIgnoreFile, appDataFile];

    const workspaceZipBlob = await zipFiles(filesToBeDeployed);

    return new KnativeBuilderPipeline({
      workspaceName: this.args.targetFile.name,
      workspaceZipBlob: workspaceZipBlob,
      resourceName: this.args.resourceName,
      targetUri: this.args.targetFile.relativePath,
      namespace: this.args.namespace,
      openShiftService: this.args.openShiftService,
    });
  }

  protected prepareDockerfileContent(): string {
    const steps = {
      importBaseImage: `FROM ${DashbuilderViewer.CONTAINER_IMAGE}`,
      copyFilesIntoContainer: `COPY . ${DashbuilderViewer.HTTPD_OUT}`,
    };

    return `
    ${steps.importBaseImage}
    ${steps.copyFilesIntoContainer}
    `;
  }
}
