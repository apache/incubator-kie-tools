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
  DC__Point,
  DMNDI15__DMNEdge,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import * as RF from "reactflow";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { snapPoint } from "../SnapGrid";
import { PositionalNodeHandleId } from "../connections/PositionalNodeHandles";
import { getLineRectangleIntersectionPoint, getHandlePosition, pointsToPath } from "../maths/DmnMaths";
import { getBoundsCenterPoint } from "../maths/Maths";
import { Bounds, getDiscretelyAutoPositionedEdgeParams } from "../maths/Maths";
import { AutoPositionedEdgeMarker } from "./AutoPositionedEdgeMarker";
import { SnapGrid } from "../../store/Store";

export function getSnappedMultiPointAnchoredEdgePath({
  snapGrid,
  dmnEdge,
  sourceNodeBounds,
  targetNodeBounds,
  dmnShapeSource,
  dmnShapeTarget,
}: {
  snapGrid: SnapGrid;
  dmnEdge: DMNDI15__DMNEdge | undefined;
  sourceNodeBounds: Bounds | undefined;
  targetNodeBounds: Bounds | undefined;
  dmnShapeSource: DMNDI15__DMNShape | undefined;
  dmnShapeTarget: DMNDI15__DMNShape | undefined;
}) {
  if (!sourceNodeBounds || !targetNodeBounds) {
    return { path: undefined, points: [] };
  }

  const points: DC__Point[] = new Array(Math.max(2, dmnEdge?.["di:waypoint"]?.length ?? 0));

  const discreteAuto = getDiscretelyAutoPositionedEdgeParams(sourceNodeBounds, targetNodeBounds);

  if (dmnEdge?.["@_id"]?.endsWith(AutoPositionedEdgeMarker.BOTH)) {
    points[0] = { "@_x": discreteAuto.sx, "@_y": discreteAuto.sy };
    points[points.length - 1] = { "@_x": discreteAuto.tx, "@_y": discreteAuto.ty };
  } else if (dmnEdge?.["@_id"]?.endsWith(AutoPositionedEdgeMarker.SOURCE)) {
    points[0] = { "@_x": discreteAuto.sx, "@_y": discreteAuto.sy };
  } else if (dmnEdge?.["@_id"]?.endsWith(AutoPositionedEdgeMarker.TARGET)) {
    points[points.length - 1] = { "@_x": discreteAuto.tx, "@_y": discreteAuto.ty };
  }

  ///////

  if (!dmnEdge?.["di:waypoint"]) {
    console.warn("DMN DIAGRAM: No waypoints found. Creating a default straight line.");
    points[0] = { "@_x": discreteAuto.sx, "@_y": discreteAuto.sy };
    points[points.length - 1] = { "@_x": discreteAuto.tx, "@_y": discreteAuto.ty };
  } else if (dmnEdge?.["di:waypoint"].length < 2) {
    console.warn("DMN DIAGRAM: Invalid waypoints for edge. Creating a default straight line.");
    points[0] = { "@_x": discreteAuto.sx, "@_y": discreteAuto.sy };
    points[points.length - 1] = { "@_x": discreteAuto.tx, "@_y": discreteAuto.ty };
  } else {
    const firstWaypoint = dmnEdge["di:waypoint"][0];
    const secondWaypoint = points[1] ?? dmnEdge["di:waypoint"][1];
    const sourceHandlePoint = getSnappedHandlePosition(
      dmnShapeSource!,
      sourceNodeBounds,
      firstWaypoint,
      points.length === 2 ? getBoundsCenterPoint(targetNodeBounds) : snapPoint(snapGrid, secondWaypoint)
    );
    points[0] ??= sourceHandlePoint;

    const lastWaypoint = dmnEdge["di:waypoint"][dmnEdge["di:waypoint"].length - 1];
    const secondToLastWaypoint = points[points.length - 2] ?? dmnEdge["di:waypoint"][dmnEdge["di:waypoint"].length - 2];
    const targetHandlePoint = getSnappedHandlePosition(
      dmnShapeTarget!,
      targetNodeBounds,
      lastWaypoint,
      points.length === 2 ? getBoundsCenterPoint(sourceNodeBounds) : snapPoint(snapGrid, secondToLastWaypoint)
    );
    points[points.length - 1] ??= targetHandlePoint;
  }

  ///////

  // skip first and last elements, as they are pre-filled using the logic below.
  for (let i = 1; i < points.length - 1; i++) {
    points[i] = snapPoint(snapGrid, { ...(dmnEdge?.["di:waypoint"] ?? [])[i] });
  }

  return { path: pointsToPath(points), points };
}

export function getSnappedHandlePosition(
  shape: DMNDI15__DMNShape,
  snappedNode: Bounds,
  originalHandleWaypoint: DC__Point,
  snappedSecondWaypoint: DC__Point
): DC__Point {
  const { handlePosition } = getHandlePosition({ shapeBounds: shape["dc:Bounds"], waypoint: originalHandleWaypoint });

  const centerHandleWaypoint = getBoundsCenterPoint(snappedNode);

  const nodeRectangle = {
    x: snappedNode.x ?? 0,
    y: snappedNode.y ?? 0,
    width: snappedNode.width ?? 0,
    height: snappedNode.height ?? 0,
  };

  return switchExpression(handlePosition, {
    [PositionalNodeHandleId.Top]: { "@_x": nodeRectangle.x + nodeRectangle.width / 2, "@_y": nodeRectangle.y },
    [PositionalNodeHandleId.Right]: {
      "@_x": nodeRectangle.x + nodeRectangle.width,
      "@_y": nodeRectangle.y + nodeRectangle.height / 2,
    },
    [PositionalNodeHandleId.Bottom]: {
      "@_x": nodeRectangle.x + nodeRectangle.width / 2,
      "@_y": nodeRectangle.y + nodeRectangle.height,
    },
    [PositionalNodeHandleId.Left]: { "@_x": nodeRectangle.x, "@_y": nodeRectangle.y + nodeRectangle.height / 2 },
    [PositionalNodeHandleId.Center]: getLineRectangleIntersectionPoint(
      snappedSecondWaypoint,
      centerHandleWaypoint,
      nodeRectangle
    ),
  });
}
