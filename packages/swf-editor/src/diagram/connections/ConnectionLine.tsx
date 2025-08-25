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
import { snapPoint } from "../SnapGrid";
import { EDGE_TYPES } from "../edges/SwfEdgeTypes";
import {
  TransitionPath,
  ErrorTransitionPath,
  EventConditionTransitionPath,
  DefaultConditionTransitionPath,
  DataConditionTransitionPath,
  CompensationTransitionPath,
} from "../edges/SwfEdges";
import { NODE_TYPES } from "../nodes/SwfNodeTypes";
import { getPositionalHandlePosition } from "../maths/Maths";
import {
  EventstateSvg,
  OperationstateSvg,
  SwitchstateSvg,
  SleepstateSvg,
  ParallelstateSvg,
  InjectstateSvg,
  ForeachstateSvg,
  CallbackstateSvg,
} from "../nodes/SwfNodeSvgs";
import { pointsToPath } from "../maths/SwfMaths";
import { getBoundsCenterPoint } from "../maths/Maths";
import { NodeType, getDefaultEdgeTypeBetween } from "./graphStructure";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { DEFAULT_NODE_SIZES } from "../nodes/SwfDefaultSizes";
import { useSwfEditorStore } from "../../store/StoreContext";
import { useKieEdgePath } from "../edges/useKieEdgePath";
import { PositionalNodeHandleId } from "./PositionalNodeHandles";

