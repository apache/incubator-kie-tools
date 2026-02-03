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

import { Text } from "@visx/text";
import * as React from "react";
import { useMemo } from "react";
import * as RF from "reactflow";
import {
  BpmnNodeElement,
  BpmnNodeType,
  EventVariant,
  GatewayVariant,
  MIN_NODE_SIZES,
  SubProcessVariant,
  TaskVariant,
} from "../diagram/BpmnDiagramDomain";
import { EdgeMarkers } from "@kie-tools/xyflow-react-kie-diagram/dist/edges/EdgeMarkers";
import { BpmnDiagramEdgeData } from "../diagram/BpmnDiagramDomain";
import { EDGE_TYPES } from "../diagram/BpmnDiagramDomain";
import { getSnappedMultiPointAnchoredEdgePath } from "@kie-tools/xyflow-react-kie-diagram/dist/edges/getSnappedMultiPointAnchoredEdgePath";
import { BpmnDiagramNodeData } from "../diagram/BpmnDiagramDomain";
import { getBpmnFontStyle, getNodeLabelPosition, getNodeStyle } from "../diagram/nodes/NodeStyle";
import { assertUnreachable } from "../ts-ext/assertUnreachable";
import {
  DataObjectNodeSvg,
  EndEventNodeSvg,
  GatewayNodeSvg,
  GroupNodeSvg,
  IntermediateCatchEventNodeSvg,
  IntermediateThrowEventNodeSvg,
  LaneNodeSvg,
  StartEventNodeSvg,
  SubProcessNodeSvg,
  TaskNodeSvg,
  TextAnnotationNodeSvg,
  UnknownNodeSvg,
} from "../diagram/nodes/NodeSvgs";
import { NODE_TYPES } from "../diagram/BpmnDiagramDomain";
import { snapBounds, SnapGrid } from "@kie-tools/xyflow-react-kie-diagram/dist/snapgrid/SnapGrid";
import { NodeLabelPosition } from "@kie-tools/xyflow-react-kie-diagram/dist/nodes/NodeSvgs";
import { AssociationPath, SequenceFlowPath } from "../diagram/edges/EdgeSvgs";
import { getShouldDisplayIsInterruptingFlag } from "../propertiesPanel/singleNodeProperties/StartEventProperties";
import { Normalized } from "../normalization/normalize";
import {
  BPMN20__tEndEvent,
  BPMN20__tGateway,
  BPMN20__tIntermediateCatchEvent,
  BPMN20__tStartEvent,
  BPMN20__tSubProcess,
  BPMN20__tTask,
  BPMN20__tThrowEvent,
} from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import {
  BOUNDARY_EVENT_CANCEL_ACTIVITY_DEFAULT_VALUE,
  START_EVENT_NODE_ON_EVENT_SUB_PROCESSES_IS_INTERRUPTING_DEFAULT_VALUE,
} from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/Bpmn20Spec";
import { useActivityIcons } from "../diagram/nodes/Nodes";
import { useCustomTasks } from "../customTasks/BpmnEditorCustomTasksContextProvider";
import { CustomTask } from "../BpmnEditor";

// Map node types to variant return types
type NodeVariantReturnMap = {
  [NODE_TYPES.startEvent]: EventVariant | "none";
  [NODE_TYPES.intermediateCatchEvent]: EventVariant | "none";
  [NODE_TYPES.intermediateThrowEvent]: EventVariant | "none";
  [NODE_TYPES.endEvent]: EventVariant | "none";
  [NODE_TYPES.gateway]: GatewayVariant | "none";
  [NODE_TYPES.subProcess]: SubProcessVariant | "other";
  [NODE_TYPES.task]: TaskVariant | "task" | "callActivity" | "none";
};

// Map node types to input types (Normalized BPMN elements)
type NodeElementMap = {
  [NODE_TYPES.startEvent]: Normalized<BPMN20__tStartEvent>;
  [NODE_TYPES.intermediateCatchEvent]: Normalized<BPMN20__tIntermediateCatchEvent>;
  [NODE_TYPES.intermediateThrowEvent]: Normalized<BPMN20__tThrowEvent>;
  [NODE_TYPES.endEvent]: Normalized<BPMN20__tEndEvent>;
  [NODE_TYPES.gateway]: Normalized<BPMN20__tGateway>;
  [NODE_TYPES.subProcess]: Normalized<BPMN20__tSubProcess>;
  [NODE_TYPES.task]: Normalized<BPMN20__tTask>;
};

