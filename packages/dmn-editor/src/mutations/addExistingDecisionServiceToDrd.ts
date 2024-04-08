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

import {
  DMN15__tDecisionService,
  DMN15__tDefinitions,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { DECISION_SERVICE_COLLAPSED_DIMENSIONS } from "../diagram/nodes/DefaultSizes";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { Computed } from "../store/Store";
import { computeContainingDecisionServiceHrefsByDecisionHrefs } from "../store/computed/computeContainingDecisionServiceHrefsByDecisionHrefs.ts";
import { computeIndexedDrd } from "../store/computed/computeIndexes";
import { xmlHrefToQName } from "../xml/xmlHrefToQName";
import { buildXmlHref, parseXmlHref } from "../xml/xmlHrefs";
import { addShape } from "./addShape";
import { repositionNode } from "./repositionNode";

/**
 * When adding a Decision Service to a DRD, we need to bring all its encapsulated and output Decisions with it,
 * copying their layout from other DRDs, or formatting with autolayout.
 */
export function addExistingDecisionServiceToDrd({
  decisionServiceNamespace,
  decisionService,
  externalDmnsIndex,
  thisDmnsNamespace,
  thisDmnsDefinitions,
  thisDmnsIndexedDrd,
  drdIndex,
  dropPoint,
}: {
  decisionServiceNamespace: string;
  decisionService: DMN15__tDecisionService;
  externalDmnsIndex: ReturnType<Computed["getExternalModelTypesByNamespace"]>["dmns"];
  thisDmnsNamespace: string;
  thisDmnsDefinitions: DMN15__tDefinitions;
  thisDmnsIndexedDrd: ReturnType<Computed["indexedDrd"]>;
  drdIndex: number;
  dropPoint: { x: number; y: number };
}) {
  const decisionServiceDmnDefinitions =
    !decisionServiceNamespace || decisionServiceNamespace === thisDmnsNamespace
      ? thisDmnsDefinitions
      : externalDmnsIndex.get(decisionServiceNamespace)?.model.definitions;
  if (!decisionServiceDmnDefinitions) {
    throw new Error(`DMN MUTATION: Can't find definitions for model with namespace ${decisionServiceNamespace}`);
  }
  const { decisionServiceNamespaceForHref, containedDecisionHrefsRelativeToThisDmn } =
    getDecisionServicePropertiesRelativeToThisDmn({
      thisDmnsNamespace,
      decisionServiceNamespace,
      decisionService,
    });

  const decisionServiceHrefRelativeToThisDmn = buildXmlHref({
    namespace: decisionServiceNamespaceForHref,
    id: decisionService["@_id"]!,
  });

  const containingDecisionServiceHrefsByDecisionHrefsRelativeToThisDmn =
    computeContainingDecisionServiceHrefsByDecisionHrefs({
      thisDmnsNamespace,
      drgElementsNamespace: decisionServiceNamespace,
      drgElements: decisionServiceDmnDefinitions.drgElement,
    });

  const doesThisDrdHaveConflictingDecisionService = containedDecisionHrefsRelativeToThisDmn.some((decisionHref) =>
    (containingDecisionServiceHrefsByDecisionHrefsRelativeToThisDmn.get(decisionHref) ?? []).some((d) =>
      thisDmnsIndexedDrd.dmnShapesByHref.has(d)
    )
  );

  if (doesThisDrdHaveConflictingDecisionService) {
    // There's already, in this DRD, a Decision Service in expanded form that contains a Decision that is contained by the Decision Service we're adding.
    // As the DMN specification doesn't allow two copies of the same DRG element to be depicted in the same DRD, we can't add the Decision Service in expanded form.
    // To not disallow depicting the Decision Service in this DRD, though, we add it in collpased form.
    addShape({
      definitions: thisDmnsDefinitions,
      drdIndex,
      nodeType: NODE_TYPES.decisionService,
      shape: {
        "@_dmnElementRef": xmlHrefToQName(decisionServiceHrefRelativeToThisDmn, thisDmnsDefinitions),
        "@_isCollapsed": true,
        "dc:Bounds": {
          "@_x": dropPoint.x,
          "@_y": dropPoint.y,
          "@_width": DECISION_SERVICE_COLLAPSED_DIMENSIONS.width,
          "@_height": DECISION_SERVICE_COLLAPSED_DIMENSIONS.height,
        },
      },
    });
    return;
  }

  const drds = decisionServiceDmnDefinitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"] ?? [];

  let indexedDrd: ReturnType<Computed["indexedDrd"]> | undefined;
  for (let i = 0; i < drds.length; i++) {
    if (thisDmnsNamespace === decisionServiceNamespace && i === drdIndex) {
      continue; // Skip the current DRD!
    }

    const _indexedDrd = computeIndexedDrd(thisDmnsNamespace, decisionServiceDmnDefinitions, i);
    const dsShape = _indexedDrd.dmnShapesByHref.get(decisionServiceHrefRelativeToThisDmn);
    const hasCompleteExpandedDepictionOfDecisionService =
      dsShape &&
      !(dsShape["@_isCollapsed"] ?? false) &&
      containedDecisionHrefsRelativeToThisDmn.every((dHref) => _indexedDrd.dmnShapesByHref.has(dHref));

    if (hasCompleteExpandedDepictionOfDecisionService) {
      indexedDrd = _indexedDrd;
      break; // Found a DRD with a complete expanded depiction of the Decision Service.
    }
  }

  if (!indexedDrd) {
    // There's no DRD which inclues a complete expanded depiction of the Decision Service. Let's proceed with auto-layout.
    // TODO: Tiago
  } else {
    // Let's copy the expanded depiction of the Decision Service from `drd`.
    // Adding or moving nodes that already exist in the current DRD to inside the Decision Service.
    // The positions need all be relative to the Decision Service node, of course.
    const dsShapeOnOtherDrd = indexedDrd.dmnShapesByHref.get(decisionServiceHrefRelativeToThisDmn);
    if (
      dsShapeOnOtherDrd?.["dc:Bounds"]?.["@_x"] === undefined ||
      dsShapeOnOtherDrd?.["dc:Bounds"]?.["@_y"] === undefined
    ) {
      throw new Error(
        `DMN MUTATION: Complete DMNShape for Decision Service with href ${decisionServiceHrefRelativeToThisDmn} should've existed on the indexed DRD.`
      );
    }

    addShape({
      definitions: thisDmnsDefinitions,
      drdIndex,
      nodeType: NODE_TYPES.decisionService,
      shape: {
        "@_dmnElementRef": xmlHrefToQName(decisionServiceHrefRelativeToThisDmn, thisDmnsDefinitions),
        "dc:Bounds": {
          "@_x": dropPoint.x,
          "@_y": dropPoint.y,
          "@_width": dsShapeOnOtherDrd["dc:Bounds"]["@_width"],
          "@_height": dsShapeOnOtherDrd["dc:Bounds"]["@_height"],
        },
      },
    });

    for (const decisionHref of containedDecisionHrefsRelativeToThisDmn) {
      const decisionShapeOnOtherDrd = indexedDrd.dmnShapesByHref.get(decisionHref);
      if (
        decisionShapeOnOtherDrd?.["dc:Bounds"]?.["@_x"] === undefined ||
        decisionShapeOnOtherDrd?.["dc:Bounds"]?.["@_y"] === undefined ||
        decisionShapeOnOtherDrd?.["dc:Bounds"]?.["@_width"] === undefined ||
        decisionShapeOnOtherDrd?.["dc:Bounds"]?.["@_height"] === undefined
      ) {
        throw new Error(
          `DMN MUTATION: Complete DMNShape for Decision with href ${decisionHref} should've existed on the indexed DRD.`
        );
      }

      const x = dropPoint.x + (decisionShapeOnOtherDrd["dc:Bounds"]["@_x"] - dsShapeOnOtherDrd["dc:Bounds"]["@_x"]);
      const y = dropPoint.y + (decisionShapeOnOtherDrd["dc:Bounds"]["@_y"] - dsShapeOnOtherDrd["dc:Bounds"]["@_y"]);

      const existingDecisionShape = thisDmnsIndexedDrd.dmnShapesByHref.get(decisionHref);
      if (existingDecisionShape) {
        repositionNode({
          definitions: thisDmnsDefinitions,
          drdIndex,
          controlWaypointsByEdge: new Map(),
          change: {
            nodeType: NODE_TYPES.decision,
            type: "absolute",
            position: { x, y },
            shapeIndex: existingDecisionShape.index,
            selectedEdges: [],
            sourceEdgeIndexes: [],
            targetEdgeIndexes: [],
          },
        });
      } else {
        const decisionNs = parseXmlHref(decisionHref).namespace;
        const decisionDmnDefinitions =
          !decisionNs || decisionNs === thisDmnsNamespace
            ? thisDmnsDefinitions
            : externalDmnsIndex.get(decisionNs)?.model.definitions;
        if (!decisionDmnDefinitions) {
          throw new Error(`DMN MUTATION: Can't find definitions for model with namespace ${decisionServiceNamespace}`);
        }

        addShape({
          definitions: thisDmnsDefinitions,
          drdIndex,
          nodeType: NODE_TYPES.decision,
          shape: {
            "@_dmnElementRef": xmlHrefToQName(decisionHref, thisDmnsDefinitions),
            "dc:Bounds": {
              "@_x": x,
              "@_y": y,
              "@_width": decisionShapeOnOtherDrd["dc:Bounds"]["@_width"],
              "@_height": decisionShapeOnOtherDrd["dc:Bounds"]["@_height"],
            },
          },
        });
      }
    }
  }
}

export function getDecisionServicePropertiesRelativeToThisDmn({
  thisDmnsNamespace,
  decisionServiceNamespace,
  decisionService,
}: {
  thisDmnsNamespace: string;
  decisionServiceNamespace: string;
  decisionService: DMN15__tDecisionService;
}) {
  const decisionServiceNamespaceForHref =
    decisionServiceNamespace === thisDmnsNamespace ? "" : decisionServiceNamespace;

  const containedDecisionHrefsRelativeToThisDmn = [
    ...(decisionService.outputDecision ?? []),
    ...(decisionService.encapsulatedDecision ?? []),
  ].map((d) => {
    const parsedHref = parseXmlHref(d["@_href"]);
    return buildXmlHref({
      namespace: !parsedHref.namespace ? decisionServiceNamespaceForHref : parsedHref.namespace,
      id: parsedHref.id,
    });
  });

  return { decisionServiceNamespaceForHref, containedDecisionHrefsRelativeToThisDmn };
}
