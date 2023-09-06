import * as RF from "reactflow";
import { useState, useCallback, useMemo } from "react";
import { addEdgeWaypoint } from "../../mutations/addEdgeWaypoint";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../../store/Store";
import { useDmnEditorDiagramContainer } from "../DiagramContainerContext";
import { snapPoint } from "../SnapGrid";
import { DC__Point } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export function usePotentialWaypointControls(
  waypoints: DC__Point[],
  isEdgeSelected: boolean | undefined,
  edgeId: string,
  edgeIndex: number | undefined,
  interactionPathRef: React.RefObject<SVGPathElement>
) {
  const diagram = useDmnEditorStore((s) => s.diagram);
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const reactFlowInstance = RF.useReactFlow();

  const [potentialWaypoint, setPotentialWaypoint] =
    useState<ReturnType<typeof approximateClosestPoint> | undefined>(undefined);

  const { container } = useDmnEditorDiagramContainer();

  const isConnecting = !!RF.useStore(useCallback((state) => state.connectionNodeId, []));

  const isExistingWaypoint = useCallback(
    (point: DC__Point) => waypoints.find((w) => w["@_x"] === point["@_x"] && w["@_y"] === point["@_y"]),
    [waypoints]
  );

  const onMouseMove = useCallback(
    (e: React.MouseEvent) => {
      const containerBounds = container.current!.getBoundingClientRect();
      const projectedPoint = reactFlowInstance.project({
        x: e.clientX - containerBounds.left,
        y: e.clientY - containerBounds.top,
      });

      setPotentialWaypoint(approximateClosestPoint(interactionPathRef.current!, [projectedPoint.x, projectedPoint.y]));
    },
    [container, interactionPathRef, reactFlowInstance]
  );

  const snappedPotentialWaypoint = useMemo(() => {
    if (!potentialWaypoint) {
      return undefined;
    }

    return snapPoint(diagram.snapGrid, {
      "@_x": potentialWaypoint.point.x,
      "@_y": potentialWaypoint.point.y,
    });
  }, [diagram.snapGrid, potentialWaypoint]);

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

    dmnEditorStoreApi.setState((state) => {
      addEdgeWaypoint({
        definitions: state.dmn.model.definitions,
        beforeIndex: i - 1,
        edgeIndex,
        waypoint: snappedPotentialWaypoint,
      });
    });
  }, [dmnEditorStoreApi, edgeIndex, isExistingWaypoint, potentialWaypoint, snappedPotentialWaypoint, waypoints]);

  const isDraggingWaypoint = !!diagram.draggingWaypoints.find((e) => e === edgeId);

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

    const afterLength = bestLength + precision;
    const after = pathNode.getPointAtLength(afterLength);
    const afterDistance = distanceComponentsSquared(point, after);
    if (afterLength <= pathLength && afterDistance < bestDistance) {
      best = after;
      bestLength = afterLength;
      bestDistance = afterDistance;
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
