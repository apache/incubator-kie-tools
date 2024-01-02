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

interface ImportedModelsByModelResources {
  modelResources: DmnLanguageServiceImportedModelResources[];
  normalizedPosixPathRelativeToWorkspaceRoot?: undefined;
}

interface ImportedModelsByNormalizedPosixPathRelativeToWorkspaceRoot {
  modelResources?: undefined;
  normalizedPosixPathRelativeToWorkspaceRoot: string;
}

type ImportedModelsParams = ImportedModelsByModelResources | ImportedModelsByNormalizedPosixPathRelativeToWorkspaceRoot;

export interface DmnLanguageServiceImportedModelResources {
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
      }) => Promise<DmnLanguageServiceImportedModelResources | undefined>;
    }
  ) {}

  private getImportedModelByNormalizedPosixPathRelativeToWorkspaceRoot(
    modelResources: DmnLanguageServiceImportedModelResources,
    importedModelsNormalizedPosixPathRelativeToWorkspaceRoot: Set<string>
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
        const importedModelNormalizedPosixPathRelativeToWorkspaceRoot = path.normalize(
          path.posix.join(
            path.dirname(modelResources.normalizedPosixPathRelativeToWorkspaceRoot),
            path.normalize(importedModel["@_locationURI"] ?? "")
          )
        );

        // Can't import a model that already is imported
        if (
          importedModelsNormalizedPosixPathRelativeToWorkspaceRoot.has(
            importedModelNormalizedPosixPathRelativeToWorkspaceRoot
          )
        ) {
          return [];
        }
        importedModelsNormalizedPosixPathRelativeToWorkspaceRoot.add(
          importedModelNormalizedPosixPathRelativeToWorkspaceRoot
        );
        return importedModelNormalizedPosixPathRelativeToWorkspaceRoot;
      })
      .filter((e) => e !== null) as string[];
  }

  private getImportedModelsByNormalizedPosixPathRelativeToWorkspaceRoot(
    modelResources: DmnLanguageServiceImportedModelResources[] | DmnLanguageServiceImportedModelResources,
    importedModelsNormalizedPosixPathRelativeToWorkspaceRoot: Set<string>
  ): string[] {
    if (Array.isArray(modelResources)) {
      return modelResources.flatMap((modelResource) =>
        this.getImportedModelByNormalizedPosixPathRelativeToWorkspaceRoot(
          modelResource,
          importedModelsNormalizedPosixPathRelativeToWorkspaceRoot
        )
      );
    }

    return this.getImportedModelByNormalizedPosixPathRelativeToWorkspaceRoot(
      modelResources,
      importedModelsNormalizedPosixPathRelativeToWorkspaceRoot
    );
  }

  private async internalGetImportedModelsByModelResources(
    modelResources: DmnLanguageServiceImportedModelResources[],
    importedModelsNormalizedPosixPathRelativeToWorkspaceRoot: Set<string>
  ): Promise<DmnLanguageServiceImportedModelResources[]> {
    // get imported models resources
    const importedModelsPathsRelativeToWorkspaceRoot =
      this.getImportedModelsByNormalizedPosixPathRelativeToWorkspaceRoot(
        modelResources,
        importedModelsNormalizedPosixPathRelativeToWorkspaceRoot
      );
    if (importedModelsPathsRelativeToWorkspaceRoot && importedModelsPathsRelativeToWorkspaceRoot.length > 0) {
      const importedModelsResources = (
        await Promise.all(
          importedModelsPathsRelativeToWorkspaceRoot.map((normalizedPosixPathRelativeToWorkspaceRoot) =>
            this.args.getModelContent({ normalizedPosixPathRelativeToWorkspaceRoot })
          )
        )
      ).filter((e) => e !== undefined) as DmnLanguageServiceImportedModelResources[];

      if (importedModelsResources.length > 0) {
        return [
          ...importedModelsResources,
          ...(await this.internalGetImportedModelsByModelResources(
            importedModelsResources,
            importedModelsNormalizedPosixPathRelativeToWorkspaceRoot
          )),
        ];
      }
      return [...importedModelsResources];
    }
    return [];
  }

  private async getImportedModelsByModelResources(
    modelsResources: DmnLanguageServiceImportedModelResources[]
  ): Promise<DmnLanguageServiceImportedModelResources[]> {
    return this.internalGetImportedModelsByModelResources(modelsResources, new Set<string>());
  }

  public async getImportedModels(args: ImportedModelsParams) {
    if (args.modelResources) {
      return this.getImportedModelsByModelResources(args.modelResources);
    }

    const importedModelsNormalizedPosixPathRelativeToWorkspaceRoot = new Set<string>();
    importedModelsNormalizedPosixPathRelativeToWorkspaceRoot.add(args.normalizedPosixPathRelativeToWorkspaceRoot);

    const modelResource = [
      (await this.args.getModelContent({
        normalizedPosixPathRelativeToWorkspaceRoot: args.normalizedPosixPathRelativeToWorkspaceRoot,
      })) ?? ([] as DmnLanguageServiceImportedModelResources[]),
    ].flatMap((e) => e);
    return this.internalGetImportedModelsByModelResources(
      modelResource,
      importedModelsNormalizedPosixPathRelativeToWorkspaceRoot
    );
  }

  // Receive all contents, paths and a node ID and returns the model that contains the node.
  public getPathFromNodeId(resourceContents: DmnLanguageServiceImportedModelResources[], nodeId: string): string {
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
      return getMarshaller(modelContent)?.originalVersion;
    } catch (error) {
      return;
    }
  }
}
