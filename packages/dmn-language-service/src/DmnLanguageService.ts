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

export interface DmnLanguageServiceResource {
  content: string;
  normalizedPosixPathRelativeToTheWorkspaceRoot: string;
}

/**
 * TODO: Write this documentation
 *
 * This type represents the ...
 * string: normalizedPosixPathRelativeToTheWorkspaceRoot
 * .
 */
export interface ImportIndex {
  hierarchy: Map<
    string,
    {
      deep: Set<string>;
      immediate: Set<string>;
    }
  >;
  models: Map<
    string,
    {
      definitions: DMN15__tDefinitions;
      xml: string;
    }
  >;
}

export class DmnLanguageService {
  private readonly parser = new DOMParser(); // TODO: Delete this when the new Marshaller is being used for everything.
  private readonly inputDataRegEx = new RegExp(`([a-z]*:)?(${INPUT_DATA})`); // TODO: Delete this when the new Marshaller is being used for everything.
  private readonly decisionsTagRegExp = new RegExp(`([a-z]*:)?(${DECISION})`); // TODO: Delete this when the new Marshaller is being used for everything.
  private readonly definitionsTagRegExp = new RegExp(`([a-z]*:)?(${DEFINITIONS})`); // TODO: Delete this when the new Marshaller is being used for everything.

  constructor(
    private readonly args: {
      getModelXml: (args: { normalizedPosixPathRelativeToTheWorkspaceRoot: string }) => Promise<string>;
    }
  ) {}

  private async buildImportIndexModel(normalizedPosixPathRelativeToTheWorkspaceRoot: string) {
    const xml = await this.args.getModelXml({ normalizedPosixPathRelativeToTheWorkspaceRoot });
    return {
      definitions: getMarshaller(xml, { upgradeTo: "latest" }).parser.parse().definitions,
      xml,
    };
  }

  private async recusivelyPopulateImportIndex(
    normalizedPosixPathRelativeToTheWorkspaceRoot: string,
    importIndex: ImportIndex,
    parents: string[],
    depth: number
  ): Promise<void> {
    // Depth === -1 means we're going to recursve forever.
    // Depth === 0 means we'll stop without including any imports in the index
    // Depth > 0 means we'll keep going down one level of imports at a time and add them to the index, when it reaches zero, we stop.
    if (depth === 0) {
      return;
    }

    // Add the current model to the index if not present
    const model =
      importIndex.models.get(normalizedPosixPathRelativeToTheWorkspaceRoot) ??
      importIndex.models
        .set(
          normalizedPosixPathRelativeToTheWorkspaceRoot,
          await this.buildImportIndexModel(normalizedPosixPathRelativeToTheWorkspaceRoot)
        )
        .get(normalizedPosixPathRelativeToTheWorkspaceRoot)!;

    // Ensure a hierarchy always exists
    const hierarchy =
      importIndex.hierarchy.get(normalizedPosixPathRelativeToTheWorkspaceRoot) ??
      importIndex.hierarchy
        .set(normalizedPosixPathRelativeToTheWorkspaceRoot, { immediate: new Set(), deep: new Set() })
        .get(normalizedPosixPathRelativeToTheWorkspaceRoot)!;

    // Iterate over the imports
    const basedir = path.dirname(normalizedPosixPathRelativeToTheWorkspaceRoot);
    for (const i of model.definitions.import ?? []) {
      const locationUri = i["@_locationURI"];
      if (!locationUri) {
        // TODO: Write a test for this case temporarily, while we still depend on `locationURI`s and not exclusively on the namespace.
        // Can't determine import without a locationURI.
        console.warn(`Ignoring import with namespace '${i["@_namespace"]}' because it doesn't have a locationURI.`);
        continue;
      }

      /** Normalized POSIX path of the DMN import relative to the workspace root.*/
      const p = path.posix.join(basedir, path.posix.normalize(locationUri));

      // Prevent cycles by looking if already in immediate hierarchy
      if (hierarchy.immediate.has(p)) {
        // We're going to abort the recursion, but some parents might not have had this import's hierarchy added to their deep dependencies lists.
        for (const parent of parents) {
          const parentHierarchy = importIndex.hierarchy.get(parent);

          parentHierarchy?.deep.add(p);

          for (const i of importIndex.hierarchy.get(p)?.deep ?? []) {
            parentHierarchy?.deep.add(i);
          }
        }

        continue; // Abort recursion, as a cycle has been found.
      }

      // Proceed normally, first by adding `p` to the hierarchy and to the parents too. Then recursing one more level down.
      hierarchy.immediate.add(p);
      hierarchy.deep.add(p);
      for (const parent of parents) {
        importIndex.hierarchy.get(parent)?.deep.add(p);
      }

      parents.push(normalizedPosixPathRelativeToTheWorkspaceRoot);
      await this.recusivelyPopulateImportIndex(p, importIndex, parents, depth - 1);
      parents.pop(); // TODO: Add a test that actually breaks if we remove this line.
    }
  }

