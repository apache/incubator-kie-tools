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
import { SnapGrid } from "../../store/Store";
import { snapBoundsDimensions, snapBoundsPosition } from "../SnapGrid";
import { PositionalNodeHandleId } from "../connections/PositionalNodeHandles";
import { AutoPositionedEdgeMarker } from "../edges/AutoPositionedEdgeMarker";
import { NODE_TYPES } from "../nodes/SwfNodeTypes";
import { NodeSwfObjects } from "../nodes/SwfNodes";
import { Bounds, getCenter } from "./Maths";
import * as RF from "reactflow";

export const DEFAULT_INTRACTION_WIDTH = 40;
export const CONTAINER_NODES_DESIRABLE_PADDING = 60;

export function getDistance(a: RF.XYPosition, b: RF.XYPosition) {
  return Math.sqrt(Math.pow(a["x"] - b["x"], 2) + Math.pow(a["y"] - b["y"], 2));
}

export function getSwfBoundsCenterPoint(bounds: RF.Rect): RF.XYPosition {
  const { x, y } = getCenter(bounds["x"], bounds["y"], bounds["width"], bounds["height"]);
  return { x: x, y: y };
}

export function getPointForHandle({
  handle,
  bounds,
}: {
  bounds: RF.Rect;
  handle: PositionalNodeHandleId;
}): RF.XYPosition {
  if (handle === PositionalNodeHandleId.Center) {
    return getSwfBoundsCenterPoint(bounds);
  } else if (handle === PositionalNodeHandleId.Top) {
    return { x: bounds["x"] + bounds["width"] / 2, y: bounds["y"] };
  } else if (handle === PositionalNodeHandleId.Right) {
    return { x: bounds["x"] + bounds["width"], y: bounds["y"] + bounds["height"] / 2 };
  } else if (handle === PositionalNodeHandleId.Bottom) {
    return { x: bounds["x"] + bounds["width"] / 2, y: bounds["y"] + bounds["height"] };
  } else if (handle === PositionalNodeHandleId.Left) {
    return { x: bounds["x"], y: bounds["y"] + bounds["height"] / 2 };
  } else {
    throw new Error(`Invalid target handle id '${handle}'.`);
  }
}

export function getHandlePosition({ shapeBounds, waypoint }: { shapeBounds: Bounds; waypoint: RF.XYPosition }): {
  handlePosition: PositionalNodeHandleId;
  point: RF.XYPosition;
} {
  const x = shapeBounds?.["x"] ?? 0;
  const y = shapeBounds?.["y"] ?? 0;
  const w = shapeBounds?.["width"] ?? 0;
  const h = shapeBounds?.["height"] ?? 0;

  const center = { x: x + w / 2, y: y + h / 2 };
  const left = { x: x, y: y + h / 2 };
  const right = { x: x + w, y: y + h / 2 };
  const top = { x: x + w / 2, y: y };
  const bottom = { x: x + w / 2, y: y + h };

  if (getDistance(center, waypoint) <= 1) {
    return { handlePosition: PositionalNodeHandleId.Center, point: center };
  } else if (getDistance(top, waypoint) <= 1) {
    return { handlePosition: PositionalNodeHandleId.Top, point: top };
  } else if (getDistance(right, waypoint) <= 1) {
    return { handlePosition: PositionalNodeHandleId.Right, point: right };
  } else if (getDistance(bottom, waypoint) <= 1) {
    return { handlePosition: PositionalNodeHandleId.Bottom, point: bottom };
  } else if (getDistance(left, waypoint) <= 1) {
    return { handlePosition: PositionalNodeHandleId.Left, point: left };
  } else {
    console.warn("SWF DIAGRAM: Can't find a match of NSWE/Center handles. Using Center as default.");
    return { handlePosition: PositionalNodeHandleId.Center, point: center };
  }
}

export function getLineRectangleIntersectionPoint(
  point1: RF.XYPosition,
  point2: RF.XYPosition,
  rectangle: {
    x: number;
    y: number;
    width: number;
    height: number;
  }
): RF.XYPosition {
  const [x1, y1] = [point1["x"], point1["y"]];
  const [x2, y2] = [point2["x"], point2["y"]];
  const [rx, ry] = [rectangle.x, rectangle.y];
  const [rw, rh] = [rectangle.width, rectangle.height];

  // Calculate the line equation: y = mx + b
  const m = (y2 - y1) / (x2 - x1);
  const b = y1 - m * x1;

  if (m === Infinity || m === -Infinity) {
    // Vertical line
    const x = point1["x"];
    const minY = Math.min(point1["y"], point2["y"]);
    const maxY = Math.max(point1["y"], point2["y"]);

    if (x >= rectangle.x && x <= rectangle.x + rectangle.width) {
      if (minY <= rectangle.y) {
        return { x: x, y: rectangle.y };
      } else if (maxY >= rectangle.y + rectangle.height) {
        return { x: x, y: rectangle.y + rectangle.height };
      }
    }
  }

  // Check intersections with rectangle sides
  const intersections: { x: number; y: number }[] = [];

  // Top side (y = ry)
  const topX = Math.round((ry - b) / m);
  if (topX >= rx && topX <= rx + rw) {
    intersections.push({ x: topX, y: ry });
  }

  // Bottom side (y = ry + rh)
  const bottomX = Math.round((ry + rh - b) / m);
  if (bottomX >= rx && bottomX <= rx + rw) {
    intersections.push({ x: bottomX, y: ry + rh });
  }

  // Left side (x = rx)
  const leftY = Math.round(m * rx + b);
  if (leftY >= ry && leftY <= ry + rh) {
    intersections.push({ x: rx, y: leftY });
  }

  // Right side (x = rx + rw)
  const rightY = Math.round(m * (rx + rw) + b);
  if (rightY >= ry && rightY <= ry + rh) {
    intersections.push({ x: rx + rw, y: rightY });
  }

  // Find the closest intersection point to the line segment
  let closestIntersection: { x: number; y: number } | null = null;

  for (const intersection of intersections) {
    if (!closestIntersection || minDistance(intersection, x1, y1) < minDistance(closestIntersection, x1, y1)) {
      closestIntersection = intersection;
    }
  }

  return (
    (closestIntersection && {
      x: closestIntersection.x,
      y: closestIntersection.y,
    }) ||
    point2
  );
}

