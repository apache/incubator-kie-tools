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
  DMN15__tDecisionService,
  DMN15__tDefinitions,
  DMNDI15__DMNEdge,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { snapShapeDimensions, snapShapePosition } from "../diagram/SnapGrid";
import { PositionalNodeHandleId } from "../diagram/connections/PositionalNodeHandles";
import { NodeType } from "../diagram/connections/graphStructure";
import { getHandlePosition } from "../diagram/maths/DmnMaths";
import { MIN_NODE_SIZES } from "../diagram/nodes/DefaultSizes";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { SnapGrid } from "../store/Store";
import { addOrGetDrd } from "./addOrGetDrd";
import { DECISION_SERVICE_DIVIDER_LINE_PADDING } from "./updateDecisionServiceDividerLine";
import { ExternalDmnsIndex } from "../DmnEditor";
import { computeIndexedDrd } from "../store/computed/computeIndexes";
import { getDecisionServicePropertiesRelativeToThisDmn } from "./addExistingDecisionServiceToDrd";

export function resizeNode({
  definitions,
  drdIndex,
  __readonly_dmnShapesByHref,
  __readonly_dmnObjectNamespace,
  __readonly_externalDmnsIndex,
  snapGrid,
  __readonly_href,
  __readonly_dmnObjectId,
  change,
}: {
  definitions: Normalized<DMN15__tDefinitions>;
  drdIndex: number;
  __readonly_dmnShapesByHref: Map<string, Normalized<DMNDI15__DMNShape> & { index: number }>;
  snapGrid: SnapGrid;
  __readonly_dmnObjectNamespace: string | undefined;
  __readonly_externalDmnsIndex: ExternalDmnsIndex;
  __readonly_href: string;
  __readonly_dmnObjectId: string;
  change: {
    nodeType: NodeType;
    isExternal: boolean;
    index: number;
    shapeIndex: number;
    dimension: { "@_width": number; "@_height": number };
    sourceEdgeIndexes: number[];
    targetEdgeIndexes: number[];
  };
}) {
  const edgeIndexesAlreadyUpdated = new Set<number>();

  const { diagramElements } = addOrGetDrd({ definitions, drdIndex });

  const shape = diagramElements?.[change.shapeIndex] as Normalized<DMNDI15__DMNShape> | undefined;
  const shapeBounds = shape?.["dc:Bounds"];
  if (!shapeBounds) {
    throw new Error("DMN MUTATION: Cannot resize non-existent shape bounds");
  }

  const limit = { x: 0, y: 0 };
  if (change.nodeType === NODE_TYPES.decisionService) {
    const externalDmn = __readonly_externalDmnsIndex.get(__readonly_dmnObjectNamespace ?? "");

    const ds =
      externalDmn === undefined
        ? (definitions.drgElement![change.index] as Normalized<DMN15__tDecisionService>)
        : (externalDmn.model.definitions.drgElement![change.index] as Normalized<DMN15__tDecisionService>);
    if (!ds) {
      throw new Error("DMN MUTATION: Cannot reposition divider line of non-existent Decision Service");
    }

    const dividerLineY =
      shape["dmndi:DMNDecisionServiceDividerLine"]?.["di:waypoint"]?.[0]?.["@_y"] ?? shapeBounds["@_y"];
    limit.y = dividerLineY + DECISION_SERVICE_DIVIDER_LINE_PADDING;

    // We ignore handling the contents of the Decision Service when it is external
    if (!change.isExternal) {
      ds.encapsulatedDecision?.forEach((ed) => {
        const edShape = __readonly_dmnShapesByHref.get(ed["@_href"])!;
        const dim = snapShapeDimensions(snapGrid, edShape, MIN_NODE_SIZES[NODE_TYPES.decision]({ snapGrid }));
        const pos = snapShapePosition(snapGrid, edShape);
        if (pos.x + dim.width > limit.x) {
          limit.x = pos.x + dim.width;
        }

        if (pos.y + dim.height > limit.y) {
          limit.y = pos.y + dim.height;
        }
      });

      // Output Decisions don't limit the resizing vertically, only horizontally.
      ds.outputDecision?.forEach((ed) => {
        const edShape = __readonly_dmnShapesByHref.get(ed["@_href"])!;
        const dim = snapShapeDimensions(snapGrid, edShape, MIN_NODE_SIZES[NODE_TYPES.decision]({ snapGrid }));
        const pos = snapShapePosition(snapGrid, edShape);
        if (pos.x + dim.width > limit.x) {
          limit.x = pos.x + dim.width;
        }
      });
    }
  }

  const snappedPosition = snapShapePosition(snapGrid, shape);

  const newDimensions = {
    width: Math.max(change.dimension["@_width"], limit.x - snappedPosition.x),
    height: Math.max(change.dimension["@_height"], limit.y - snappedPosition.y),
  };

  const deltaWidth = newDimensions.width - shapeBounds["@_width"];
  const deltaHeight = newDimensions.height - shapeBounds["@_height"];

  const offsetByPosition = (position: PositionalNodeHandleId | undefined) => {
    return switchExpression(position, {
      [PositionalNodeHandleId.Center]: { x: deltaWidth / 2, y: deltaHeight / 2 },
      [PositionalNodeHandleId.Top]: { x: deltaWidth / 2, y: 0 },
      [PositionalNodeHandleId.Right]: { x: deltaWidth, y: deltaHeight / 2 },
      [PositionalNodeHandleId.Bottom]: { x: deltaWidth / 2, y: deltaHeight },
      [PositionalNodeHandleId.Left]: { x: 0, y: deltaHeight / 2 },
    });
  };

  const offsetEdges = (args: { edgeIndexes: number[]; waypointSelector: "last" | "first" }) => {
    for (const edgeIndex of args.edgeIndexes) {
      if (edgeIndexesAlreadyUpdated.has(edgeIndex)) {
        continue;
      }

      edgeIndexesAlreadyUpdated.add(edgeIndex);

      const edge = diagramElements[edgeIndex] as Normalized<DMNDI15__DMNEdge> | undefined;
      if (!edge || !edge["di:waypoint"]) {
        throw new Error("DMN MUTATION: Cannot reposition non-existent edge");
      }

      const waypoint = switchExpression(args.waypointSelector, {
        first: edge["di:waypoint"][0],
        last: edge["di:waypoint"][edge["di:waypoint"].length - 1],
      });

      const offset = offsetByPosition(getHandlePosition({ shapeBounds, waypoint }).handlePosition);
      waypoint["@_x"] += offset.x;
      waypoint["@_y"] += offset.y;
    }
  };

  // Reposition edges after resizing

  offsetEdges({ edgeIndexes: change.sourceEdgeIndexes, waypointSelector: "first" });
  offsetEdges({ edgeIndexes: change.targetEdgeIndexes, waypointSelector: "last" });

  // Update at the end because we need the original shapeBounds value to correctly identify the position of the edges

  shapeBounds["@_width"] = newDimensions.width;
  shapeBounds["@_height"] = newDimensions.height;

  // Handles resizing a decision service in a DRD resizes it in all DRDs to keep Decision Services consistent
  if (change.nodeType === NODE_TYPES.decisionService) {
    const drds = definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"] ?? [];
    const drgElements = definitions.drgElement!;
    for (let i = 0; i < drds.length; i++) {
      if (i === drdIndex) {
        continue;
      }
      const _indexedDrd = computeIndexedDrd(definitions["@_namespace"], definitions, i);
      const dsShape = _indexedDrd.dmnShapesByHref.get(__readonly_href);
      if (dsShape && dsShape["dc:Bounds"] && !shape["@_isCollapsed"]) {
        dsShape["dc:Bounds"]["@_width"] = shape["dc:Bounds"]?.["@_width"] ?? 0;
        dsShape["dc:Bounds"]["@_height"] = shape["dc:Bounds"]?.["@_height"] ?? 0;
      }

      // Apply delta shift to neighbouring nodes in other DRD
      const decisionService = drgElements.find(
        (elem) => elem["@_id"] === __readonly_dmnObjectId
      ) as Normalized<DMN15__tDecisionService>;
      const { containedDecisionHrefsRelativeToThisDmn } = getDecisionServicePropertiesRelativeToThisDmn({
        thisDmnsNamespace: definitions["@_namespace"],
        decisionService,
        decisionServiceNamespace: __readonly_dmnObjectNamespace ?? definitions["@_namespace"],
      });

      const decisionsInDecisionServiceInDrd: string[] = [];
      for (const elem of drgElements) {
        if (elem.__$$element === "decisionService") {
          decisionsInDecisionServiceInDrd.push(
            ...(elem.outputDecision ?? []).map((od) => od["@_href"]),
            ...(elem.encapsulatedDecision ?? []).map((od) => od["@_href"])
          );
        }
      }

      for (const [key] of _indexedDrd.dmnShapesByHref.entries()) {
        if (key !== __readonly_href && !containedDecisionHrefsRelativeToThisDmn.includes(key)) {
          const nodeShape = _indexedDrd.dmnShapesByHref.get(key);
          if (nodeShape && nodeShape["dc:Bounds"] && dsShape && !shape["@_isCollapsed"]) {
            const nodeShapeWidth = nodeShape["dc:Bounds"]!["@_x"] + nodeShape["dc:Bounds"]!["@_width"];
            const nodeShapeHeight = nodeShape["dc:Bounds"]!["@_y"] + nodeShape["dc:Bounds"]!["@_height"];
            const dsShapeWidth = dsShape["dc:Bounds"]!["@_x"] + dsShape["dc:Bounds"]!["@_width"] - deltaWidth;
            const dsShapeHeight = dsShape["dc:Bounds"]!["@_y"] + dsShape["dc:Bounds"]!["@_height"] - deltaHeight;

            const drgElem = drgElements.filter((item) => item["@_id"] === nodeShape["@_dmnElementRef"]);

            const shiftXPosition =
              nodeShape["dc:Bounds"]["@_x"] >= dsShapeWidth &&
              (nodeShapeHeight >= dsShape["dc:Bounds"]!["@_y"] || nodeShape["dc:Bounds"]["@_y"] <= dsShapeHeight);

            const shiftYPosition =
              nodeShape["dc:Bounds"]["@_y"] >= dsShapeHeight &&
              (dsShapeWidth <= nodeShapeWidth || nodeShape["dc:Bounds"]!["@_x"] <= nodeShapeWidth);

            const bounds = nodeShape["dc:Bounds"];

            if (drgElem[0].__$$element === "decisionService") {
              const containedDecisions = [
                ...(drgElem[0].outputDecision ?? []).map((od) => od["@_href"]),
                ...(drgElem[0].encapsulatedDecision ?? []).map((od) => od["@_href"]),
              ];

              if (shiftXPosition || shiftYPosition) {
                const divider = nodeShape["dmndi:DMNDecisionServiceDividerLine"];
                const waypoints = divider?.["di:waypoint"];
                // Handles position shift of decision service
                if (shiftXPosition) {
                  bounds["@_x"] += deltaWidth;
                }

                if (shiftYPosition) {
                  bounds["@_y"] += deltaHeight;
                  waypoints![0]["@_y"] += deltaHeight;
                  waypoints![1]["@_y"] += deltaHeight;
                }
                // Handles position shift of decisions inside decision service
                for (const decision of containedDecisions) {
                  const containedDecisionBounds = _indexedDrd.dmnShapesByHref.get(decision)?.["dc:Bounds"];
                  if (containedDecisionBounds) {
                    if (shiftXPosition) containedDecisionBounds["@_x"] += deltaWidth;
                    if (shiftYPosition) containedDecisionBounds["@_y"] += deltaHeight;
                  }
                }
              }
            } else {
              // Handles position shift of other independent shapes
              if (!decisionsInDecisionServiceInDrd.some((arr) => arr.includes(key))) {
                if (shiftXPosition) {
                  bounds["@_x"] = bounds!["@_x"] + deltaWidth;
                }
                if (shiftYPosition) {
                  bounds["@_y"] = bounds!["@_y"] + deltaHeight;
                }
              }
            }
          }
        }
      }
    }
  }
}
