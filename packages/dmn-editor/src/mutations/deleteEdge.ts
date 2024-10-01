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

import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import {
  DMN15__tDecision,
  DMN15__tDefinitions,
  DMNDI15__DMNEdge,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { addOrGetDrd } from "./addOrGetDrd";
import { DmnDiagramEdgeData } from "../diagram/edges/Edges";
import { repopulateInputDataAndDecisionsOnAllDecisionServices } from "./repopulateInputDataAndDecisionsOnDecisionService";
import { Normalized } from "../normalization/normalize";
import { xmlHrefToQName } from "../xml/xmlHrefToQName";
import { ExternalModelsIndex } from "../DmnEditor";

export enum EdgeDeletionMode {
  FROM_DRG_AND_ALL_DRDS,
  FROM_CURRENT_DRD_ONLY,
}

export function deleteEdge({
  definitions,
  drdIndex,
  edge,
  mode,
  externalModelsByNamespace,
}: {
  definitions: Normalized<DMN15__tDefinitions>;
  drdIndex: number;
  edge: { id: string; dmnObject: DmnDiagramEdgeData["dmnObject"] };
  mode: EdgeDeletionMode;
  externalModelsByNamespace: ExternalModelsIndex | undefined;
}) {
  if (edge.dmnObject.namespace === definitions["@_namespace"]) {
    const dmnObjects: Normalized<DMN15__tDefinitions>["drgElement" | "artifact"] =
      switchExpression(edge?.dmnObject.type, {
        association: definitions.artifact,
        group: definitions.artifact,
        default: definitions.drgElement,
      }) ?? [];

    const dmnObjectIndex = dmnObjects.findIndex((d) => d["@_id"] === edge.dmnObject.id);
    if (dmnObjectIndex < 0) {
      throw new Error(`DMN MUTATION: Can't find DMN element with ID ${edge.dmnObject.id}`);
    }

    if (mode === EdgeDeletionMode.FROM_DRG_AND_ALL_DRDS) {
      const requirements =
        switchExpression(edge?.dmnObject.requirementType, {
          // Casting to DMN15__tDecision because if has all types of requirement, but not necessarily that's true.
          informationRequirement: (dmnObjects[dmnObjectIndex] as Normalized<DMN15__tDecision>).informationRequirement,
          knowledgeRequirement: (dmnObjects[dmnObjectIndex] as Normalized<DMN15__tDecision>).knowledgeRequirement,
          authorityRequirement: (dmnObjects[dmnObjectIndex] as Normalized<DMN15__tDecision>).authorityRequirement,
          association: dmnObjects,
        }) ?? [];

      // Deleting the requirement
      const requirementIndex = (requirements ?? []).findIndex((d) => d["@_id"] === edge.id);
      if (requirementIndex >= 0) {
        requirements?.splice(requirementIndex, 1);
      }
    }
  }

  // Deleting the DMNEdge's
  // needs to be executed even if edge.dmnObject.namespace !== definitions["@_namespace"]
  // As they may be DMNEdge depictions for edges targeting external nodes
  let deletedDmnEdgeOnCurrentDrd: Normalized<DMNDI15__DMNEdge> | undefined;

  const drdCount = (definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"] ?? []).length;
  for (let i = 0; i < drdCount; i++) {
    const { diagramElements } = addOrGetDrd({ definitions, drdIndex: i });

    if (mode === EdgeDeletionMode.FROM_CURRENT_DRD_ONLY && i !== drdIndex) {
      continue;
    }

    const dmnEdgeIndex = (diagramElements ?? []).findIndex(
      (d) => d["@_dmnElementRef"] === xmlHrefToQName(edge.id, definitions)
    );
    if (dmnEdgeIndex >= 0) {
      if (i === drdIndex) {
        deletedDmnEdgeOnCurrentDrd = diagramElements[dmnEdgeIndex];
      }

      diagramElements?.splice(dmnEdgeIndex, 1);
    }
  }

  repopulateInputDataAndDecisionsOnAllDecisionServices({ definitions, externalModelsByNamespace });

  return { deletedDmnEdgeOnCurrentDrd };
}
