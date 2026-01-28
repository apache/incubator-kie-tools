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
import { BPMN20__tDefinitions } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { getDiBoundsCenterPoint, getHandlePosition } from "@kie-tools/xyflow-react-kie-diagram/dist/maths/DcMaths";
import { DC__Dimension, DC__Shape } from "@kie-tools/xyflow-react-kie-diagram/dist/maths/model";
import { PositionalNodeHandleId } from "@kie-tools/xyflow-react-kie-diagram/dist/nodes/PositionalNodeHandles";
import { SnapGrid, snapShapePosition } from "@kie-tools/xyflow-react-kie-diagram/dist/snapgrid/SnapGrid";
import { DC__Edge } from "@kie-tools/xyflow-react-kie-diagram/dist/maths/model";
import { BpmnNodeElement, BpmnNodeType, NODE_TYPES } from "../diagram/BpmnDiagramDomain";
import { Normalized } from "../normalization/normalize";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";
import { nodeNatures } from "./_NodeNature";
import { visitFlowElementsAndArtifacts } from "./_elementVisitor";
import { repositionNode } from "./repositionNode";

export function resizeNode({
  definitions,
  __readonly_snapGrid,
  __readonly_change,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  __readonly_snapGrid: SnapGrid;
  __readonly_change: {
    bpmnElement: BpmnNodeElement;
    nodeType: BpmnNodeType;
    shapeIndex: number;
    dimension: DC__Dimension;
    sourceEdgeIndexes: number[];
    targetEdgeIndexes: number[];
  };
}) {
  const edgeIndexesAlreadyUpdated = new Set<number>();

  const { process, diagramElements } = addOrGetProcessAndDiagramElements({ definitions });

  const shape = diagramElements?.[__readonly_change.shapeIndex] as Normalized<DC__Shape> | undefined;
  const shapeBounds = shape?.["dc:Bounds"];
  if (!shapeBounds) {
    throw new Error("BPMN MUTATION: Cannot resize non-existent shape bounds");
  }

  const limit = { x: 0, y: 0 };

  const snappedPosition = snapShapePosition(__readonly_snapGrid, shape);

  const newDimensions = {
    width: Math.max(__readonly_change.dimension["@_width"], limit.x - snappedPosition.x),
    height: Math.max(__readonly_change.dimension["@_height"], limit.y - snappedPosition.y),
  };

  const deltaWidth = newDimensions.width - shapeBounds["@_width"];
  const deltaHeight = newDimensions.height - shapeBounds["@_height"];

  if (__readonly_change.nodeType === NODE_TYPES.task || __readonly_change.nodeType === NODE_TYPES.subProcess) {
    visitFlowElementsAndArtifacts(process, ({ element }) => {
      if (
        element.__$$element === "boundaryEvent" &&
        element["@_attachedToRef"] === __readonly_change.bpmnElement["@_id"]
      ) {
        const boundaryEventShapeIndex = diagramElements.findIndex((d) => d["@_bpmnElement"] === element["@_id"]);
        const boundaryEventShape = diagramElements[boundaryEventShapeIndex] as Normalized<DC__Shape> | undefined;
        if (!boundaryEventShape) {
          return true; // Ignore this one, but keep visiting.
        }

        const boundaryEventCenterPoint = getDiBoundsCenterPoint(boundaryEventShape["dc:Bounds"]);
        const activityCenterPoint = getDiBoundsCenterPoint(shapeBounds);
        let deltaX = 0;
        if (
          // reisizng down, boundary event would be outside
          boundaryEventCenterPoint["@_x"] > snappedPosition.x + newDimensions.width ||
          // resizing up, boundary event will move together with new size
          boundaryEventCenterPoint["@_x"] > activityCenterPoint["@_x"]
        ) {
          deltaX = deltaWidth;
        }

        let deltaY = 0;
        if (
          // reisizng down, boundary event would be outside
          boundaryEventCenterPoint["@_y"] > snappedPosition.y + newDimensions.height ||
          // resizing up, boundary event will move together with new size
          boundaryEventCenterPoint["@_y"] > activityCenterPoint["@_y"]
        ) {
          deltaY = deltaHeight;
        }

        repositionNode({
          definitions,
          controlWaypointsByEdge: new Map(),
          __readonly_change: {
            nodeType: NODE_TYPES.intermediateCatchEvent,
            type: "offset",
            selectedEdges: [],
            sourceEdgeIndexes: [],
            targetEdgeIndexes: [],
            shapeIndex: boundaryEventShapeIndex,
            offset: {
              deltaX,
              deltaY,
            },
          },
        });
      }
    });
  }

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

      const edge = diagramElements[edgeIndex] as Normalized<DC__Edge> | undefined;
      if (!edge || !edge["di:waypoint"]) {
        throw new Error("BPMN MUTATION: Cannot reposition non-existent edge");
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

  offsetEdges({ edgeIndexes: __readonly_change.sourceEdgeIndexes, waypointSelector: "first" });
  offsetEdges({ edgeIndexes: __readonly_change.targetEdgeIndexes, waypointSelector: "last" });

  // Update at the end because we need the original shapeBounds value to correctly identify the position of the edges

  shapeBounds["@_width"] = newDimensions.width;
  shapeBounds["@_height"] = newDimensions.height;
}
