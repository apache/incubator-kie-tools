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
  DC__Bounds,
  DC__Dimension,
  DC__Point,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { SnapGrid } from "../../store/Store";
import { snapBoundsDimensions, snapBoundsPosition } from "../SnapGrid";
import { PositionalNodeHandleId } from "../connections/PositionalNodeHandles";
import { AutoPositionedEdgeMarker } from "../edges/AutoPositionedEdgeMarker";
import { NODE_TYPES } from "../nodes/NodeTypes";
import { NodeDmnObjects } from "../nodes/Nodes";
import { getCenter } from "./Maths";

export const DEFAULT_INTRACTION_WIDTH = 40;
export const CONTAINER_NODES_DESIRABLE_PADDING = 60;

export function getDistance(a: DC__Point, b: DC__Point) {
  return Math.sqrt(Math.pow(a["@_x"] - b["@_x"], 2) + Math.pow(a["@_y"] - b["@_y"], 2));
}

export function getDmnBoundsCenterPoint(bounds: DC__Bounds): DC__Point {
  const { x, y } = getCenter(bounds["@_x"], bounds["@_y"], bounds["@_width"], bounds["@_height"]);
  return { "@_x": x, "@_y": y };
}

export function getPointForHandle({
  handle,
  bounds,
}: {
  bounds: DC__Bounds;
  handle: PositionalNodeHandleId;
}): DC__Point {
  if (handle === PositionalNodeHandleId.Center) {
    return getDmnBoundsCenterPoint(bounds);
  } else if (handle === PositionalNodeHandleId.Top) {
    return { "@_x": bounds["@_x"] + bounds["@_width"] / 2, "@_y": bounds["@_y"] };
  } else if (handle === PositionalNodeHandleId.Right) {
    return { "@_x": bounds["@_x"] + bounds["@_width"], "@_y": bounds["@_y"] + bounds["@_height"] / 2 };
  } else if (handle === PositionalNodeHandleId.Bottom) {
    return { "@_x": bounds["@_x"] + bounds["@_width"] / 2, "@_y": bounds["@_y"] + bounds["@_height"] };
  } else if (handle === PositionalNodeHandleId.Left) {
    return { "@_x": bounds["@_x"], "@_y": bounds["@_y"] + bounds["@_height"] / 2 };
  } else {
    throw new Error(`Invalid target handle id '${handle}'.`);
  }
}

export function getHandlePosition({
  shapeBounds,
  waypoint,
}: {
  shapeBounds: DC__Bounds | undefined;
  waypoint: DC__Point;
}): { handlePosition: PositionalNodeHandleId; point: DC__Point } {
  const x = shapeBounds?.["@_x"] ?? 0;
  const y = shapeBounds?.["@_y"] ?? 0;
  const w = shapeBounds?.["@_width"] ?? 0;
  const h = shapeBounds?.["@_height"] ?? 0;

  const center = { "@_x": x + w / 2, "@_y": y + h / 2 };
  const left = { "@_x": x, "@_y": y + h / 2 };
  const right = { "@_x": x + w, "@_y": y + h / 2 };
  const top = { "@_x": x + w / 2, "@_y": y };
  const bottom = { "@_x": x + w / 2, "@_y": y + h };

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
    console.warn("DMN DIAGRAM: Can't find a match of NSWE/Center handles. Using Center as default.");
    return { handlePosition: PositionalNodeHandleId.Center, point: center };
  }
}

export function getLineRectangleIntersectionPoint(
  point1: DC__Point,
  point2: DC__Point,
  rectangle: {
    x: number;
    y: number;
    width: number;
    height: number;
  }
): DC__Point {
  const [x1, y1] = [point1["@_x"], point1["@_y"]];
  const [x2, y2] = [point2["@_x"], point2["@_y"]];
  const [rx, ry] = [rectangle.x, rectangle.y];
  const [rw, rh] = [rectangle.width, rectangle.height];

  // Calculate the line equation: y = mx + b
  const m = (y2 - y1) / (x2 - x1);
  const b = y1 - m * x1;

  if (m === Infinity || m === -Infinity) {
    // Vertical line
    const x = point1["@_x"];
    const minY = Math.min(point1["@_y"], point2["@_y"]);
    const maxY = Math.max(point1["@_y"], point2["@_y"]);

    if (x >= rectangle.x && x <= rectangle.x + rectangle.width) {
      if (minY <= rectangle.y) {
        return { "@_x": x, "@_y": rectangle.y };
      } else if (maxY >= rectangle.y + rectangle.height) {
        return { "@_x": x, "@_y": rectangle.y + rectangle.height };
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
      "@_x": closestIntersection.x,
      "@_y": closestIntersection.y,
    }) ||
    point2
  );
}

