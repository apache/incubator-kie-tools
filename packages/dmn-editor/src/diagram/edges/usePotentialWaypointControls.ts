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
import { addEdgeWaypoint } from "../../mutations/addEdgeWaypoint";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../../store/StoreContext";
import { snapPoint } from "../SnapGrid";
import { DC__Point } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { DmnDiagramNodeData } from "../nodes/Nodes";
import { DmnDiagramEdgeData } from "./Edges";
import { useExternalModels } from "../../includedModels/DmnEditorDependenciesContext";
import { addEdge } from "../../mutations/addEdge";
import { EdgeType, NodeType } from "../connections/graphStructure";
import { PositionalNodeHandleId } from "../connections/PositionalNodeHandles";
import { getHandlePosition } from "../maths/DmnMaths";

export function usePotentialWaypointControls(
  waypoints: DC__Point[],
  isEdgeSelected: boolean | undefined,
  edgeId: string,
  edgeIndex: number | undefined,
  interactionPathRef: React.RefObject<SVGPathElement>
) {
  const snapGrid = useDmnEditorStore((s) => s.diagram.snapGrid);
  const drdIndex = useDmnEditorStore((s) => s.computed(s).getDrdIndex());
  const isDraggingWaypoint = useDmnEditorStore((s) => !!s.diagram.draggingWaypoints.find((e) => e === edgeId));
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const reactFlowInstance = RF.useReactFlow<DmnDiagramNodeData, DmnDiagramEdgeData>();
  const { externalModelsByNamespace } = useExternalModels();

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
    if (!potentialWaypoint || !snappedPotentialWaypoint) {
      return;
    }

    if (edgeIndex === undefined) {
      /**
       * This means we are adding a first waypoint to one of following edges:
       * - an edge in a non default DRD
       * - an edge targeting an external node
       */
      dmnEditorStoreApi.setState((state) => {
        const nodesById = state.computed(state).getDiagramData(externalModelsByNamespace).nodesById;
        const edge = state.computed(state).getDiagramData(externalModelsByNamespace).edgesById.get(edgeId);
        if (edge === undefined || edge.data?.dmnShapeSource === undefined || edge.data?.dmnShapeTarget == undefined) {
          console.debug(
            `DMN MUTATION: We can not add DMNEdge for '${edgeId}' edge into diagram. There are missing data edge: ${edge}, edge.data: ${edge?.data}`
          );
          return;
        }
        const edgeSourceBounds = edge.data?.dmnShapeSource["dc:Bounds"];
        const edgeTargetBounds = edge.data?.dmnShapeTarget["dc:Bounds"];

        if (edgeSourceBounds === undefined || edgeTargetBounds === undefined) {
          console.debug(
            `DMN MUTATION: We can not add DMNEdge for '${edgeId}' edge into diagram. There are missing data edgeSourceBounds: ${edgeSourceBounds}, edgeTargetBounds: ${edgeTargetBounds}`
          );
          return;
        }

        const sourceNode = nodesById.get(edge.source);
        const targetNode = nodesById.get(edge.target);

        if (sourceNode === undefined || targetNode === undefined) {
          console.debug(
            `DMN MUTATION: We can not add DMNEdge for '${edgeId}' edge into diagram. There are missing data sourceNode: ${sourceNode}, targetNode: ${targetNode}`
          );
          return;
        }

        addEdge({
          definitions: state.dmn.model.definitions,
          drdIndex: state.computed(state).getDrdIndex(),
          edge: {
            type: edge.type as EdgeType,
            targetHandle: getHandlePosition({ shapeBounds: edgeTargetBounds, waypoint: snappedPotentialWaypoint })
              .handlePosition as PositionalNodeHandleId,
            sourceHandle: getHandlePosition({ shapeBounds: edgeSourceBounds, waypoint: snappedPotentialWaypoint })
              .handlePosition as PositionalNodeHandleId,
            autoPositionedEdgeMarker: undefined,
          },
          sourceNode: {
            type: sourceNode.type as NodeType,
            data: sourceNode.data,
            href: edge.source,
            bounds: edgeSourceBounds,
            shapeId: edge.data?.dmnShapeSource["@_id"],
          },
          targetNode: {
            type: targetNode.type as NodeType,
            href: edge.target,
            data: targetNode.data,
            bounds: edgeTargetBounds,
            index: nodesById.get(edge.target)?.data.index ?? 0,
            shapeId: edge.data?.dmnShapeTarget["@_id"],
          },
          keepWaypoints: false,
          extraArg: {
            requirementEdgeTargetingExternalNodeId: targetNode.data.dmnObjectQName.prefix ? edgeId : undefined,
          },
        });

        console.debug(`DMN MUTATION: DMNEdge for '${edgeId}' edge was added into diagram.`);
      });
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
      const dmnEdgeIndex = state.computed(state).indexedDrd().dmnEdgesByDmnElementRef.get(edgeId)?.index;
      if (dmnEdgeIndex === undefined) {
        console.debug(`DMN MUTATION: DMNEdge for '${edgeId}' edge has missing index.`);
        return;
      }
      addEdgeWaypoint({
        definitions: state.dmn.model.definitions,
        drdIndex,
        beforeIndex: i - 1,
        edgeIndex: dmnEdgeIndex,
        waypoint: snappedPotentialWaypoint,
      });

      console.debug(`DMN MUTATION: Waypoint on the DMNEdge for '${edgeId}' edge was added.`);
    });
  }, [
    drdIndex,
    dmnEditorStoreApi,
    edgeId,
    edgeIndex,
    externalModelsByNamespace,
    isExistingWaypoint,
    potentialWaypoint,
    snappedPotentialWaypoint,
    waypoints,
  ]);

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