const minDistance = (point: { x: number; y: number }, x1: number, y1: number) =>
  Math.pow(point.x - x1, 2) + Math.pow(point.y - y1, 2);

// SWF does not have containment by it can make sense in the future
export function getContainmentRelationship({
  bounds,
  container,
  divingLineLocalY,
  snapGrid,
  containerMinSizes,
  boundsMinSizes,
}: {
  bounds: RF.Rect;
  container: RF.Rect;
  divingLineLocalY?: number;
  snapGrid: SnapGrid;
  containerMinSizes: (args: { snapGrid: SnapGrid }) => RF.Dimensions;
  boundsMinSizes: (args: { snapGrid: SnapGrid }) => RF.Dimensions;
}): { isInside: true; section: "upper" | "lower" } | { isInside: false } {
  const { x: cx, y: cy } = snapBoundsPosition(snapGrid, container);
  const { width: cw, height: ch } = snapBoundsDimensions(snapGrid, container, containerMinSizes({ snapGrid }));
  const { x: bx, y: by } = snapBoundsPosition(snapGrid, bounds);
  const { width: bw, height: bh } = snapBoundsDimensions(snapGrid, bounds, boundsMinSizes({ snapGrid }));

  const center = getSwfBoundsCenterPoint({
    height: bh,
    width: bw,
    x: bx,
    y: by,
  });

  const isInside =
    bx >= cx && // force-line-break
    by >= cy && // force-line-break
    bx + bw <= cx + cw && // force-line-break
    by + bh <= cy + ch;

  if (isInside) {
    return { isInside: true, section: center["y"] > cy + (divingLineLocalY ?? 0) ? "lower" : "upper" };
  } else {
    return { isInside: false };
  }
}

export function pointsToPath(points: RF.XYPosition[]): string {
  const start = points[0];
  let path = `M ${start["x"]},${start["y"]}`;
  for (let i = 1; i < points.length - 1; i++) {
    const p = points[i];
    path += ` L ${p["x"]},${p["y"]} M ${p["x"]},${p["y"]}`;
  }
  const end = points[points.length - 1];
  path += ` L ${end["x"]},${end["y"]}`;

  return path;
}

export const DISCRETE_AUTO_POSITIONING_SWF_EDGE_ID_MARKER = [
  AutoPositionedEdgeMarker.BOTH, // This needs to be the first element.
  AutoPositionedEdgeMarker.SOURCE,
  AutoPositionedEdgeMarker.TARGET,
];
export function getDiscreteAutoPositioningEdgeIdMarker(edgeId: string): AutoPositionedEdgeMarker | undefined {
  for (const marker of DISCRETE_AUTO_POSITIONING_SWF_EDGE_ID_MARKER) {
    if (edgeId.endsWith(marker)) {
      return marker;
    }
  }

  return undefined;
}

export function getBounds({
  nodes,
  padding,
}: {
  nodes: Array<{
    width?: number | null;
    height?: number | null;
    position: { x: number; y: number };
  }>;
  padding: number;
}): RF.Rect {
  let maxX = 0,
    maxY = 0,
    minX = Infinity,
    minY = Infinity;

  for (let i = 0; i < nodes.length; i++) {
    const node = nodes[i];
    maxX = Math.max(maxX, node.position.x + (node.width ?? 0));
    minX = Math.min(minX, node.position.x);
    maxY = Math.max(maxY, node.position.y + (node.height ?? 0));
    minY = Math.min(minY, node.position.y);
  }

  return {
    x: minX - padding,
    y: minY - padding,
    width: maxX - minX + 2 * padding,
    height: maxY - minY + 2 * padding,
  };
}

// Fetch node types
export function getNodeTypeFromSwfObject(swfObject: NodeSwfObjects) {
  if (!swfObject) {
    return NODE_TYPES.unknown;
  }

  const type = switchExpression(swfObject.type, {
    sleep: NODE_TYPES.sleepState,
    event: NODE_TYPES.eventState,
    operation: NODE_TYPES.operationState,
    parallel: NODE_TYPES.parallelState,
    switch: NODE_TYPES.switchState,
    inject: NODE_TYPES.injectState,
    foreach: NODE_TYPES.foreachState,
    default: undefined,
  });

  return type;
}