export function ConnectionLine({ toX, toY, fromNode, fromHandle }: RF.ConnectionLineComponentProps) {
  const snapGrid = useSwfEditorStore((s) => s.diagram.snapGrid);
  const edgeBeingUpdated = useSwfEditorStore((s) =>
    s.diagram.edgeIdBeingUpdated
      ? s.computed(s).getDiagramData().edgesById.get(s.diagram.edgeIdBeingUpdated)
      : undefined
  );
  const kieEdgePath = useKieEdgePath(edgeBeingUpdated?.source, edgeBeingUpdated?.target, edgeBeingUpdated?.data);

  // This works because nodes are configured with:
  // - Source handles with ids matching EDGE_TYPES or NODE_TYPES
  // - Target handles with ids matching TargetHandleId
  //
  // When editing an existing edge from its first waypoint (i.e., source handle) the edge is rendered
  // in reverse. So the connection line's "from" properties are actually "to" properties.
  const isUpdatingFromSourceHandle = Object.keys(PositionalNodeHandleId).some(
    (k) => (PositionalNodeHandleId as any)[k] === fromHandle?.id
  );

  const { x: fromX, y: fromY } = getBoundsCenterPoint({
    x: fromNode?.positionAbsolute?.x,
    y: fromNode?.positionAbsolute?.y,
    width: fromNode?.width,
    height: fromNode?.height,
  });

  const connectionLinePath =
    edgeBeingUpdated && kieEdgePath.points
      ? isUpdatingFromSourceHandle
        ? pointsToPath([{ x: toX, y: toY }, ...kieEdgePath.points.slice(1)]) // First point is being dragged
        : pointsToPath([...kieEdgePath.points.slice(0, -1), { x: toX, y: toY }]) // Last point is being dragged
      : `M${fromX},${fromY} L${toX},${toY}`;

  const handleId = isUpdatingFromSourceHandle ? edgeBeingUpdated?.type : edgeBeingUpdated?.type ?? fromHandle?.id;

  // Edges
  if (handleId === EDGE_TYPES.compensationTransition) {
    return <CompensationTransitionPath d={connectionLinePath} />;
  } else if (handleId === EDGE_TYPES.dataConditionTransition) {
    return <DataConditionTransitionPath d={connectionLinePath} />;
  } else if (handleId === EDGE_TYPES.defaultConditionTransition) {
    return <DefaultConditionTransitionPath d={connectionLinePath} />;
  } else if (handleId === EDGE_TYPES.errorTransition) {
    return <ErrorTransitionPath d={connectionLinePath} />;
  } else if (handleId === EDGE_TYPES.eventConditionTransition) {
    return <EventConditionTransitionPath d={connectionLinePath} />;
  } else if (handleId === EDGE_TYPES.transition) {
    return <TransitionPath d={connectionLinePath} />;
  }
  // Nodes
  else {
    const nodeType = handleId as NodeType;
    const { x: toXsnapped, y: toYsnapped } = snapPoint(snapGrid, { x: toX, y: toY });

    const defaultSize = DEFAULT_NODE_SIZES[nodeType]({ snapGrid });
    const [toXauto, toYauto] = getPositionalHandlePosition(
      { x: toXsnapped, y: toYsnapped, width: defaultSize["width"], height: defaultSize["height"] },
      { x: fromX, y: fromY, width: 1, height: 1 }
    );

    const edgeType = getDefaultEdgeTypeBetween(fromNode?.type as NodeType, handleId as NodeType);
    if (!edgeType) {
      throw new Error(`Invalid structure: ${fromNode?.type} --(any)--> ${handleId}`);
    }

    const path = `M${fromX},${fromY} L${toXauto},${toYauto}`;

    const edgeSvg = switchExpression(edgeType, {
      [EDGE_TYPES.compensationTransition]: <CompensationTransitionPath d={path} />,
      [EDGE_TYPES.dataConditionTransition]: <DataConditionTransitionPath d={path} />,
      [EDGE_TYPES.defaultConditionTransition]: <DefaultConditionTransitionPath d={path} />,
      [EDGE_TYPES.errorTransition]: <ErrorTransitionPath d={path} />,
      [EDGE_TYPES.eventConditionTransition]: <EventConditionTransitionPath d={path} />,
      [EDGE_TYPES.transition]: <TransitionPath d={path} />,
    });

    if (nodeType === NODE_TYPES.callbackState) {
      return (
        <g>
          {edgeSvg}
          <CallbackstateSvg x={toXsnapped} y={toYsnapped} width={defaultSize["width"]} height={defaultSize["height"]} />
        </g>
      );
    } else if (nodeType === NODE_TYPES.eventState) {
      return (
        <g className={"pulse"}>
          {edgeSvg}
          <EventstateSvg x={toXsnapped} y={toYsnapped} width={defaultSize["width"]} height={defaultSize["height"]} />
        </g>
      );
    } else if (nodeType === NODE_TYPES.foreachState) {
      return (
        <g>
          {edgeSvg}
          <ForeachstateSvg x={toXsnapped} y={toYsnapped} width={defaultSize["width"]} height={defaultSize["height"]} />
        </g>
      );
    } else if (nodeType === NODE_TYPES.injectState) {
      return (
        <g>
          {edgeSvg}
          <InjectstateSvg x={toXsnapped} y={toYsnapped} width={defaultSize["width"]} height={defaultSize["height"]} />
        </g>
      );
    } else if (nodeType === NODE_TYPES.operationState) {
      return (
        <g>
          {edgeSvg}
          <OperationstateSvg
            x={toXsnapped}
            y={toYsnapped}
            width={defaultSize["width"]}
            height={defaultSize["height"]}
          />
        </g>
      );
    } else if (nodeType === NODE_TYPES.parallelState) {
      return (
        <g>
          {edgeSvg}
          <ParallelstateSvg x={toXsnapped} y={toYsnapped} width={defaultSize["width"]} height={defaultSize["height"]} />
        </g>
      );
    } else if (nodeType === NODE_TYPES.sleepState) {
      return (
        <g>
          {edgeSvg}
          <SleepstateSvg x={toXsnapped} y={toYsnapped} width={defaultSize["width"]} height={defaultSize["height"]} />
        </g>
      );
    } else if (nodeType === NODE_TYPES.switchState) {
      return (
        <g>
          {edgeSvg}
          <SwitchstateSvg x={toXsnapped} y={toYsnapped} width={defaultSize["width"]} height={defaultSize["height"]} />
        </g>
      );
    }
  }

  throw new Error(`Unknown source of ConnectionLine '${handleId}'.`);
}
