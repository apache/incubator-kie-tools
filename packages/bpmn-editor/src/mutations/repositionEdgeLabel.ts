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

import { BPMN20__tDefinitions } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { DC__Bounds, DC__Point } from "@kie-tools/xyflow-react-kie-diagram/dist/maths/model";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { Normalized } from "../normalization/normalize";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";

/**
 * Pins the label of a BPMNEdge to the given bounds. Labels without bounds are positioned automatically.
 */
export function repositionEdgeLabel({
  definitions,
  __readonly_edgeIndex,
  __readonly_bounds,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  __readonly_edgeIndex: number;
  __readonly_bounds: DC__Bounds;
}) {
  const { diagramElements } = addOrGetProcessAndDiagramElements({ definitions });

  const diagramElement = diagramElements[__readonly_edgeIndex];
  if (diagramElement?.__$$element !== "bpmndi:BPMNEdge") {
    throw new Error("BPMN MUTATION: Can't reposition the label of an element that is not a BPMNEdge.");
  }

  if (!isFiniteBounds(__readonly_bounds)) {
    throw new Error("BPMN MUTATION: Can't reposition the label of a BPMNEdge to invalid bounds.");
  }

  diagramElement["bpmndi:BPMNLabel"] ??= { "@_id": generateUuid() };
  diagramElement["bpmndi:BPMNLabel"]["dc:Bounds"] = {
    "@_x": __readonly_bounds["@_x"],
    "@_y": __readonly_bounds["@_y"],
    "@_width": __readonly_bounds["@_width"],
    "@_height": __readonly_bounds["@_height"],
  };
}

/**
 * Runs `updateWaypoints` and then moves the edge's label (if it has explicit bounds) so that it stays at the same
 * place relative to the edge path, like the label was attached to it.
 *
 * `referenceEdge` is the edge as it was before the ongoing gesture. Calculating from it doesn't accumulate errors during
 * a drag. It's ignored when it doesn't match the current label anymore, as the label or the edge changed since then.
 */
export function updateEdgeWaypointsKeepingLabelAttached(
  edge: EdgeWithLabel,
  updateWaypoints: () => void,
  referenceEdge?: EdgeWithLabel
) {
  const labelBounds = edge["bpmndi:BPMNLabel"]?.["dc:Bounds"];
  if (!labelBounds) {
    updateWaypoints();
    return;
  }

  const source = referenceEdge && isReferenceOf(referenceEdge, edge) ? referenceEdge : edge;
  const previousBounds = { ...source["bpmndi:BPMNLabel"]!["dc:Bounds"]! };
  const previousWaypoints = copyWaypoints(source["di:waypoint"]);

  updateWaypoints();

  const newBounds = getEdgeLabelBoundsAfterWaypointsChange({
    __readonly_labelBounds: previousBounds,
    __readonly_previousWaypoints: previousWaypoints,
    __readonly_newWaypoints: edge["di:waypoint"],
  });

  if (newBounds["@_x"] !== labelBounds["@_x"] || newBounds["@_y"] !== labelBounds["@_y"]) {
    labelBounds["@_x"] = newBounds["@_x"];
    labelBounds["@_y"] = newBounds["@_y"];
  }
}

function isReferenceOf(referenceEdge: EdgeWithLabel, edge: EdgeWithLabel) {
  const referenceBounds = referenceEdge["bpmndi:BPMNLabel"]?.["dc:Bounds"];
  const labelBounds = edge["bpmndi:BPMNLabel"]?.["dc:Bounds"];
  if (
    !referenceBounds ||
    !labelBounds ||
    referenceBounds["@_width"] !== labelBounds["@_width"] ||
    referenceBounds["@_height"] !== labelBounds["@_height"]
  ) {
    return false;
  }

  // Reference must reproduce the label.
  const expectedBounds = getEdgeLabelBoundsAfterWaypointsChange({
    __readonly_labelBounds: referenceBounds,
    __readonly_previousWaypoints: referenceEdge["di:waypoint"],
    __readonly_newWaypoints: edge["di:waypoint"],
  });

  return (
    Math.abs(expectedBounds["@_x"] - labelBounds["@_x"]) < REFERENCE_TOLERANCE &&
    Math.abs(expectedBounds["@_y"] - labelBounds["@_y"]) < REFERENCE_TOLERANCE
  );
}

