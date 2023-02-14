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

const IMPORT = "import";
const INPUT_DATA = "inputData";
const XML_MIME = "text/xml";
const LOCATION_URI_ATTRIBUTE = "locationURI";

export interface DmnLanguageServiceImportedModel {
  content: string;
  relativePath: string;
}

export class DmnLanguageService {
  private readonly parser = new DOMParser();
  private readonly importTagRegExp = new RegExp(`([a-z]*:)?(${IMPORT})`);
  private readonly inputDataRegEx = new RegExp(`([a-z]*:)?(${INPUT_DATA})`);

  constructor(
    private readonly args: {
      getDmnImportedModel: (importedModelRelativePath: string) => Promise<DmnLanguageServiceImportedModel>;
    }
  ) {}

  private getImportedModelRelativePath(model: string): string[] {
    const xmlContent = this.parser.parseFromString(model, XML_MIME);
    const importTag = this.importTagRegExp.exec(model);
    const importedModels = xmlContent.getElementsByTagName(importTag?.[0] ?? IMPORT);
    return Array.from(importedModels)
      .map((importedModel) => importedModel.getAttribute(LOCATION_URI_ATTRIBUTE))
      .filter((e) => e !== null) as string[];
  }

  public getImportedModelRelativePaths(models: string | string[]): string[] {
    if (Array.isArray(models)) {
      return models.flatMap((model) => this.getImportedModelRelativePath(model));
    }

    return this.getImportedModelRelativePath(models);
  }

  // Receive all contents, paths and a node ID and returns the model that contains the node.
  public getPathFromNodeId(resourceContents: DmnLanguageServiceImportedModel[], nodeId: string): string {
    for (const resourceContent of resourceContents) {
      const xmlContent = this.parser.parseFromString(resourceContent.content ?? "", XML_MIME);
      const inputDataTag = this.inputDataRegEx.exec(resourceContent.content ?? "");
      const inputs = xmlContent.getElementsByTagName(inputDataTag?.[0] ?? INPUT_DATA);
      for (const input of Array.from(inputs)) {
        if (input.id === nodeId) {
          return resourceContent.relativePath;
        }
      }
    }
    return "";
  }

  // recursively get imported models
  public async getAllImportedModelsResources(modelsContent: string[]): Promise<DmnLanguageServiceImportedModel[]> {
    // get imported models resources
    const importedModelRelativePaths = this.getImportedModelRelativePaths(modelsContent);
    if (importedModelRelativePaths && importedModelRelativePaths.length > 0) {
      const importedModels = (
        await Promise.all(
          importedModelRelativePaths.map((importedModelRelativePath) =>
            this.args.getDmnImportedModel(importedModelRelativePath)
          )
        )
      ).filter((e) => e !== undefined) as DmnLanguageServiceImportedModel[];

      const importedModelsContent = this.getImportedModelRelativePaths(
        importedModels.map((importedModel) => importedModel.content ?? "")
      );
      if (importedModelsContent.length > 0) {
        return [...importedModels, ...(await this.getAllImportedModelsResources(importedModelsContent))];
      }
      return [...importedModels];
    }
    return [];
  }
}
