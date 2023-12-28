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
import { DmnMarshaller, getMarshaller } from "@kie-tools/dmn-marshaller";

const INPUT_DATA = "inputData";
const XML_MIME = "text/xml";
const DECISION_NAME_ATTRIBUTE = "name";
const NAMESPACE = "namespace";
const DMN_NAME = "name";
const DECISION = "decision";
const DEFINITIONS = "definitions";

export interface DmnLanguageServiceImportedModelResources {
  content: string;
  pathRelativeToWorkspaceRoot: string;
}

export class DmnLanguageService {
  private readonly parser = new DOMParser();
  private readonly inputDataRegEx = new RegExp(`([a-z]*:)?(${INPUT_DATA})`);
  private readonly decisionsTagRegExp = new RegExp(`([a-z]*:)?(${DECISION})`);
  private readonly definitionsTagRegExp = new RegExp(`([a-z]*:)?(${DEFINITIONS})`);
  private marshaller: DmnMarshaller;
  private importedModelsByPathsRelativeToWorkspaceRoot = new Set<string>();

  constructor(
    private readonly args: {
      getModelContentFromPathRelativeToWorkspaceRoot: (
        pathRelativeToWorkspaceRoot: string
      ) => Promise<DmnLanguageServiceImportedModelResources | undefined>;
      modelContent: string;
      pathRelativeToWorkspaceRoot: string;
    }
  ) {
    if (args.modelContent !== "") {
      this.marshaller = getMarshaller(args.modelContent);
    }
    this.importedModelsByPathsRelativeToWorkspaceRoot.add(args.pathRelativeToWorkspaceRoot);
  }

  private getImportedModelPathRelativeToWorkspaceRoot(
    modelResources: DmnLanguageServiceImportedModelResources
  ): string[] {
    if (!modelResources.content) {
      return [];
    }

    const marshaller: DmnMarshaller = getMarshaller(modelResources.content);
    const definitions = marshaller.parser.parse()?.definitions;

    if (!definitions) {
      return [];
    }
    if (!definitions.import) {
      return [];
    }

    return definitions.import
      .flatMap((importedModel) => {
        const importedModelPathRelativeToWorkspaceRoot = path.posix.join(
          path.dirname(modelResources.pathRelativeToWorkspaceRoot),
          path.normalize(importedModel["@_locationURI"] ?? "")
        );
        // Can't import a model that already is imported
        if (this.importedModelsByPathsRelativeToWorkspaceRoot.has(importedModelPathRelativeToWorkspaceRoot)) {
          return [];
        }
        this.importedModelsByPathsRelativeToWorkspaceRoot.add(importedModelPathRelativeToWorkspaceRoot);
        return importedModelPathRelativeToWorkspaceRoot;
      })
      .filter((e) => e !== null) as string[];
  }

  private getImportedModelPathsRelativeToWorkspaceRoot(
    modelResources: DmnLanguageServiceImportedModelResources[] | DmnLanguageServiceImportedModelResources
  ): string[] {
    if (Array.isArray(modelResources)) {
      return modelResources.flatMap((modelResource) => this.getImportedModelPathRelativeToWorkspaceRoot(modelResource));
    }

    return this.getImportedModelPathRelativeToWorkspaceRoot(modelResources);
  }

  public async getAllImportedModelsByModelResource(
    modelsResources: DmnLanguageServiceImportedModelResources[]
  ): Promise<DmnLanguageServiceImportedModelResources[]> {
    // get imported models resources
    const importedModelsPathsRelativeToWorkspaceRoot =
      this.getImportedModelPathsRelativeToWorkspaceRoot(modelsResources);
    if (importedModelsPathsRelativeToWorkspaceRoot && importedModelsPathsRelativeToWorkspaceRoot.length > 0) {
      const importedModelsResources = (
        await Promise.all(
          importedModelsPathsRelativeToWorkspaceRoot.map((importedModelPathRelativeToOpenFile) =>
            this.args.getModelContentFromPathRelativeToWorkspaceRoot(importedModelPathRelativeToOpenFile)
          )
        )
      ).filter((e) => e !== undefined) as DmnLanguageServiceImportedModelResources[];

      if (importedModelsResources.length > 0) {
        return [
          ...importedModelsResources,
          ...(await this.getAllImportedModelsByModelResource(importedModelsResources)),
        ];
      }
      return [...importedModelsResources];
    }
    return [];
  }

  public async getAllImportedModelsByPathRelativeToWorkspaceRoot(modelPathRelativeToWorkspaceRoot: string) {
    const modelResource = [
      (await this.args.getModelContentFromPathRelativeToWorkspaceRoot(modelPathRelativeToWorkspaceRoot)) ??
        ([] as DmnLanguageServiceImportedModelResources[]),
    ].flatMap((e) => e);
    return this.getAllImportedModelsByModelResource(modelResource);
  }

  // Receive all contents, paths and a node ID and returns the model that contains the node.
  public getPathFromNodeId(resourceContents: DmnLanguageServiceImportedModelResources[], nodeId: string): string {
    for (const resourceContent of resourceContents) {
      const xmlContent = this.parser.parseFromString(resourceContent.content ?? "", XML_MIME);
      const inputDataTag = this.inputDataRegEx.exec(resourceContent.content ?? "");
      const inputs = xmlContent.getElementsByTagName(inputDataTag?.[0] ?? INPUT_DATA);
      for (const input of Array.from(inputs)) {
        if (input.id === nodeId) {
          return resourceContent.pathRelativeToWorkspaceRoot;
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

  public getDmnSpecVersion() {
    return this.marshaller?.originalVersion;
  }
}