/**
 * Calculates where a label should go after the waypoints of its edge changed.
 *
 * The label center is projected on the previous path. The same spot is then found on the new path (same segment and
 * relative position when the number of waypoints didn't change, same relative length otherwise), and the offset between
 * the label center and the path is rotated by how much that segment rotated. Invalid inputs keep the label where it is.
 */
export function getEdgeLabelBoundsAfterWaypointsChange({
  __readonly_labelBounds,
  __readonly_previousWaypoints,
  __readonly_newWaypoints,
}: {
  __readonly_labelBounds: DC__Bounds;
  __readonly_previousWaypoints: DC__Point[] | undefined;
  __readonly_newWaypoints: DC__Point[] | undefined;
}): DC__Bounds {
  const bounds = { ...__readonly_labelBounds };

  const previousPath = toPath(__readonly_previousWaypoints);
  const newPath = toPath(__readonly_newWaypoints);
  const center = getEdgeLabelCenter(bounds);
  if (!previousPath || !newPath || !center) {
    return bounds;
  }

  const previousAnchor = getClosestPointOnPath(previousPath, center);
  const newAnchor =
    previousPath.length === newPath.length
      ? getPointOnSegment(newPath, previousAnchor.segmentIndex, previousAnchor.segmentFraction)
      : getPointAtLength(newPath, getPathLength(newPath) * previousAnchor.lengthFraction);

  const previousAngle = getSegmentAngle(previousPath, previousAnchor.segmentIndex);
  const newAngle = getSegmentAngle(newPath, newAnchor.segmentIndex);
  const rotation = previousAngle === undefined || newAngle === undefined ? 0 : newAngle - previousAngle;
  const offset = rotate({ x: center.x - previousAnchor.x, y: center.y - previousAnchor.y }, rotation);

  const newX = newAnchor.x + offset.x - (center.x - bounds["@_x"]);
  const newY = newAnchor.y + offset.y - (center.y - bounds["@_y"]);
  if (!Number.isFinite(newX) || !Number.isFinite(newY)) {
    return bounds;
  }

  bounds["@_x"] = newX;
  bounds["@_y"] = newY;
  return bounds;
}

/**
 * Center of the label bounds, or `undefined` when the label has no usable position. Missing sizes count as zero.
 */
export function getEdgeLabelCenter(bounds: Partial<DC__Bounds> | undefined): Point | undefined {
  if (!bounds || !Number.isFinite(bounds["@_x"]) || !Number.isFinite(bounds["@_y"])) {
    return undefined;
  }

  return {
    x: bounds["@_x"]! + (Number.isFinite(bounds["@_width"]) ? bounds["@_width"]! : 0) / 2,
    y: bounds["@_y"]! + (Number.isFinite(bounds["@_height"]) ? bounds["@_height"]! : 0) / 2,
  };
}

type Point = { x: number; y: number };
type PointOnPath = Point & { segmentIndex: number; segmentFraction: number; lengthFraction: number };
type EdgeWithLabel = { "di:waypoint"?: DC__Point[]; "bpmndi:BPMNLabel"?: { "dc:Bounds"?: DC__Bounds } };

const REFERENCE_TOLERANCE = 0.01;

function isFiniteBounds(bounds: DC__Bounds) {
  return (
    Number.isFinite(bounds["@_x"]) &&
    Number.isFinite(bounds["@_y"]) &&
    Number.isFinite(bounds["@_width"]) &&
    Number.isFinite(bounds["@_height"])
  );
}

