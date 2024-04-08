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
import { snapShapeDimensions, snapShapePosition } from "../diagram/SnapGrid";
import { PositionalNodeHandleId } from "../diagram/connections/PositionalNodeHandles";
import { NodeType } from "../diagram/connections/graphStructure";
import { getHandlePosition } from "../diagram/maths/DmnMaths";
import { MIN_NODE_SIZES } from "../diagram/nodes/DefaultSizes";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { SnapGrid } from "../store/Store";
import { addOrGetDrd } from "./addOrGetDrd";
import { DECISION_SERVICE_DIVIDER_LINE_PADDING } from "./updateDecisionServiceDividerLine";

export function resizeNode({
  definitions,
  drdIndex,
  dmnShapesByHref,
  snapGrid,
  change,
}: {
  definitions: DMN15__tDefinitions;
  drdIndex: number;
  dmnShapesByHref: Map<string, DMNDI15__DMNShape & { index: number }>;
  snapGrid: SnapGrid;
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

  const shape = diagramElements?.[change.shapeIndex] as DMNDI15__DMNShape | undefined;
  const shapeBounds = shape?.["dc:Bounds"];
  if (!shapeBounds) {
    throw new Error("DMN MUTATION: Cannot resize non-existent shape bounds");
  }

  const limit = { x: 0, y: 0 };
  if (change.nodeType === NODE_TYPES.decisionService) {
    const ds = definitions.drgElement![change.index] as DMN15__tDecisionService;

    const dividerLineY =
      shape["dmndi:DMNDecisionServiceDividerLine"]?.["di:waypoint"]?.[0]?.["@_y"] ?? shapeBounds["@_y"];
    limit.y = dividerLineY + DECISION_SERVICE_DIVIDER_LINE_PADDING;

    // We ignore handling the contents of the Decision Service when it is external
    if (!change.isExternal) {
      ds.encapsulatedDecision?.forEach((ed) => {
        const edShape = dmnShapesByHref.get(ed["@_href"])!;
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
        const edShape = dmnShapesByHref.get(ed["@_href"])!;
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

      const edge = diagramElements[edgeIndex] as DMNDI15__DMNEdge | undefined;
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
}
