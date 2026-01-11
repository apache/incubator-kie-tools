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
import { useState, useCallback, useMemo } from "react";
import { snapPoint } from "../snapgrid/SnapGrid";
import { DC__Point } from "../maths/model";
import { useXyFlowReactKieDiagramStore } from "../store/Store";
import { useWaypointsActions } from "./WaypointActionsContext";

export function usePotentialWaypointControls(
  waypoints: DC__Point[],
  isEdgeSelected: boolean | undefined,
  edgeId: string,
  edgeIndex: number | undefined,
  interactionPathRef: React.RefObject<SVGPathElement>
) {
  const reactFlowInstance = RF.useReactFlow();

  const { onWaypointAdded } = useWaypointsActions();

  const snapGrid = useXyFlowReactKieDiagramStore((s) => ({
    isEnabled: s.xyFlowReactKieDiagram.snapGrid.isEnabled,
    x: s.xyFlowReactKieDiagram.snapGrid.x / 2,
    y: s.xyFlowReactKieDiagram.snapGrid.y / 2,
  }));

  const isDraggingWaypoint = useXyFlowReactKieDiagramStore(
    (s) => !!s.xyFlowReactKieDiagram.draggingWaypoints.find((e) => e === edgeId)
  );

  const [potentialWaypoint, setPotentialWaypoint] = useState<ReturnType<typeof approximateClosestPoint> | undefined>(
    undefined
  );

  const isConnecting = !!RF.useStore((s) => s.connectionNodeId);

  const isExistingWaypoint = useCallback(
    (point: DC__Point) => waypoints.find((w) => w["@_x"] === point["@_x"] && w["@_y"] === point["@_y"]),
    [waypoints]
  );

  const onMouseMove = useCallback(
    (e: React.MouseEvent) => {
      if (!e.clientX || !e.clientY) {
        return;
      }

      const projectedPoint = reactFlowInstance.screenToFlowPosition({
        x: e.clientX,
        y: e.clientY,
      });

      setPotentialWaypoint(approximateClosestPoint(interactionPathRef.current!, [projectedPoint.x, projectedPoint.y]));
    },
    [interactionPathRef, reactFlowInstance]
  );

  const snappedPotentialWaypoint = useMemo(() => {
    if (!potentialWaypoint) {
      return undefined;
    }

    return snapPoint(snapGrid, {
      "@_x": potentialWaypoint.point.x,
      "@_y": potentialWaypoint.point.y,
    });
  }, [snapGrid, potentialWaypoint]);

  const onDoubleClick = useCallback(() => {
    if (!potentialWaypoint || !snappedPotentialWaypoint || edgeIndex === undefined) {
      return;
    }

    if (isExistingWaypoint(snappedPotentialWaypoint)) {
      console.debug("Preventing overlapping waypoint creation.");
      return;
    }

    // This only works because the lines connecting waypoints are ALWAYS straight lines.
    // This code will stop working properly if the interpolation method changes.
    let i = 1;
    for (let currentLength = 0; currentLength < potentialWaypoint.lengthInPath; i++) {
      currentLength += Math.sqrt(
        distanceComponentsSquared([waypoints[i]["@_x"], waypoints[i]["@_y"]], {
          x: waypoints[i - 1]["@_x"],
          y: waypoints[i - 1]["@_y"],
        })
      );
    }

    console.log("XYFLOW-DIAGRAM: Waypoint added");
    onWaypointAdded({
      beforeIndex: i - 1,
      edgeIndex,
      waypoint: snappedPotentialWaypoint,
    });
  }, [edgeIndex, isExistingWaypoint, onWaypointAdded, potentialWaypoint, snappedPotentialWaypoint, waypoints]);

  const shouldReturnPotentialWaypoint =
    isEdgeSelected &&
    !isDraggingWaypoint &&
    snappedPotentialWaypoint &&
    !isExistingWaypoint(snappedPotentialWaypoint) &&
    !isConnecting;

  return {
    isDraggingWaypoint,
    onMouseMove,
    onDoubleClick,
    potentialWaypoint: !shouldReturnPotentialWaypoint ? undefined : potentialWaypoint,
  };
}

function approximateClosestPoint(
  pathNode: SVGPathElement,
  point: [number, number]
): { point: DOMPoint; lengthInPath: number } {
  const pathLength = pathNode.getTotalLength();
  let precision = Math.floor(pathLength / 10);
  let best: DOMPoint;
  let bestLength = 0;
  let bestDistance = Infinity;

  let scan: DOMPoint;
  let scanDistance: number;
  for (let scanLength = 0; scanLength <= pathLength; scanLength += precision) {
    scan = pathNode.getPointAtLength(scanLength);
    scanDistance = distanceComponentsSquared(point, scan);

    if (scanDistance < bestDistance) {
      best = scan;
      bestLength = scanLength;
      bestDistance = scanDistance;
    }
  }

  precision /= 2;

  while (precision > 1) {
    const bLength = bestLength - precision;
    const b = pathNode.getPointAtLength(bLength);
    const bDistance = distanceComponentsSquared(point, b);
    if (bLength >= 0 && bDistance < bestDistance) {
      best = b;
      bestLength = bLength;
      bestDistance = bDistance;
      continue;
    }

    const aLength = bestLength + precision;
    const a = pathNode.getPointAtLength(aLength);
    const aDistance = distanceComponentsSquared(point, a);
    if (aLength <= pathLength && aDistance < bestDistance) {
      best = a;
      bestLength = aLength;
      bestDistance = aDistance;
      continue;
    }

    precision /= 2;
  }

  return { point: best!, lengthInPath: bestLength };
}

// No need to calculate the sqrt
function distanceComponentsSquared(a: [number, number], b: { x: number; y: number }) {
  const dx = b.x - a[0];
  const dy = b.y - a[1];
  return dx * dx + dy * dy;
}
