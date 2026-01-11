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

import * as React from "react";
import * as RF from "reactflow";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { getDefaultEdgeTypeBetween, GraphStructure } from "../graph/graphStructure";
import { usePathForEdgeWithWaypoints } from "./usePathForEdgeWithWaypoints";
import { PositionalNodeHandleId } from "../nodes/PositionalNodeHandles";
import { getBoundsCenterPoint, getPositionalHandlePosition } from "../maths/Maths";
import { pointsToPath } from "../maths/DcMaths";
import { useXyFlowReactKieDiagramStore } from "../store/Store";
import { snapPoint } from "../snapgrid/SnapGrid";
import { NodeSizes } from "../nodes/NodeSizes";

export type NodeComponent = React.ComponentType<{ x: number; y: number; width: number; height: number }>;
export type ConnectionLineNodeMapping<N extends string> = Record<N, NodeComponent>;

export type EdgeComponent = React.ComponentType<{ d: string }>;
export type ConnectionLineEdgeMapping<E extends string> = Record<E, EdgeComponent>;

export function ConnectionLine<N extends string, E extends string>({
  toX,
  toY,
  fromNode,
  fromHandle,
  defaultNodeSizes,
  minNodeSizes,
  graphStructure,
  nodeComponentsMapping,
  edgeComponentsMapping,
}: RF.ConnectionLineComponentProps & {
  defaultNodeSizes: NodeSizes<N>;
  minNodeSizes: NodeSizes<N>;
  graphStructure: GraphStructure<N, E>;
  nodeComponentsMapping: ConnectionLineNodeMapping<N>;
  edgeComponentsMapping: ConnectionLineEdgeMapping<E>;
}) {
  const snapGrid = useXyFlowReactKieDiagramStore((s) => s.xyFlowReactKieDiagram.snapGrid);

  const edgeBeingUpdated = useXyFlowReactKieDiagramStore((s) =>
    s.xyFlowReactKieDiagram.edgeIdBeingUpdated
      ? s.computed(s).getDiagramData().edgesById.get(s.xyFlowReactKieDiagram.edgeIdBeingUpdated)
      : undefined
  );

  // This works because nodes are configured with:
  // - Source handles with ids matching EDGE_TYPES or NODE_TYPES
  // - Target handles with ids matching TargetHandleId
  //
  // When editing an existing edge from its first waypoint (i.e., source handle) the edge is rendered
  // in reverse. So the connection line's "from" properties are actually "to" properties.
  const isUpdatingFromSourceHandle = Object.keys(PositionalNodeHandleId).some(
    (k) => (PositionalNodeHandleId as any)[k] === fromHandle?.id
  );

  const handleId = isUpdatingFromSourceHandle ? edgeBeingUpdated?.type : edgeBeingUpdated?.type ?? fromHandle?.id;

  const kieEdgePath = usePathForEdgeWithWaypoints(
    edgeBeingUpdated?.data,
    edgeBeingUpdated?.data?.shapeSource,
    edgeBeingUpdated?.data?.shapeTarget,
    minNodeSizes[fromNode?.type as N],
    minNodeSizes[handleId as N]
  );

  const { "@_x": toXsnapped, "@_y": toYsnapped } = snapPoint(snapGrid, { "@_x": toX, "@_y": toY });

  const { "@_x": fromX, "@_y": fromY } = getBoundsCenterPoint({
    x: fromNode?.positionAbsolute?.x,
    y: fromNode?.positionAbsolute?.y,
    width: fromNode?.width,
    height: fromNode?.height,
  });

  const connectionLinePath =
    edgeBeingUpdated && kieEdgePath.points
      ? isUpdatingFromSourceHandle
        ? pointsToPath([{ "@_x": toX, "@_y": toY }, ...kieEdgePath.points.slice(1)]) // First point is being dragged
        : pointsToPath([...kieEdgePath.points.slice(0, -1), { "@_x": toX, "@_y": toY }]) // Last point is being dragged
      : `M${fromX},${fromY} L${toX},${toY}`;

  // Edges
  const EdgeConnectionLine = edgeComponentsMapping[handleId as E] as EdgeComponent;
  if (EdgeConnectionLine !== undefined) {
    return <EdgeConnectionLine d={connectionLinePath} />;
  }

  // Nodes
  else {
    const nodeType = handleId as N;

    const defaultSize = defaultNodeSizes[nodeType]({ snapGrid });
    const [toXauto, toYauto] = getPositionalHandlePosition(
      { x: toXsnapped, y: toYsnapped, width: defaultSize["@_width"], height: defaultSize["@_height"] },
      { ...fromNode!.position, width: fromNode!.width, height: fromNode!.height },
      undefined
    );

    const [fromXauto, fromYauto] = getPositionalHandlePosition(
      { ...fromNode!.position, width: fromNode!.width, height: fromNode!.height },
      { x: toXsnapped, y: toYsnapped, width: defaultSize["@_width"], height: defaultSize["@_height"] },
      undefined
    );

    const edgeType = getDefaultEdgeTypeBetween(graphStructure, fromNode?.type as N, handleId as N);
    if (!edgeType) {
      throw new Error(`Invalid structure: ${fromNode?.type} --(any)--> ${handleId}`);
    }

    const path = `M${fromXauto},${fromYauto} L${toXauto},${toYauto}`;

    const EdgeConnectionLine = edgeComponentsMapping[edgeType] as EdgeComponent;
    if (EdgeConnectionLine === undefined) {
      throw new Error("Nonexisting mapping for edge of type " + edgeType);
    }

    const NodeConnectionLine = nodeComponentsMapping[nodeType] as NodeComponent;
    if (NodeConnectionLine === undefined) {
      throw new Error("Nonexisting mapping for node of type " + nodeType);
    }

    return (
      <g>
        <EdgeConnectionLine d={path} />
        <NodeConnectionLine
          x={toXsnapped}
          y={toYsnapped}
          width={defaultSize["@_width"]}
          height={defaultSize["@_height"]}
        />
      </g>
    );
  }
}
