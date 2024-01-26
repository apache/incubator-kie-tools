/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { NodeNature } from "./NodeNature";
import { addOrGetDrd } from "./addOrGetDrd";
import { repopulateInputDataAndDecisionsOnAllDecisionServices } from "./repopulateInputDataAndDecisionsOnDecisionService";
import { XmlQName, buildXmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";
import { getNewDmnIdRandomizer } from "../idRandomizer/dmnIdRandomizer";

export enum NodeDeletionMode {
  FORM_DRG_AND_DRD,
  FROM_DRD_ONLY,
}

export function deleteNode({
  definitions,
  drdIndex,
  nodeNature,
  dmnObjectId,
  dmnObjectQName,
  mode,
}: {
  definitions: DMN15__tDefinitions;
  drdIndex: number;
  nodeNature: NodeNature;
  dmnObjectId: string | undefined;
  dmnObjectQName: XmlQName;
  mode: NodeDeletionMode;
}) {
  const { diagramElements, widthsExtension } = addOrGetDrd({ definitions, drdIndex });

  // Edges need to be deleted by a separate call to `deleteEdge` prior to this.

  // delete the DMNShape
  const shapeDmnElementRef = buildXmlQName(dmnObjectQName);
  diagramElements?.splice(
    (diagramElements ?? []).findIndex((d) => d["@_dmnElementRef"] === shapeDmnElementRef),
    1
  );

  // External or unknown nodes don't have a dmnObject associated with it, just the shape..
  if (!dmnObjectQName.prefix) {
    // Delete the dmnObject itself
    if (nodeNature === NodeNature.ARTIFACT) {
      if (mode === NodeDeletionMode.FORM_DRG_AND_DRD) {
        const nodeIndex = (definitions.artifact ?? []).findIndex((a) => a["@_id"] === dmnObjectId);
        definitions.artifact?.splice(nodeIndex, 1);
      }
    } else if (nodeNature === NodeNature.DRG_ELEMENT) {
      const nodeIndex = (definitions.drgElement ?? []).findIndex((d) => d["@_id"] === dmnObjectId);
      const node =
        mode === NodeDeletionMode.FORM_DRG_AND_DRD
          ? definitions.drgElement?.splice(nodeIndex, 1)
          : [definitions.drgElement?.[nodeIndex]];

      const deletedIdsOnDrgElementTree = getNewDmnIdRandomizer()
        .ack({ json: node, type: "DMN15__tDefinitions", attr: "drgElement" })
        .getOriginalIds();

      // Delete widths
      widthsExtension["kie:ComponentWidths"] = widthsExtension["kie:ComponentWidths"]?.filter(
        (w) => !deletedIdsOnDrgElementTree.has(w["@_dmnElementRef"]!)
      );
    } else if (nodeNature === NodeNature.UNKNOWN) {
      // Ignore. There's no dmnObject here.
    } else {
      throw new Error(`Unknown node nature '${nodeNature}'.`);
    }
  }

  repopulateInputDataAndDecisionsOnAllDecisionServices({ definitions });
}