type TaskNodeSvgWithoutMarkersProps = Omit<React.ComponentProps<typeof TaskNodeSvg>, "markers">;
type SubProcessNodeSvgWithoutIconsProps = Omit<React.ComponentProps<typeof SubProcessNodeSvg>, "icons">;
type BpmnElementActivitytIcons = Parameters<typeof useActivityIcons>[0];

type TaskOrSubProcessNodeSvgWithIconsProps = {
  nodeType: typeof NODE_TYPES.task | typeof NODE_TYPES.subProcess;
  bpmnElement: BpmnElementActivitytIcons;
  customTasks: CustomTask[] | undefined;
} & (TaskNodeSvgWithoutMarkersProps | SubProcessNodeSvgWithoutIconsProps);

export function BpmnDiagramSvg({
  nodes,
  edges,
  customTasks,
  snapGrid,
}: {
  nodes: RF.Node<BpmnDiagramNodeData, BpmnNodeType>[];
  edges: RF.Edge<BpmnDiagramEdgeData>[];
  customTasks: CustomTask[] | undefined;
  snapGrid: SnapGrid;
}) {
  const sortedNodesByParent = useMemo(() => sortNodesByParent(nodes), [nodes]);
  const { nodesSvg, nodesById } = useMemo(() => {
    const nodesById = new Map<string, RF.Node<BpmnDiagramNodeData, BpmnNodeType>>();

    const nodesSvg = sortedNodesByParent.map((node) => {
      const { fontCssProperties: fontStyle } = getNodeStyle({
        bpmnFontStyle: getBpmnFontStyle({ isEnabled: true }),
      });

      nodesById.set(node.id, node);

      const { height, width, strokeWidth, strokeDasharray, borderRadius, ...style } = node.style!;

      const label =
        node.data.bpmnElement.__$$element !== "group"
          ? node.data.bpmnElement.__$$element === "textAnnotation"
            ? node?.data?.bpmnElement?.text?.__$$text ?? ""
            : node.data.bpmnElement["@_name"] ?? ""
          : "";

      return (
        // bpmn2nodeid is necessary for the Kogito SVG Add-on
        <g id={node.id} data-kie-bpmn-node-id={node.id} key={node.id} {...({ bpmn2nodeid: node.id } as any)}>
          {node.type === NODE_TYPES.dataObject && (
            <DataObjectNodeSvg
              width={node.width!}
              height={node.height!}
              x={node.position!.x}
              y={node.position!.y}
              showFoldedPage={true}
              // Doesn't need to be painted by the Kogito SVG Add-on
              {...style}
            />
          )}
          {node.type === NODE_TYPES.task && (
            <TaskOrSubProcessNodeSvgWithIcons
              nodeType={NODE_TYPES.task}
              bpmnElement={node?.data?.bpmnElement as BpmnElementActivitytIcons}
              width={node.width!}
              height={node.height!}
              x={node.position!.x}
              y={node.position!.y}
              variant={getBpmnNodeVariant<typeof NODE_TYPES.task>(NODE_TYPES.task, node?.data?.bpmnElement)}
              strokeWidth={node?.data?.bpmnElement?.__$$element === "callActivity" ? 5 : undefined}
              customTasks={customTasks}
              exportedSvgId={node.id}
              {...style}
            />
          )}
          {node.type === NODE_TYPES.group && (
            <GroupNodeSvg
              width={node.width!}
              height={node.height!}
              x={node.position!.x}
              y={node.position!.y}
              strokeWidth={3}
              // Doesn't need to be painted by the Kogito SVG Add-on
              {...style}
            />
          )}
          {node.type === NODE_TYPES.textAnnotation && (
            <TextAnnotationNodeSvg
              width={node.width!}
              height={node.height!}
              x={node.position!.x}
              y={node.position!.y}
              // Doesn't need to be painted by the Kogito SVG Add-on
              {...style}
            />
          )}
          {node.type === NODE_TYPES.startEvent && (
            <StartEventNodeSvg
              width={node.width!}
              height={node.height!}
              x={node.position!.x}
              y={node.position!.y}
              variant={getBpmnNodeVariant<typeof NODE_TYPES.startEvent>(NODE_TYPES.startEvent, node?.data?.bpmnElement)}
              isInterrupting={
                getShouldDisplayIsInterruptingFlag(
                  node?.data?.bpmnElement,
                  node?.data?.bpmnElement as Normalized<BPMN20__tStartEvent> & { __$$element: "startEvent" }
                )
                  ? (node?.data?.bpmnElement as Normalized<BPMN20__tStartEvent>)["@_isInterrupting"] ??
                    START_EVENT_NODE_ON_EVENT_SUB_PROCESSES_IS_INTERRUPTING_DEFAULT_VALUE
                  : START_EVENT_NODE_ON_EVENT_SUB_PROCESSES_IS_INTERRUPTING_DEFAULT_VALUE
              }
              exportedSvgId={node.id}
              {...style}
            />
          )}
          {node.type === NODE_TYPES.intermediateCatchEvent && (
            <IntermediateCatchEventNodeSvg
              width={node.width!}
              height={node.height!}
              x={node.position!.x}
              y={node.position!.y}
              variant={getBpmnNodeVariant<typeof NODE_TYPES.intermediateCatchEvent>(
                NODE_TYPES.intermediateCatchEvent,
                node?.data?.bpmnElement
              )}
              isInterrupting={
                node?.data?.bpmnElement.__$$element === "boundaryEvent"
                  ? node?.data?.bpmnElement["@_cancelActivity"] ?? BOUNDARY_EVENT_CANCEL_ACTIVITY_DEFAULT_VALUE
                  : true
              }
              exportedSvgId={node.id}
              {...style}
            />
          )}
          {node.type === NODE_TYPES.intermediateThrowEvent && (
            <IntermediateThrowEventNodeSvg
              width={node.width!}
              height={node.height!}
              x={node.position!.x}
              y={node.position!.y}
              variant={getBpmnNodeVariant<typeof NODE_TYPES.intermediateThrowEvent>(
                NODE_TYPES.intermediateThrowEvent,
                node?.data?.bpmnElement
              )}
              exportedSvgId={node.id}
              {...style}
            />
          )}
          {node.type === NODE_TYPES.gateway && (
            <GatewayNodeSvg
              width={node.width!}
              height={node.height!}
              x={node.position!.x}
              y={node.position!.y}
              variant={getBpmnNodeVariant<typeof NODE_TYPES.gateway>(NODE_TYPES.gateway, node?.data?.bpmnElement)}
              exportedSvgId={node.id}
              {...style}
            />
          )}
          {node.type === NODE_TYPES.endEvent && (
            <EndEventNodeSvg
              width={node.width!}
              height={node.height!}
              x={node.position!.x}
              y={node.position!.y}
              variant={getBpmnNodeVariant<typeof NODE_TYPES.endEvent>(NODE_TYPES.endEvent, node?.data?.bpmnElement)}
              strokeWidth={6}
              exportedSvgId={node.id}
              {...style}
            />
          )}
          {node.type === NODE_TYPES.subProcess && (
            <TaskOrSubProcessNodeSvgWithIcons
              customTasks={customTasks}
              nodeType={NODE_TYPES.subProcess}
              bpmnElement={node?.data?.bpmnElement as BpmnElementActivitytIcons}
              width={node.width!}
              height={node.height!}
              x={node.position!.x}
              y={node.position!.y}
              variant={getBpmnNodeVariant<typeof NODE_TYPES.subProcess>(NODE_TYPES.subProcess, node?.data?.bpmnElement)}
              exportedSvgId={node.id}
              {...style}
            />
          )}
          {node.type === NODE_TYPES.lane && (
            <LaneNodeSvg
              width={node.width!}
              height={node.height!}
              x={node.position!.x}
              y={node.position!.y}
              // Doesn't need to be painted by the Kogito SVG Add-on
              {...style}
            />
          )}
          {node.type === NODE_TYPES.unknown && (
            <UnknownNodeSvg
              width={node.width!}
              height={node.height!}
              x={node.position!.x}
              y={node.position!.y}
              // Doesn't need to be painted by the Kogito SVG Add-on
              {...style}
            />
          )}
          {label.trim() && (
            <>
              {label.split("\n").map((labelLine, i) => (
                <Text
                  key={i}
                  lineHeight={fontStyle.lineHeight}
                  style={{ ...fontStyle }}
                  dy={`calc(1.5em * ${i})`}
                  {...getNodeLabelSvgTextAlignmentProps(
                    node,
                    getNodeLabelPosition({ nodeType: node.type as BpmnNodeType })
                  )}
                >
                  {labelLine}
                </Text>
              ))}
            </>
          )}
        </g>
      );
    });

    return { nodesSvg, nodesById };
  }, [customTasks, sortedNodesByParent]);

  return (
    <>
      <EdgeMarkers />
      {edges.map((e) => {
        const s = nodesById?.get(e.source);
        const t = nodesById?.get(e.target);

        const { path } = getSnappedMultiPointAnchoredEdgePath({
          snapGrid: {
            isEnabled: snapGrid.isEnabled,
            x: snapGrid.x / 2,
            y: snapGrid.y / 2,
          },
          edge: e.data?.bpmnEdge,
          snappedSourceNodeBounds: snapBounds(
            snapGrid,
            s?.data.shape?.["dc:Bounds"],
            e.data?.bpmnSourceType
              ? MIN_NODE_SIZES[e.data?.bpmnSourceType]({ snapGrid })
              : { "@_height": 0, "@_width": 0 }
          ),
          snappedTargetNodeBounds: snapBounds(
            snapGrid,
            t?.data.shape?.["dc:Bounds"],
            e.data?.bpmnTargetType
              ? MIN_NODE_SIZES[e.data?.bpmnTargetType]({ snapGrid })
              : { "@_height": 0, "@_width": 0 }
          ),
          shapeSource: e.data?.bpmnShapeSource,
          shapeTarget: e.data?.bpmnShapeTarget,
        });
        return (
          // bpmn2nodeid is necessary for the Kogito SVG Add-on
          <g key={e.id} id={e.id} {...({ bpmn2nodeid: e.id } as any)}>
            {e.type === EDGE_TYPES.sequenceFlow && <SequenceFlowPath d={path} />}
            {e.type === EDGE_TYPES.association && <AssociationPath d={path} />}
          </g>
        );
      })}
      {nodesSvg}
    </>
  );
}

