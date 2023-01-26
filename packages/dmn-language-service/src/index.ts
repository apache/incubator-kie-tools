/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { ResourceContent } from "@kie-tools-core/workspace/dist/api";
import { WorkspacesContextType } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";

const DMN_IMPORT = "dmn:import";
const DMN_INPUT_DATA = "dmn:inputData";
const XML_MIME = "text/xml";
const LOCATION_URI_ATTRIBUTE = "locationURI";
const ID_ATTRIBUTE = "id";

export class DmnLanguageService {
  private readonly parser = new DOMParser();
  DmnLanguageService() {}

  private getImportedModel(model: string): string[] {
    const xmlContent = this.parser.parseFromString(model, XML_MIME);
    const importedModels = xmlContent.getElementsByTagName(DMN_IMPORT);
    return Array.from(importedModels)
      .map((importedModel) => importedModel.getAttribute(LOCATION_URI_ATTRIBUTE))
      .filter((e) => e !== null) as string[];
  }

  public getImportedModels(models: string | string[]): string[] {
    if (Array.isArray(models)) {
      return models.flatMap((model) => this.getImportedModel(model));
    }

    return this.getImportedModel(models);
  }

  // Receive all contents, paths and a node ID and returns the model that contains the node.
  public getPathFromNodeId(resourceContents: { path: string; content?: string }[], nodeId: string): string {
    for (const resourceContent of resourceContents) {
      const xmlContent = this.parser.parseFromString(resourceContent.content ?? "", XML_MIME);
      const inputs = xmlContent.getElementsByTagName(DMN_INPUT_DATA);
      for (const input of Array.from(inputs)) {
        if (input.id === nodeId) {
          return resourceContent.path;
        }
      }
    }
    return "";
  }

  // recursively get imported models
  public async getAllImportedModelsResources(
    workspaces: WorkspacesContextType,
    workspaceId: string,
    models: string[],
    resources: ResourceContent[] = []
  ): Promise<ResourceContent[]> {
    // get imported models resources
    const importedModels = this.getImportedModels(models);
    if (importedModels && importedModels.length > 0) {
      const importedModelsResources = (
        await Promise.all(
          importedModels.map((importedModel) => {
            return workspaces.resourceContentGet({
              workspaceId: workspaceId,
              relativePath: importedModel,
            });
          })
        )
      ).filter((e) => e !== undefined) as ResourceContent[];

      const contents = importedModelsResources.map((resources) => resources.content ?? "");
      const importedFiles = this.getImportedModels(contents);
      if (importedFiles.length > 0) {
        return [
          ...importedModelsResources,
          ...(await this.getAllImportedModelsResources(workspaces, workspaceId, importedFiles)),
        ];
      }
      return [...importedModelsResources];
    }
    return resources;
  }
}
