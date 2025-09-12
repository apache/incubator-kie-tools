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

import * as RF from "reactflow";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { snapPoint } from "../SnapGrid";
import { PositionalNodeHandleId } from "../connections/PositionalNodeHandles";
import { getLineRectangleIntersectionPoint, getHandlePosition, pointsToPath } from "../maths/SwfMaths";
import { Bounds, getBoundsCenterPoint } from "../maths/Maths";
import { getDiscretelyAutoPositionedEdgeParams } from "../maths/Maths";
import { SnapGrid } from "../../store/Store";

export function getSnappedMultiPointAnchoredEdgePath({
  snapGrid,
  waypoints,
  sourceNodeBounds,
  targetNodeBounds,
}: {
  snapGrid: SnapGrid;
  waypoints: RF.XYPosition[] | undefined;
  swfEdge: { index: number } | undefined;
  sourceNodeBounds: Bounds;
  targetNodeBounds: Bounds;
}) {
  if (!sourceNodeBounds || !targetNodeBounds) {
    return { path: undefined, points: [] };
  }

  const points: RF.XYPosition[] = new Array(Math.max(2, waypoints?.length ?? 0));

  const discreteAuto = getDiscretelyAutoPositionedEdgeParams(sourceNodeBounds, targetNodeBounds);

  if (!waypoints) {
    console.warn("SWF DIAGRAM: No waypoints found. Creating a default straight line.");
    points[0] = { x: discreteAuto.sx, y: discreteAuto.sy };
    points[points.length - 1] = { x: discreteAuto.tx, y: discreteAuto.ty };
  } else if (waypoints.length < 2) {
    console.warn("SWF DIAGRAM: Invalid waypoints for edge. Creating a default straight line.");
    points[0] = { x: discreteAuto.sx, y: discreteAuto.sy };
    points[points.length - 1] = { x: discreteAuto.tx, y: discreteAuto.ty };
  } else {
    const firstWaypoint = waypoints[0];
    const secondWaypoint = points[1] ?? waypoints[1];
    const sourceHandlePoint = getSnappedHandlePosition(
      sourceNodeBounds,
      firstWaypoint,
      points.length === 2 ? getBoundsCenterPoint(targetNodeBounds) : snapPoint(snapGrid, secondWaypoint)
    );
    points[0] ??= sourceHandlePoint;

    const lastWaypoint = waypoints[waypoints.length - 1];
    const secondToLastWaypoint = points[points.length - 2] ?? waypoints[waypoints.length - 2];
    const targetHandlePoint = getSnappedHandlePosition(
      targetNodeBounds,
      lastWaypoint,
      points.length === 2 ? getBoundsCenterPoint(sourceNodeBounds) : snapPoint(snapGrid, secondToLastWaypoint)
    );
    points[points.length - 1] ??= targetHandlePoint;
  }

  // ///////

  // skip first and last elements, as they are pre-filled using the logic below.
  for (let i = 1; i < points.length - 1; i++) {
    points[i] = snapPoint(snapGrid, { ...(waypoints ?? [])[i] });
  }

  return { path: pointsToPath(points), points };
}

export function getSnappedHandlePosition(
  snappedNode: Bounds,
  originalHandleWaypoint: RF.XYPosition,
  snappedSecondWaypoint: RF.XYPosition
): RF.XYPosition {
  const { handlePosition } = getHandlePosition({ shapeBounds: snappedNode, waypoint: originalHandleWaypoint });

  const centerHandleWaypoint = getBoundsCenterPoint(snappedNode);

  const nodeRectangle = {
    x: snappedNode.x ?? 0,
    y: snappedNode.y ?? 0,
    width: snappedNode.width ?? 0,
    height: snappedNode.height ?? 0,
  };

  return switchExpression(handlePosition, {
    [PositionalNodeHandleId.Top]: { x: nodeRectangle.x + nodeRectangle.width / 2, y: nodeRectangle.y },
    [PositionalNodeHandleId.Right]: {
      x: nodeRectangle.x + nodeRectangle.width,
      y: nodeRectangle.y + nodeRectangle.height / 2,
    },
    [PositionalNodeHandleId.Bottom]: {
      x: nodeRectangle.x + nodeRectangle.width / 2,
      y: nodeRectangle.y + nodeRectangle.height,
    },
    [PositionalNodeHandleId.Left]: { x: nodeRectangle.x, y: nodeRectangle.y + nodeRectangle.height / 2 },
    [PositionalNodeHandleId.Center]: getLineRectangleIntersectionPoint(
      snappedSecondWaypoint,
      centerHandleWaypoint,
      nodeRectangle
    ),
  });
}