const SVG_NODE_LABEL_TEXT_PADDING_ALL = 10;
const SVG_NODE_LABEL_TEXT_ADDITIONAL_PADDING_TOP_LEFT = 8;

export function getNodeLabelSvgTextAlignmentProps(
  n: RF.Node<BpmnDiagramNodeData, BpmnNodeType>,
  labelPosition: NodeLabelPosition
) {
  switch (labelPosition) {
    case "center-bottom":
      const cbTx = n.position.x! + n.width! / 2;
      const cbTy = n.position.y! + n.height! + 4;
      const cbWidth = n.width!;
      return {
        verticalAnchor: "start",
        textAnchor: "middle",
        transform: `translate(${cbTx},${cbTy})`,
        width: cbWidth,
      } as const;

    case "center-center":
      const ccTx = n.position.x! + n.width! / 2;
      const ccTy = n.position.y! + n.height! / 2;
      const ccWidth = n.width! - 2 * SVG_NODE_LABEL_TEXT_PADDING_ALL;
      return {
        verticalAnchor: "middle",
        textAnchor: "middle",
        transform: `translate(${ccTx},${ccTy})`,
        width: ccWidth,
      } as const;

    case "top-center":
      const tcTx = n.position.x! + n.width! / 2;
      const tcTy = n.position.y! + SVG_NODE_LABEL_TEXT_PADDING_ALL;
      const tcWidth = n.width! - 2 * SVG_NODE_LABEL_TEXT_PADDING_ALL;
      return {
        verticalAnchor: "start",
        textAnchor: "middle",
        transform: `translate(${tcTx},${tcTy})`,
        width: tcWidth,
      } as const;

    case "center-left":
      const clTx = n.position.x! + SVG_NODE_LABEL_TEXT_PADDING_ALL;
      const clTy = n.position.y! + n.height! / 2;
      const clWidth = n.width! - 2 * SVG_NODE_LABEL_TEXT_PADDING_ALL;
      return {
        verticalAnchor: "middle",
        textAnchor: "start",
        transform: `translate(${clTx},${clTy})`,
        width: clWidth,
      } as const;

    case "center-left-vertical":
    case "top-left":
      const tlTx = n.position.x! + SVG_NODE_LABEL_TEXT_PADDING_ALL + SVG_NODE_LABEL_TEXT_ADDITIONAL_PADDING_TOP_LEFT;
      const tlTy = n.position.y! + SVG_NODE_LABEL_TEXT_PADDING_ALL + SVG_NODE_LABEL_TEXT_ADDITIONAL_PADDING_TOP_LEFT;
      const tlWidth =
        n.width! - 2 * SVG_NODE_LABEL_TEXT_PADDING_ALL - 2 * SVG_NODE_LABEL_TEXT_ADDITIONAL_PADDING_TOP_LEFT;
      return {
        verticalAnchor: "start",
        textAnchor: "start",
        transform: `translate(${tlTx},${tlTy})`,
        width: tlWidth,
      } as const;
    default:
      assertUnreachable(labelPosition);
  }
}