  public async buildImportIndex(
    resources: DmnLanguageServiceResource[],
    depth = -1 // By default, we recurse infinitely.
  ): Promise<ImportIndex> {
    try {
      const importIndex: ImportIndex = {
        hierarchy: new Map(),
        models: new Map(
          resources.map((r) => [
            r.normalizedPosixPathRelativeToTheWorkspaceRoot,
            {
              xml: r.content,
              definitions: getMarshaller(r.content, { upgradeTo: "latest" }).parser.parse().definitions,
            },
          ])
        ),
      };

      for (const r of resources) {
        await this.recusivelyPopulateImportIndex(
          r.normalizedPosixPathRelativeToTheWorkspaceRoot,
          importIndex, // will be modified
          [], // parents stack
          depth // unaltered initial depth
        );
      }

      return importIndex;
    } catch (error) {
      throw new Error(`
DMN LANGUAGE SERVICE - buildImportIndex: Error while getting imported models from model resources.
Tried to use the following model resources: ${JSON.stringify(resources)}
Error details: ${error}`);
    }
  }

  // TODO: Rewrite this using the new Marshaller.
  // Receive all contents, paths and a node ID and returns the model that contains the node.
  public getPathFromNodeId(resources: DmnLanguageServiceResource[], nodeId: string): string {
    for (const resourceContent of resources) {
      const xmlContent = this.parser.parseFromString(resourceContent.content ?? "", XML_MIME);
      const inputDataTag = this.inputDataRegEx.exec(resourceContent.content ?? "");
      const inputs = xmlContent.getElementsByTagName(inputDataTag?.[0] ?? INPUT_DATA);
      for (const input of Array.from(inputs)) {
        if (input.id === nodeId) {
          return resourceContent.normalizedPosixPathRelativeToTheWorkspaceRoot;
        }
      }
    }
    return "";
  }

  // TODO: Rewrite this using the new Marshaller.
  public getDmnDocumentData(xml: string): DmnDocumentData {
    const xmlContent = this.parser.parseFromString(xml, XML_MIME);
    const definitionsTag = this.definitionsTagRegExp.exec(xml);
    const definitions = xmlContent.getElementsByTagName(definitionsTag ? definitionsTag[0] : DEFINITIONS);
    const definition = definitions[0];
    const namespace = definition.getAttribute(NAMESPACE);
    const dmnModelName = definition.getAttribute(DMN_NAME);

    const dmnDecisions = this.decisionsTagRegExp.exec(xml);
    const dmnDecisionsContent = xmlContent.getElementsByTagName(dmnDecisions ? dmnDecisions[0] : DECISION);

    const decisions = Array.from(dmnDecisionsContent)
      .map((decision) => decision.getAttribute(DECISION_NAME_ATTRIBUTE))
      .flatMap((decisionName) => (decisionName ? [new DmnDecision(decisionName)] : []));
    return new DmnDocumentData(namespace ?? "", dmnModelName ?? "", decisions);
  }

  public getSpecVersion(xml: string) {
    if (xml === "") {
      return;
    }

    try {
      return getMarshaller(xml).originalVersion;
    } catch (error) {
      return;
    }
  }
}