function copyWaypoints(waypoints: DC__Point[] | undefined): DC__Point[] | undefined {
  return waypoints?.map((w) => ({ "@_x": w["@_x"], "@_y": w["@_y"] }));
}

function toPath(waypoints: DC__Point[] | undefined): Point[] | undefined {
  if (!waypoints || waypoints.length < 2) {
    return undefined;
  }

  const path = waypoints.map((w) => ({ x: w?.["@_x"], y: w?.["@_y"] }));
  return path.every((p) => Number.isFinite(p.x) && Number.isFinite(p.y)) ? path : undefined;
}

function getSegmentLength(path: Point[], segmentIndex: number) {
  return Math.hypot(path[segmentIndex + 1].x - path[segmentIndex].x, path[segmentIndex + 1].y - path[segmentIndex].y);
}

function getPathLength(path: Point[]) {
  let length = 0;
  for (let i = 0; i < path.length - 1; i++) {
    length += getSegmentLength(path, i);
  }
  return length;
}

function getSegmentAngle(path: Point[], segmentIndex: number) {
  const a = path[segmentIndex];
  const b = path[segmentIndex + 1];
  return a.x === b.x && a.y === b.y ? undefined : Math.atan2(b.y - a.y, b.x - a.x);
}

function getClosestPointOnPath(path: Point[], point: Point): PointOnPath {
  const totalLength = getPathLength(path);

  let closest: PointOnPath = { ...path[0], segmentIndex: 0, segmentFraction: 0, lengthFraction: 0 };
  let closestDistance = Infinity;
  let lengthBeforeSegment = 0;

  for (let i = 0; i < path.length - 1; i++) {
    const a = path[i];
    const b = path[i + 1];
    const dx = b.x - a.x;
    const dy = b.y - a.y;
    const squaredLength = dx * dx + dy * dy;
    const segmentFraction =
      squaredLength === 0 ? 0 : clamp(((point.x - a.x) * dx + (point.y - a.y) * dy) / squaredLength, 0, 1);

    const x = a.x + segmentFraction * dx;
    const y = a.y + segmentFraction * dy;
    const distance = (point.x - x) * (point.x - x) + (point.y - y) * (point.y - y);

    if (distance < closestDistance) {
      closestDistance = distance;
      closest = {
        x,
        y,
        segmentIndex: i,
        segmentFraction,
        lengthFraction:
          totalLength === 0 ? 0 : (lengthBeforeSegment + segmentFraction * Math.sqrt(squaredLength)) / totalLength,
      };
    }

    lengthBeforeSegment += Math.sqrt(squaredLength);
  }

  return closest;
}

function getPointOnSegment(path: Point[], segmentIndex: number, segmentFraction: number): PointOnPath {
  const a = path[segmentIndex];
  const b = path[segmentIndex + 1];
  return {
    x: a.x + segmentFraction * (b.x - a.x),
    y: a.y + segmentFraction * (b.y - a.y),
    segmentIndex,
    segmentFraction,
    lengthFraction: 0, // Not needed.
  };
}

function getPointAtLength(path: Point[], length: number): PointOnPath {
  let remainingLength = Math.max(0, length);
  for (let i = 0; i < path.length - 1; i++) {
    const segmentLength = getSegmentLength(path, i);
    if (remainingLength <= segmentLength || i === path.length - 2) {
      return getPointOnSegment(path, i, segmentLength === 0 ? 0 : clamp(remainingLength / segmentLength, 0, 1));
    }
    remainingLength -= segmentLength;
  }
  return getPointOnSegment(path, 0, 0);
}

function rotate(vector: Point, angle: number): Point {
  const cos = Math.cos(angle);
  const sin = Math.sin(angle);
  return { x: vector.x * cos - vector.y * sin, y: vector.x * sin + vector.y * cos };
}

function clamp(value: number, min: number, max: number) {
  return Math.min(max, Math.max(min, value));
}
