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
import { useLayoutEffect, useMemo } from "react";
import * as RF from "reactflow";
import { ContainmentMode } from "../graph/graphStructure";
import { DC__Shape } from "../maths/model";
import { snapShapeDimensions } from "../snapgrid/SnapGrid";
import { useXyFlowReactKieDiagramStore } from "../store/Store";
import { NodeSizes } from "./NodeSizes";

// CSS always last
import "./Hooks.css";

export const NODE_LAYERS = {
  GROUP_NODES: 0,
  CONTAINER_NODES: 1000,
  NODES: 2000, // We need a difference > 1000 here, since ReactFlow will add 1000 to the z-index when a node is selected.
  PARENT_NODES: 3000, // We need a difference > 1000 here, since ReactFlow will add 1000 to the z-index when a node is selected.
  NESTED_NODES: 5000,
  ATTACHED_NODES: 10000,
};

export const resizerControlStyle = {
  background: "transparent",
  border: "none",
};

export type NodeResizeHandleProps<N extends string> = {
  nodeId: string;
  nodeShapeIndex: number;
  nodeType: N;
  MIN_NODE_SIZES: NodeSizes<N>;
};

export function NodeResizerHandle<N extends string>(props: NodeResizeHandleProps<N>) {
  const snapGrid = useXyFlowReactKieDiagramStore((s) => s.xyFlowReactKieDiagram.snapGrid);

  const minSize = props.MIN_NODE_SIZES[props.nodeType]({ snapGrid });
  return (
    <RF.NodeResizeControl style={resizerControlStyle} minWidth={minSize["@_width"]} minHeight={minSize["@_height"]}>
      <div
        data-testid={`kie-tools--xyflow-react-kie-diagram--${props.nodeId}-resize-handle`}
        style={{
          position: "absolute",
          top: "-10px",
          left: "-10px",
          width: "12px",
          height: "12px",
          backgroundColor: "black",
          clipPath: "polygon(0 100%, 100% 100%, 100% 0)",
        }}
      />
    </RF.NodeResizeControl>
  );
}

export function useNodeResizing(id: string): boolean {
  return RF.useStore((s) => s.nodeInternals.get(id)?.resizing ?? false);
}

export type NodeDimensionsArgs<N extends string> = {
  shape: DC__Shape;
  nodeType: N;
  MIN_NODE_SIZES: NodeSizes<N>;
};

export function useNodeDimensions<N extends string>(args: NodeDimensionsArgs<N>): RF.Dimensions {
  const snapGrid = useXyFlowReactKieDiagramStore((s) => s.xyFlowReactKieDiagram.snapGrid);

  const { nodeType, shape } = args;

  return useMemo(() => {
    const minSizes = args.MIN_NODE_SIZES[nodeType]({
      snapGrid,
    });

    return {
      width: snapShapeDimensions(snapGrid, shape, minSizes).width,
      height: snapShapeDimensions(snapGrid, shape, minSizes).height,
    };
  }, [args.MIN_NODE_SIZES, nodeType, snapGrid, shape]);
}

export function useHoveredNodeAlwaysOnTop(
  ref: React.RefObject<HTMLDivElement | SVGElement>,
  zIndex: number,
  shouldActLikeHovered: boolean,
  dragging: boolean,
  selected: boolean,
  isEditing: boolean
) {
  useLayoutEffect(() => {
    const r = ref.current;

    if (selected && !isEditing) {
      r?.focus();
    }

    if (r) {
      r.parentElement!.style.zIndex = `${
        shouldActLikeHovered || dragging ? zIndex + NODE_LAYERS.NESTED_NODES + 1 : zIndex
      }`;
    }
  }, [dragging, shouldActLikeHovered, ref, zIndex, selected, isEditing]);
}

export function useConnection(nodeId: string) {
  const connectionNodeId = RF.useStore((s) => s.connectionNodeId);
  const connectionHandleType = RF.useStore((s) => s.connectionHandleType);

  const source = connectionNodeId;
  const target = nodeId;

  const edgeIdBeingUpdated = useXyFlowReactKieDiagramStore((s) => s.xyFlowReactKieDiagram.edgeIdBeingUpdated);
  const sourceHandle = RF.useStore(
    (s) => s.connectionHandleId ?? s.edges.find((e) => e.id === edgeIdBeingUpdated)?.type ?? null
  );

  const connection = useMemo(
    () => ({
      source: connectionHandleType === "source" ? source : target,
      target: connectionHandleType === "source" ? target : source,
      sourceHandle,
      targetHandle: null, // We don't use targetHandles, as target handles are only different in position, not in semantic.
    }),
    [connectionHandleType, source, sourceHandle, target]
  );

  return connection;
}

export function useConnectionTargetStatus(nodeId: string, shouldActLikeHovered: boolean) {
  const isTargeted = RF.useStore((s) => !!s.connectionNodeId && s.connectionNodeId !== nodeId && shouldActLikeHovered);
  const connection = useConnection(nodeId);
  const isValidConnectionTarget = RF.useStore((s) => s.isValidConnection?.(connection) ?? false);

  return useMemo(
    () => ({
      isTargeted,
      isValidConnectionTarget,
    }),
    [isTargeted, isValidConnectionTarget]
  );
}

export function useNodeClassName<N extends string, E extends string>(
  isValidConnectionTarget: boolean,
  nodeId: string,
  NODE_TYPES: Record<string, N>,
  EDGE_TYPES: Record<string, E>,
  ignoreContainment: boolean = false
) {
  const isDropTarget = useXyFlowReactKieDiagramStore((s) => s.xyFlowReactKieDiagram.dropTarget?.node.id === nodeId);
  const dropTargetContainmentMode = useXyFlowReactKieDiagramStore(
    (s) => s.xyFlowReactKieDiagram.dropTarget?.containmentMode
  );

  const isConnectionNodeId = RF.useStore((s) => s.connectionNodeId === nodeId);
  const connection = useConnection(nodeId);
  const isEdgeConnection = !!Object.values(EDGE_TYPES).find((s) => s === connection.sourceHandle);
  const isNodeConnection = !!Object.values(NODE_TYPES).find((s) => s === connection.sourceHandle);

  if (isNodeConnection && !isConnectionNodeId) {
    return "dimmed";
  }

  if (isEdgeConnection && (!isValidConnectionTarget || isConnectionNodeId)) {
    return "dimmed";
  }

  if (!ignoreContainment && isDropTarget && dropTargetContainmentMode !== undefined) {
    if (dropTargetContainmentMode === ContainmentMode.IGNORE) {
      return "drop-target-ignore";
    } else if (dropTargetContainmentMode === ContainmentMode.INSIDE) {
      return "drop-target-inside";
    } else if (dropTargetContainmentMode === ContainmentMode.BORDER) {
      return "drop-target-border";
    } else if (dropTargetContainmentMode === ContainmentMode.INVALID_BORDER) {
      return "drop-target-border-invalid";
    } else if (dropTargetContainmentMode === ContainmentMode.INVALID_INSIDE) {
      return "drop-target-inside-invalid";
    } else if (dropTargetContainmentMode === ContainmentMode.INVALID_NON_INSIDE_CONTAINER) {
      return "drop-target-invalid-non-container";
    } else if (dropTargetContainmentMode === ContainmentMode.INVALID_IGNORE) {
      return "drop-target-invalid-ignore";
    } else {
      throw new Error("Unknown containment mode");
    }
  }

  return "normal";
}
