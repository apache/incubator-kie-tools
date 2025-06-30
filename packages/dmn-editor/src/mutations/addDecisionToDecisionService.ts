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

import { DMN15__tDefinitions, DMNDI15__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { parseXmlHref, xmlHrefToQName } from "@kie-tools/dmn-marshaller/dist/xml";
import { getContainmentRelationship, getDecisionServiceDividerLineLocalY } from "../diagram/maths/DmnMaths";
import { addOrGetDrd } from "./addOrGetDrd";
import { repopulateInputDataAndDecisionsOnDecisionService } from "./repopulateInputDataAndDecisionsOnDecisionService";
import { SnapGrid } from "../store/Store";
import { MIN_NODE_SIZES } from "../diagram/nodes/DefaultSizes";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { ExternalModelsIndex } from "../DmnEditor";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { addShape } from "./addShape";
import { repositionNode } from "./repositionNode";
import { computeIndexedDrd } from "../store/computed/computeIndexes";

export function addDecisionToDecisionService({
  definitions,
  decisionHref,
  decisionServiceId,
  drdIndex,
  snapGrid,
  externalModelsByNamespace,
  __readonly_decisionServiceHref,
}: {
  definitions: Normalized<DMN15__tDefinitions>;
  decisionHref: string;
  decisionServiceId: string;
  drdIndex: number;
  snapGrid: SnapGrid;
  externalModelsByNamespace: ExternalModelsIndex | undefined;
  __readonly_decisionServiceHref: string;
}) {
  console.debug(`DMN MUTATION: Adding Decision '${decisionHref}' to Decision Service '${decisionServiceId}'`);

  const href = parseXmlHref(decisionHref);

  if (href.namespace) {
    const externalModel = externalModelsByNamespace?.[href.namespace];
    if (!externalModel) {
      throw new Error(`DMN MUTATION: Namespace '${href.namespace}' not found.`);
    }

    if (externalModel?.type !== "dmn") {
      throw new Error(`DMN MUTATION: External model with namespace ${href.namespace} is not a DMN.`);
    }

    const externalDrgs = externalModel.model.definitions.drgElement;
    const decision = externalDrgs?.find((drgElement) => drgElement["@_id"] === href.id);
    if (decision?.__$$element !== "decision") {
      throw new Error(
        `DMN MUTATION: DRG Element with id '${href.id}' is either not a Decision or doesn't exist in the external model '${href.namespace}'`
      );
    }
  } else {
    const decision = definitions.drgElement?.find((s) => s["@_id"] === href.id);
    if (decision?.__$$element !== "decision") {
      throw new Error(`DMN MUTATION: DRG Element with id '${href.id}' is either not a Decision or doesn't exist.`);
    }
  }

  const decisionService = definitions.drgElement?.find((s) => s["@_id"] === decisionServiceId);
  if (decisionService?.__$$element !== "decisionService") {
    throw new Error(
      `DMN MUTATION: DRG Element with id '${decisionServiceId}' is either not a Decision Service or doesn't exist.`
    );
  }

  const diagram = addOrGetDrd({ definitions, drdIndex });
  const dmnElementRef = xmlHrefToQName(decisionHref, definitions);

  const decisionShape = diagram.diagramElements.find(
    (s) => s["@_dmnElementRef"] === dmnElementRef && s.__$$element === "dmndi:DMNShape"
  ) as Normalized<DMNDI15__DMNShape>;

  const decisionServiceShape = diagram.diagramElements.find(
    (s) => s["@_dmnElementRef"] === decisionServiceId && s.__$$element === "dmndi:DMNShape"
  ) as Normalized<DMNDI15__DMNShape>;

  const section = getSectionForDecisionInsideDecisionService({ decisionShape, decisionServiceShape, snapGrid });
  if (section === "encapsulated") {
    decisionService.encapsulatedDecision ??= [];
    decisionService.encapsulatedDecision.push({ "@_href": `${decisionHref}` });
  } else if (section === "output") {
    decisionService.outputDecision ??= [];
    decisionService.outputDecision.push({ "@_href": `${decisionHref}` });
  } else {
    throw new Error(`DMN MUTATION: Invalid section to add decision to: '${section}' `);
  }

  repopulateInputDataAndDecisionsOnDecisionService({ definitions, decisionService, externalModelsByNamespace });

  //Adding decisions to decision service in other DRDs
  const drds = definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"] ?? [];
  for (let j = 0; j < drds.length; j++) {
    if (j === drdIndex) {
      continue;
    }
    const _indexedDrd = computeIndexedDrd(definitions["@_namespace"], definitions, j);
    const dsShape = _indexedDrd.dmnShapesByHref.get(__readonly_decisionServiceHref);
    const relativePosinCurrentDS = {
      x: (decisionShape?.["dc:Bounds"]?.["@_x"] ?? 0) - (decisionServiceShape["dc:Bounds"]?.["@_x"] ?? 0),
      y: (decisionShape?.["dc:Bounds"]?.["@_y"] ?? 0) - (decisionServiceShape["dc:Bounds"]?.["@_y"] ?? 0),
    };
    if (
      decisionShape &&
      decisionServiceShape["dc:Bounds"] &&
      dsShape &&
      dsShape["dc:Bounds"] &&
      !dsShape["@_isCollapsed"]
    ) {
      const currentDecisionShape = _indexedDrd.dmnShapesByHref.get(decisionHref);
      if (currentDecisionShape) {
        repositionNode({
          definitions: definitions,
          drdIndex: j,
          controlWaypointsByEdge: new Map(),
          change: {
            nodeType: NODE_TYPES.decision,
            type: "absolute",
            position: {
              x: (dsShape["dc:Bounds"]?.["@_x"] ?? 0) + relativePosinCurrentDS.x,
              y: (dsShape["dc:Bounds"]?.["@_y"] ?? 0) + relativePosinCurrentDS.y,
            },
            shapeIndex: currentDecisionShape.index,
            selectedEdges: [],
            sourceEdgeIndexes: [],
            targetEdgeIndexes: [],
          },
        });
      } else {
        addShape({
          definitions: definitions,
          drdIndex: j,
          nodeType: NODE_TYPES.decision,
          shape: {
            "@_id": generateUuid(),
            "@_dmnElementRef": xmlHrefToQName(decisionHref, definitions),
            "dc:Bounds": {
              "@_x": (dsShape["dc:Bounds"]?.["@_x"] ?? 0) + relativePosinCurrentDS.x,
              "@_y": (dsShape["dc:Bounds"]?.["@_y"] ?? 0) + relativePosinCurrentDS.y,
              "@_width": decisionShape["dc:Bounds"]!["@_width"],
              "@_height": decisionShape["dc:Bounds"]!["@_height"],
            },
          },
        });
      }
    }
    //Removing decisions if service is collapsed
    if (dsShape && dsShape["@_isCollapsed"]) {
      const { diagramElements } = addOrGetDrd({
        definitions: definitions,
        drdIndex: j,
      });
      const dmnShapeIndex = (diagramElements ?? []).findIndex((d) => d["@_dmnElementRef"] === href.id);
      if (dmnShapeIndex >= 0) {
        diagramElements?.splice(dmnShapeIndex, 1);
      }
    }
  }
}

export function getSectionForDecisionInsideDecisionService({
  decisionShape,
  decisionServiceShape,
  snapGrid,
}: {
  decisionShape: Normalized<DMNDI15__DMNShape>;
  decisionServiceShape: Normalized<DMNDI15__DMNShape>;
  snapGrid: SnapGrid;
}): "output" | "encapsulated" {
  if (!decisionShape?.["dc:Bounds"] || !decisionServiceShape?.["dc:Bounds"]) {
    throw new Error(
      `DMN MUTATION: Can't determine Decision Service section for Decision '${decisionShape["@_dmnElementRef"]}' because it doens't have a DMNShape.`
    );
  }

  const contaimentRelationship = getContainmentRelationship({
    bounds: decisionShape["dc:Bounds"],
    container: decisionServiceShape["dc:Bounds"],
    divingLineLocalY: getDecisionServiceDividerLineLocalY(decisionServiceShape),
    snapGrid,
    isAlternativeInputDataShape: false,
    containerMinSizes: MIN_NODE_SIZES[NODE_TYPES.decisionService],
    boundsMinSizes: MIN_NODE_SIZES[NODE_TYPES.decision],
  });

  if (!contaimentRelationship.isInside) {
    throw new Error(
      `DMN MUTATION: Decision '${decisionShape["@_dmnElementRef"]}' can't be added to Decision Service '${decisionServiceShape["@_dmnElementRef"]}' because its shape is not visually contained by the Decision Service's shape.`
    );
  }

  return contaimentRelationship.section === "upper" ? "output" : "encapsulated";
}
