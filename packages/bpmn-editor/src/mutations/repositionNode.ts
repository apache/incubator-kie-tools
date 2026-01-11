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
import { BPMN20__tDefinitions, BPMNDI__BPMNEdge } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { DC__Shape } from "@kie-tools/xyflow-react-kie-diagram/dist/maths/model";
import { BpmnNodeType } from "../diagram/BpmnDiagramDomain";
import { Normalized } from "../normalization/normalize";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";

export function repositionNode({
  definitions,
  controlWaypointsByEdge,
  __readonly_change,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  /**
   * This will keep track of all waypoints that were updated, in the case where multiple nodes move together.
   * This will make sure we only move edges once, even though they might be source/target edges for multiple nodes.
   */
  controlWaypointsByEdge: Map<number, Set<number>>;
  __readonly_change: {
    nodeType: BpmnNodeType;
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
  const { diagramElements } = addOrGetProcessAndDiagramElements({ definitions });

  const shape = diagramElements?.[__readonly_change.shapeIndex] as Normalized<DC__Shape> | undefined;
  const shapeBounds = shape?.["dc:Bounds"];
  if (!shapeBounds) {
    throw new Error("BPMN MUTATION: Cannot reposition non-existent shape bounds");
  }

  let deltaX: number;
  let deltaY: number;
  if (__readonly_change.type === "absolute") {
    deltaX = __readonly_change.position.x - (shapeBounds?.["@_x"] ?? 0);
    deltaY = __readonly_change.position.y - (shapeBounds?.["@_y"] ?? 0);
    shapeBounds["@_x"] = __readonly_change.position.x;
    shapeBounds["@_y"] = __readonly_change.position.y;
  } else if (__readonly_change.type === "offset") {
    deltaX = __readonly_change.offset.deltaX;
    deltaY = __readonly_change.offset.deltaY;
    shapeBounds["@_x"] += __readonly_change.offset.deltaX;
    shapeBounds["@_y"] += __readonly_change.offset.deltaY;
  } else {
    throw new Error(`BPMN MUTATION: Unknown type of node position change '${(__readonly_change as any).type}'.`);
  }

  const offsetEdges = (args: { edgeIndexes: number[]; waypoint: "last" | "first" }) => {
    for (const edgeIndex of args.edgeIndexes) {
      const edge = diagramElements[edgeIndex] as Normalized<BPMNDI__BPMNEdge> | undefined;
      if (!edge || !edge["di:waypoint"]) {
        throw new Error("BPMN MUTATION: Cannot reposition non-existent edge");
      }

      const isEdgeSelected = __readonly_change.selectedEdges.indexOf(edge["@_bpmnElement"]!) >= 0;

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

  offsetEdges({ edgeIndexes: __readonly_change.sourceEdgeIndexes, waypoint: "first" });
  offsetEdges({ edgeIndexes: __readonly_change.targetEdgeIndexes, waypoint: "last" });

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