export function getBpmnNodeVariant<T extends keyof NodeElementMap>(
  nodeType: BpmnNodeType,
  bpmnElement: NodeElementMap[T]
): NodeVariantReturnMap[T] {
  switch (nodeType) {
    case NODE_TYPES.startEvent:
    case NODE_TYPES.intermediateCatchEvent:
    case NODE_TYPES.intermediateThrowEvent:
    case NODE_TYPES.endEvent:
      return (bpmnElement as any)?.eventDefinition?.[0]?.__$$element ?? "none";
    case NODE_TYPES.subProcess:
      const subProcessVariant = (bpmnElement as Normalized<BPMN20__tSubProcess>)?.["@_triggeredByEvent"]
        ? "event"
        : (bpmnElement as any)?.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics"
          ? "multi-instance"
          : "other";
      return subProcessVariant as NodeVariantReturnMap[T];
    case NODE_TYPES.gateway:
    case NODE_TYPES.task:
      return (bpmnElement as any)?.__$$element ?? "none";
    default:
      return "none" as NodeVariantReturnMap[T];
  }
}

export function TaskOrSubProcessNodeSvgWithIcons({
  nodeType,
  bpmnElement,
  customTasks,
  ...props
}: TaskOrSubProcessNodeSvgWithIconsProps) {
  const icons = useActivityIcons(bpmnElement);

  const icon = useMemo(() => {
    if (bpmnElement.__$$element === "task") {
      for (const ct of customTasks ?? []) {
        if (ct.matches(bpmnElement)) {
          return <>{ct.iconSvgElement}</>;
        }
      }
    }
  }, [customTasks, bpmnElement]);

  return nodeType === NODE_TYPES.task ? (
    <TaskNodeSvg {...(props as TaskNodeSvgWithoutMarkersProps)} markers={icons} icon={icon} />
  ) : (
    <SubProcessNodeSvg {...(props as SubProcessNodeSvgWithoutIconsProps)} icons={icons} />
  );
}

export function sortNodesByParent(
  nodes: RF.Node<BpmnDiagramNodeData, BpmnNodeType>[]
): RF.Node<BpmnDiagramNodeData, BpmnNodeType>[] {
  const nodeMap = new Map<string, RF.Node<BpmnDiagramNodeData, BpmnNodeType>>();
  const visited = new Set<string>();
  const sorted: RF.Node<BpmnDiagramNodeData, BpmnNodeType>[] = [];

  // Step 1: Build a map for quick access
  for (const node of nodes) {
    nodeMap.set(node.id, node);
  }

  // Step 2: DFS to ensure parent before child
  const visit = (node: RF.Node<BpmnDiagramNodeData<BpmnNodeElement>, BpmnNodeType>) => {
    if (visited.has(node.id)) return;

    const parentId = node.data?.parentXyFlowNode?.id;
    if (parentId) {
      const parent = nodeMap.get(parentId);
      if (parent) visit(parent); // Visit parent first
    }

    visited.add(node.id);
    sorted.push(node);
  };

  // Step 3: Visit all nodes
  for (const node of nodes) {
    visit(node);
  }

  return sorted;
}
