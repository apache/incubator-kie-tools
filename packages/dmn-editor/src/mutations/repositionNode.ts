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
  DMN15__tDefinitions,
  DMNDI15__DMNEdge,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { NodeType } from "../diagram/connections/graphStructure";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { addOrGetDrd } from "./addOrGetDrd";
import { getCentralizedDecisionServiceDividerLine } from "./updateDecisionServiceDividerLine";

export function repositionNode({
  definitions,
  drdIndex,
  controlWaypointsByEdge,
  change,
}: {
  /**
   * This will keep track of all waypoints that were updated, in the case where multiple nodes move together.
   * This will make sure we only move edges once, even though they might be source/target edges for multiple nodes.
   */
  controlWaypointsByEdge: Map<number, Set<number>>;
  definitions: DMN15__tDefinitions;
  drdIndex: number;
  change: {
    nodeType: NodeType;
    shapeIndex: number;
    sourceEdgeIndexes: number[];
    targetEdgeIndexes: number[];
    selectedEdges: string[];
  } & (
    | {
        type: "absolute";
        position: { x: number; y: number };
      }
    | {
        type: "offset";
        offset: { deltaX: number; deltaY: number };
      }
  );
}) {
  const { diagramElements } = addOrGetDrd({ definitions, drdIndex });

  const shape = diagramElements?.[change.shapeIndex] as DMNDI15__DMNShape | undefined;
  const shapeBounds = shape?.["dc:Bounds"];
  if (!shapeBounds) {
    throw new Error("DMN MUTATION: Cannot reposition non-existent shape bounds");
  }

  let deltaX: number;
  let deltaY: number;
  if (change.type === "absolute") {
    deltaX = change.position.x - (shapeBounds?.["@_x"] ?? 0);
    deltaY = change.position.y - (shapeBounds?.["@_y"] ?? 0);
    shapeBounds["@_x"] = change.position.x;
    shapeBounds["@_y"] = change.position.y;
  } else if (change.type === "offset") {
    deltaX = change.offset.deltaX;
    deltaY = change.offset.deltaY;
    shapeBounds["@_x"] += change.offset.deltaX;
    shapeBounds["@_y"] += change.offset.deltaY;
  } else {
    throw new Error(`DMN MUTATION: Unknown type of node position change '${(change as any).type}'.`);
  }

  const offsetEdges = (args: { edgeIndexes: number[]; waypoint: "last" | "first" }) => {
    for (const edgeIndex of args.edgeIndexes) {
      const edge = diagramElements[edgeIndex] as DMNDI15__DMNEdge | undefined;
      if (!edge || !edge["di:waypoint"]) {
        throw new Error("DMN MUTATION: Cannot reposition non-existent edge");
      }

      const isEdgeSelected = change.selectedEdges.indexOf(edge["@_dmnElementRef"]!) >= 0;

      const waypointIndexes = switchExpression(args.waypoint, {
        first: isEdgeSelected
          ? arrayRange(0, edge["di:waypoint"].length - 2) // All except last element
          : [0],
        last: isEdgeSelected
          ? arrayRange(1, edge["di:waypoint"].length - 1) // All except first element
          : [edge["di:waypoint"].length - 1],
      });

      controlWaypointsByEdge.set(edgeIndex, controlWaypointsByEdge.get(edgeIndex) ?? new Set());
      for (const wi of waypointIndexes) {
        const waypointsControl = controlWaypointsByEdge.get(edgeIndex)!;
        if (waypointsControl.has(wi)) {
          continue;
        } else {
          waypointsControl.add(wi);
        }

        const w = edge["di:waypoint"][wi];
        w["@_x"] += deltaX;
        w["@_y"] += deltaY;
      }
    }
  };

  offsetEdges({ edgeIndexes: change.sourceEdgeIndexes, waypoint: "first" });
  offsetEdges({ edgeIndexes: change.targetEdgeIndexes, waypoint: "last" });

  if (change.nodeType === NODE_TYPES.decisionService) {
    shape["dmndi:DMNDecisionServiceDividerLine"] ??= getCentralizedDecisionServiceDividerLine(shapeBounds);
    const w = shape["dmndi:DMNDecisionServiceDividerLine"]["di:waypoint"]!;

    w[0]["@_x"] += deltaX;
    w[0]["@_y"] += deltaY;

    w[1]["@_x"] += deltaX;
    w[1]["@_y"] += deltaY;
  }

  return {
    delta: {
      x: deltaX,
      y: deltaY,
    },
    newPosition: {
      x: shapeBounds["@_x"],
      y: shapeBounds["@_y"],
    },
  };
}

function arrayRange(start: number, stop: number, step = 1) {
  return Array.from({ length: (stop - start) / step + 1 }, (_, index) => start + index * step);
}
