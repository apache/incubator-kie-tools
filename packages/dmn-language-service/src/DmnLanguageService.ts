/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { DmnDocumentData } from "./DmnDocumentData";
import { DmnDecision } from "./DmnDecision";
import * as path from "path";
import { getMarshaller } from "@kie-tools/dmn-marshaller";

const INPUT_DATA = "inputData";
const XML_MIME = "text/xml";
const DECISION_NAME_ATTRIBUTE = "name";
const NAMESPACE = "namespace";
const DMN_NAME = "name";
const DECISION = "decision";
const DEFINITIONS = "definitions";

export interface DmnLanguageServiceImportedModelResource {
  content: string;
  normalizedPosixPathRelativeToWorkspaceRoot: string;
}

export class DmnLanguageService {
  private readonly parser = new DOMParser();
  private readonly inputDataRegEx = new RegExp(`([a-z]*:)?(${INPUT_DATA})`);
  private readonly decisionsTagRegExp = new RegExp(`([a-z]*:)?(${DECISION})`);
  private readonly definitionsTagRegExp = new RegExp(`([a-z]*:)?(${DEFINITIONS})`);

  constructor(
    private readonly args: {
      getModelContent: (args: {
        normalizedPosixPathRelativeToWorkspaceRoot: string;
      }) => Promise<DmnLanguageServiceImportedModelResource>;
    }
  ) {}

  private getImportedModelByNormalizedPosixPathRelativeToWorkspaceRoot(
    modelResource: DmnLanguageServiceImportedModelResource,
    importedModelsByModel: Map<string, string[]>
  ): string[] {
    if (!modelResource.content) {
      return [];
    }

    const definitions = getMarshaller(modelResource.content, { upgradeTo: "latest" }).parser.parse()?.definitions;
    if (!definitions?.import) {
      return [];
    }

    return definitions.import.flatMap((importedModel) => {
      const importedModelNormalizedPosixPathRelativeToWorkspaceRoot = path.posix.join(
        path.dirname(modelResource.normalizedPosixPathRelativeToWorkspaceRoot),
        path.posix.normalize(importedModel["@_locationURI"] ?? "")
      );

      // Get the list of imported models from the model resource
      const importedModels = importedModelsByModel.get(modelResource.normalizedPosixPathRelativeToWorkspaceRoot);

      // Check if the imported model was already mapped
      if (importedModels && importedModels.find((e) => e === importedModelNormalizedPosixPathRelativeToWorkspaceRoot)) {
        return [];
      }

      // Map the imported model
      importedModelsByModel.set(modelResource.normalizedPosixPathRelativeToWorkspaceRoot, [
        ...(importedModels ?? []),
        importedModelNormalizedPosixPathRelativeToWorkspaceRoot,
      ]);
      return importedModelNormalizedPosixPathRelativeToWorkspaceRoot;
    });
  }

  private async internalGetImportedModelsByModelResources(
    modelResources: DmnLanguageServiceImportedModelResource[],
    importedModelsByModel: Map<string, string[]>
  ): Promise<DmnLanguageServiceImportedModelResource[]> {
    // get imported models resources
    const importedModelsPathsRelativeToWorkspaceRoot = modelResources.flatMap((modelResource) =>
      this.getImportedModelByNormalizedPosixPathRelativeToWorkspaceRoot(modelResource, importedModelsByModel)
    );

    if (importedModelsPathsRelativeToWorkspaceRoot.length > 0) {
      const importedModelsResources = await Promise.all(
        importedModelsPathsRelativeToWorkspaceRoot.map((normalizedPosixPathRelativeToWorkspaceRoot) =>
          this.args.getModelContent({ normalizedPosixPathRelativeToWorkspaceRoot })
        )
      );

      if (importedModelsResources.length > 0) {
        return [
          ...importedModelsResources,
          ...(await this.internalGetImportedModelsByModelResources(importedModelsResources, importedModelsByModel)),
        ];
      }
      return [...importedModelsResources];
    }
    return [];
  }

