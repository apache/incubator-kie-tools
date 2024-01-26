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

export function deleteEdge({
  definitions,
  drdIndex,
  edge,
}: {
  definitions: DMN15__tDefinitions;
  drdIndex: number;
  edge: { id: string; dmnObject: DmnDiagramEdgeData["dmnObject"] };
}) {
  if (edge.dmnObject.namespace !== definitions["@_namespace"]) {
    console.debug("DMN MUTATION: Can't delete an edge that's from an external node.");
    return { dmnEdge: undefined };
  }

  const { diagramElements } = addOrGetDrd({ definitions, drdIndex });

  const dmnObjects: DMN15__tDefinitions["artifact"] | DMN15__tDefinitions["drgElement"] =
    switchExpression(edge?.dmnObject.type, {
      association: definitions.artifact,
      default: definitions.drgElement,
    }) ?? [];

  const dmnObjectIndex = dmnObjects.findIndex((d) => d["@_id"] === edge.dmnObject.id);
  if (dmnObjectIndex < 0) {
    throw new Error(`DMN MUTATION: Can't find DMN element with ID ${edge.dmnObject.id}`);
  }

  const requirements =
    switchExpression(edge?.dmnObject.requirementType, {
      // Casting to DMN15__tDecision because if has all types of requirement, but not necessarily that's true.
      informationRequirement: (dmnObjects[dmnObjectIndex] as DMN15__tDecision).informationRequirement,
      knowledgeRequirement: (dmnObjects[dmnObjectIndex] as DMN15__tDecision).knowledgeRequirement,
      authorityRequirement: (dmnObjects[dmnObjectIndex] as DMN15__tDecision).authorityRequirement,
      association: dmnObjects,
    }) ?? [];

  // Deleting the requirement
  const requirementIndex = (requirements ?? []).findIndex((d) => d["@_id"] === edge.id);
  if (requirementIndex >= 0) {
    requirements?.splice(requirementIndex, 1);
  }

  // Deleting the DMNEdge's
  let dmnEdge: DMNDI15__DMNEdge | undefined;
  const dmnEdgeIndex = (diagramElements ?? []).findIndex((d) => d["@_dmnElementRef"] === edge.id);
  if (dmnEdgeIndex >= 0) {
    dmnEdge = diagramElements[dmnEdgeIndex];
    diagramElements?.splice(dmnEdgeIndex, 1);
  }

  repopulateInputDataAndDecisionsOnAllDecisionServices({ definitions });

  return { dmnEdge };
}
