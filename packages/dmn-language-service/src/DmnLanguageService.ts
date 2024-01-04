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
import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

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

/**
 * This type represents the ...
 * string: normalizedPosixPathRelativeToWorkspaceRoot
 * .
 */
export interface ImportIndex {
  hierarchy: Map<string, { deep: Set<string>; immediate: Set<string> }>;
  models: Map<string, DMN15__tDefinitions>;
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

  /**
   * This method receives a model resource list and retrieves a map of their imported models. The map keys are the model
   * normalized posix path relative to workspace root and the values all imported model resources of the models.
   */
  private async internalGetImportedModelsByModelResources(
    normalizedPosixPathRelativeToWorkspaceRoot: string,
    importIndex: ImportIndex,
    parents: string[],
    depth: number
  ): Promise<void> {
    if (depth === 0) {
      return;
    }

    const definitions = importIndex.models.get(normalizedPosixPathRelativeToWorkspaceRoot);
    if (!definitions) {
      throw new Error("TODO");
    }

    importIndex.hierarchy.set(
      normalizedPosixPathRelativeToWorkspaceRoot,
      importIndex.hierarchy.get(normalizedPosixPathRelativeToWorkspaceRoot) ?? { immediate: new Set(), deep: new Set() }
    );
    const importedModels = importIndex.hierarchy.get(normalizedPosixPathRelativeToWorkspaceRoot);

    for (const importedModel of definitions.import ?? []) {
      const importedModelNormalizedPosixPathRelativeToWorkspaceRoot = path.posix.join(
        path.dirname(normalizedPosixPathRelativeToWorkspaceRoot),
        path.posix.normalize(importedModel["@_locationURI"] ?? "")
      );

      // Get the list of imported models from the model resource
      // Prevent cycles by looking if already in hierarchy
      if (importedModels?.immediate.has(importedModelNormalizedPosixPathRelativeToWorkspaceRoot)) {
        for (const parent of parents ?? []) {
          importIndex.hierarchy.get(parent)?.deep.add(importedModelNormalizedPosixPathRelativeToWorkspaceRoot);
          for (const importedModelDeep of importIndex.hierarchy.get(
            importedModelNormalizedPosixPathRelativeToWorkspaceRoot
          )?.deep ?? []) {
            importIndex.hierarchy.get(parent)?.deep.add(importedModelDeep);
          }
        }
        continue;
      }

      // Map the imported model
      importedModels?.immediate.add(importedModelNormalizedPosixPathRelativeToWorkspaceRoot);
      const importedModelResource =
        importIndex.models.get(importedModelNormalizedPosixPathRelativeToWorkspaceRoot) ??
        getMarshaller(
          (
            await this.args.getModelContent({
              normalizedPosixPathRelativeToWorkspaceRoot: importedModelNormalizedPosixPathRelativeToWorkspaceRoot,
            })
          ).content,
          { upgradeTo: "latest" }
        ).parser.parse().definitions;

      importIndex.models.set(importedModelNormalizedPosixPathRelativeToWorkspaceRoot, importedModelResource);

      importedModels?.deep.add(importedModelNormalizedPosixPathRelativeToWorkspaceRoot);
      for (const parent of parents ?? []) {
        importIndex.hierarchy.get(parent)?.deep.add(importedModelNormalizedPosixPathRelativeToWorkspaceRoot);
      }

      parents.push(normalizedPosixPathRelativeToWorkspaceRoot);
      await this.internalGetImportedModelsByModelResources(
        importedModelNormalizedPosixPathRelativeToWorkspaceRoot,
        importIndex,
        parents,
        depth - 1
      );
      parents.pop();
    }
  }

  public async recursivelyGetImportedModels(
    modelResources: DmnLanguageServiceImportedModelResource[],
    depth = -1
  ): Promise<ImportIndex> {
    try {
      const importIndex: ImportIndex = {
        hierarchy: new Map(
          modelResources.map((modelResource) => [
            modelResource.normalizedPosixPathRelativeToWorkspaceRoot,
            { deep: new Set(), immediate: new Set() },
          ])
        ),
        models: new Map(
          modelResources.map((modelResource) => [
            modelResource.normalizedPosixPathRelativeToWorkspaceRoot,
            getMarshaller(modelResource.content, { upgradeTo: "latest" }).parser.parse().definitions,
          ])
        ),
      };

      for (const modelResource of modelResources) {
        await this.internalGetImportedModelsByModelResources(
          modelResource.normalizedPosixPathRelativeToWorkspaceRoot,
          importIndex,
          [], // initialize parents list
          depth
        );
      }

      return importIndex;
    } catch (error) {
      throw new Error(`
DMN LANGUAGE SERVICE - recursivelyGetImportedModels: Error while getting imported models from model resources.
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