  /**
   * This method receives a model resource list and retrieves a map of their imported models. The map keys are the model
   * normalized posix path relative to workspace root and the values all imported model resources of the models.
   */
  public async getImportedModels(
    modelResources: DmnLanguageServiceImportedModelResource[]
  ): Promise<Map<string, DmnLanguageServiceImportedModelResource[]>> {
    try {
      // Using "Path" to avoid big names. Both paths are normalized posix path relative to workspace root.
      const importedModelsPathsByModelPath = new Map<string, string[]>(
        modelResources.map((modelResource) => [modelResource.normalizedPosixPathRelativeToWorkspaceRoot, []])
      );
      const importedModelResources = await this.internalGetImportedModelsByModelResources(
        modelResources,
        importedModelsPathsByModelPath
      );

      return Array.from(importedModelsPathsByModelPath.entries()).reduce(
        (importedModelsResourcesByModelPath, [normalizedPosixPathRelativeToWorkspaceRoot, importedModelPaths]) => {
          importedModelPaths?.forEach((importedModelPath: string) => {
            importedModelsResourcesByModelPath.set(normalizedPosixPathRelativeToWorkspaceRoot, [
              ...(importedModelsResourcesByModelPath.get(normalizedPosixPathRelativeToWorkspaceRoot) ?? []),
              importedModelResources.find((e) => e.normalizedPosixPathRelativeToWorkspaceRoot === importedModelPath)!,
            ]);
          });

          return importedModelsResourcesByModelPath;
        },
        new Map<string, DmnLanguageServiceImportedModelResource[]>()
      );
    } catch (error) {
      throw new Error(`
DMN LANGUAGE SERVICE - getImportedModels: Error while getting imported models from model resources.
Tried to use the following model resources: ${JSON.stringify(modelResources)}
Error details: ${error}`);
    }
  }

  // Receive all contents, paths and a node ID and returns the model that contains the node.
  public getPathFromNodeId(resourceContents: DmnLanguageServiceImportedModelResource[], nodeId: string): string {
    for (const resourceContent of resourceContents) {
      const xmlContent = this.parser.parseFromString(resourceContent.content ?? "", XML_MIME);
      const inputDataTag = this.inputDataRegEx.exec(resourceContent.content ?? "");
      const inputs = xmlContent.getElementsByTagName(inputDataTag?.[0] ?? INPUT_DATA);
      for (const input of Array.from(inputs)) {
        if (input.id === nodeId) {
          return resourceContent.normalizedPosixPathRelativeToWorkspaceRoot;
        }
      }
    }
    return "";
  }

  public getDmnDocumentData(dmnContent: string): DmnDocumentData {
    const xmlContent = this.parser.parseFromString(dmnContent, XML_MIME);
    const definitionsTag = this.definitionsTagRegExp.exec(dmnContent);
    const definitions = xmlContent.getElementsByTagName(definitionsTag ? definitionsTag[0] : DEFINITIONS);
    const definition = definitions[0];
    const namespace = definition.getAttribute(NAMESPACE);
    const dmnModelName = definition.getAttribute(DMN_NAME);

    const dmnDecisions = this.decisionsTagRegExp.exec(dmnContent);
    const dmnDecisionsContent = xmlContent.getElementsByTagName(dmnDecisions ? dmnDecisions[0] : DECISION);

    const decisions = Array.from(dmnDecisionsContent)
      .map((decision) => decision.getAttribute(DECISION_NAME_ATTRIBUTE))
      .flatMap((decisionName) => (decisionName ? [new DmnDecision(decisionName)] : []));
    return new DmnDocumentData(namespace ?? "", dmnModelName ?? "", decisions);
  }

  public getDmnSpecVersion(modelContent: string) {
    if (modelContent === "") {
      return;
    }
    try {
      return getMarshaller(modelContent).originalVersion;
    } catch (error) {
      return;
    }
  }
}