const minDistance = (point: { x: number; y: number }, x1: number, y1: number) =>
  Math.pow(point.x - x1, 2) + Math.pow(point.y - y1, 2);

export function getContainmentRelationship({
  bounds,
  container,
  divingLineLocalY,
  snapGrid,
  isAlternativeInputDataShape,
  containerMinSizes,
  boundsMinSizes,
}: {
  bounds: DC__Bounds;
  container: DC__Bounds;
  divingLineLocalY?: number;
  snapGrid: SnapGrid;
  isAlternativeInputDataShape: boolean;
  containerMinSizes: (args: { snapGrid: SnapGrid; isAlternativeInputDataShape: boolean }) => DC__Dimension;
  boundsMinSizes: (args: { snapGrid: SnapGrid; isAlternativeInputDataShape: boolean }) => DC__Dimension;
}): { isInside: true; section: "upper" | "lower" } | { isInside: false } {
  const { x: cx, y: cy } = snapBoundsPosition(snapGrid, container);
  const { width: cw, height: ch } = snapBoundsDimensions(
    snapGrid,
    container,
    containerMinSizes({ snapGrid, isAlternativeInputDataShape })
  );
  const { x: bx, y: by } = snapBoundsPosition(snapGrid, bounds);
  const { width: bw, height: bh } = snapBoundsDimensions(
    snapGrid,
    bounds,
    boundsMinSizes({ snapGrid, isAlternativeInputDataShape })
  );

  const center = getDmnBoundsCenterPoint({
    "@_height": bh,
    "@_width": bw,
    "@_x": bx,
    "@_y": by,
  });

  const isInside =
    bx >= cx && // force-line-break
    by >= cy && // force-line-break
    bx + bw <= cx + cw && // force-line-break
    by + bh <= cy + ch;

  if (isInside) {
    return { isInside: true, section: center["@_y"] > cy + (divingLineLocalY ?? 0) ? "lower" : "upper" };
  } else {
    return { isInside: false };
  }
}

export function pointsToPath(points: DC__Point[]): string {
  const start = points[0];
  let path = `M ${start["@_x"]},${start["@_y"]}`;
  for (let i = 1; i < points.length - 1; i++) {
    const p = points[i];
    path += ` L ${p["@_x"]},${p["@_y"]} M ${p["@_x"]},${p["@_y"]}`;
  }
  const end = points[points.length - 1];
  path += ` L ${end["@_x"]},${end["@_y"]}`;

  return path;
}

export function getDecisionServiceDividerLineLocalY(shape: DMNDI15__DMNShape) {
  return (
    (shape["dmndi:DMNDecisionServiceDividerLine"]?.["di:waypoint"]?.[0]["@_y"] ?? 0) -
    (shape["dc:Bounds"]?.["@_y"] ?? 0)
  );
}

export const DISCRETE_AUTO_POSITIONING_DMN_EDGE_ID_MARKER = [
  AutoPositionedEdgeMarker.BOTH, // This needs to be the first element.
  AutoPositionedEdgeMarker.SOURCE,
  AutoPositionedEdgeMarker.TARGET,
];
export function getDiscreteAutoPositioningEdgeIdMarker(edgeId: string): AutoPositionedEdgeMarker | undefined {
  for (const marker of DISCRETE_AUTO_POSITIONING_DMN_EDGE_ID_MARKER) {
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
}): DC__Bounds {
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
    "@_x": minX - padding,
    "@_y": minY - padding,
    "@_width": maxX - minX + 2 * padding,
    "@_height": maxY - minY + 2 * padding,
  };
}

export function getNodeTypeFromDmnObject(dmnObject: NodeDmnObjects) {
  if (!dmnObject) {
    return NODE_TYPES.unknown;
  }

  const type = switchExpression(dmnObject.__$$element, {
    inputData: NODE_TYPES.inputData,
    decision: NODE_TYPES.decision,
    businessKnowledgeModel: NODE_TYPES.bkm,
    knowledgeSource: NODE_TYPES.knowledgeSource,
    decisionService: NODE_TYPES.decisionService,
    group: NODE_TYPES.group,
    textAnnotation: NODE_TYPES.textAnnotation,
    default: undefined,
  });

  return type;
}
