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
import {
  BPMN20__tBoundaryEvent,
  BPMN20__tDataObject,
  BPMN20__tEndEvent,
  BPMN20__tGroup,
  BPMN20__tIntermediateCatchEvent,
  BPMN20__tIntermediateThrowEvent,
  BPMN20__tLane,
  BPMN20__tProcess,
  BPMN20__tStartEvent,
  BPMN20__tSubProcess,
  BPMN20__tTask,
  BPMN20__tTextAnnotation,
} from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import {
  BOUNDARY_EVENT_CANCEL_ACTIVITY_DEFAULT_VALUE,
  START_EVENT_NODE_ON_EVENT_SUB_PROCESSES_IS_INTERRUPTING_DEFAULT_VALUE,
} from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/Bpmn20Spec";
import { getContainmentRelationship } from "@kie-tools/xyflow-react-kie-diagram/dist/maths/DcMaths";
import { propsHaveSameValuesDeep } from "@kie-tools/xyflow-react-kie-diagram/dist/memoization/memoization";
import {
  EditableNodeLabel,
  OnEditableNodeLabelChange,
  useEditableNodeLabel,
} from "@kie-tools/xyflow-react-kie-diagram/dist/nodes/EditableNodeLabel";
import {
  NodeResizerHandle,
  useConnectionTargetStatus,
  useHoveredNodeAlwaysOnTop,
  useNodeClassName,
  useNodeDimensions,
  useNodeResizing,
} from "@kie-tools/xyflow-react-kie-diagram/dist/nodes/Hooks";
import { InfoNodePanel } from "@kie-tools/xyflow-react-kie-diagram/dist/nodes/InfoNodePanel";
import { OutgoingStuffNodePanel } from "@kie-tools/xyflow-react-kie-diagram/dist/nodes/OutgoingStuffNodePanel";
import { PositionalNodeHandles } from "@kie-tools/xyflow-react-kie-diagram/dist/nodes/PositionalNodeHandles";
import { useIsHovered } from "@kie-tools/xyflow-react-kie-diagram/dist/reactExt/useIsHovered";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { updateFlowElement, updateLane, updateTextAnnotation } from "../../mutations/renameNode";
import { Normalized } from "../../normalization/normalize";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import {
  ActivityNodeMarker,
  BPMN_OUTGOING_STRUCTURE,
  BpmnDiagramEdgeData,
  BpmnDiagramNodeData,
  bpmnEdgesOutgoingStuffNodePanelMapping,
  bpmnNodesOutgoingStuffNodePanelMapping,
  BpmnNodeType,
  EDGE_TYPES,
  MIN_NODE_SIZES,
  NODE_TYPES,
} from "../BpmnDiagramDomain";
import { getNodeLabelPosition, useNodeStyle } from "./NodeStyle";
import {
  DataObjectNodeSvg,
  EndEventNodeSvg,
  GatewayNodeSvg,
  GroupNodeSvg,
  IntermediateCatchEventNodeSvg,
  IntermediateThrowEventNodeSvg,
  LaneNodeSvg,
  NODE_COLORS,
  StartEventNodeSvg,
  SubProcessNodeSvg,
  TaskNodeSvg,
  TextAnnotationNodeSvg,
  UnknownNodeSvg,
} from "./NodeSvgs";
import { NodeMorphingPanel } from "./morphing/NodeMorphingPanel";
import { useEventNodeMorphingActions } from "./morphing/useEventNodeMorphingActions";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { useGatewayNodeMorphingActions } from "./morphing/useGatewayNodeMorphingActions";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { useTaskNodeMorphingActions } from "./morphing/useTaskNodeMorphingActions";
import { useSubProcessNodeMorphingActions } from "./morphing/useSubProcessNodeMorphingActions";
import { useKeyboardShortcutsForMorphingActions } from "./morphing/useKeyboardShortcutsForMorphingActions";
import { getShouldDisplayIsInterruptingFlag } from "../../propertiesPanel/singleNodeProperties/StartEventProperties";
import "./Nodes.css";
import { useCustomTasks } from "../../customTasks/BpmnEditorCustomTasksContextProvider";

export const StartEventNode = React.memo(
  ({
    data: { bpmnElement: startEvent, shape, shapeIndex, parentXyFlowNode },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<BpmnDiagramNodeData<Normalized<BPMN20__tStartEvent> & { __$$element: "startEvent" }>>) => {
    const renderCount = useRef<number>(0);
    renderCount.current++;

    const ref = useRef<HTMLDivElement>(null);

    const isHovered = useIsHovered(ref);
    const isResizing = useNodeResizing(id);
    const shouldActLikeHovered = useBpmnEditorStore(
      (s) => (isHovered || isResizing) && s.xyFlowReactKieDiagram.draggingNodes.length === 0
    );

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(id);
    useHoveredNodeAlwaysOnTop(ref, zIndex, shouldActLikeHovered, dragging, selected, isEditingLabel);

    const bpmnEditorStoreApi = useBpmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id, NODE_TYPES, EDGE_TYPES);
    const nodeDimensions = useNodeDimensions({ shape, nodeType: type as BpmnNodeType, MIN_NODE_SIZES });

    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        bpmnEditorStoreApi.setState((state) => {
          updateFlowElement({
            definitions: state.bpmn.model.definitions,
            newFlowElement: { "@_name": newName.trim() },
            id,
          });
        });
      },
      [bpmnEditorStoreApi, id]
    );

    const [isMorphingPanelExpanded, setMorphingPanelExpanded] = useState(false);
    useEffect(() => setMorphingPanelExpanded(false), [isHovered]);
    const morphingActions = useEventNodeMorphingActions(startEvent);
    const disabledMorphingActionIds = useMemo<Set<Unpacked<typeof morphingActions>["id"]>>(
      () =>
        parentXyFlowNode?.type === NODE_TYPES.subProcess
          ? new Set(["none", "linkEventDefinition", "terminateEventDefinition"])
          : new Set([
              "errorEventDefinition",
              "escalationEventDefinition",
              "compensateEventDefinition",
              "linkEventDefinition",
              "terminateEventDefinition",
            ]),
      [parentXyFlowNode?.type]
    );
    useKeyboardShortcutsForMorphingActions(ref, morphingActions, disabledMorphingActionIds);

    return (
      <>
        <svg className={`xyflow-react-kie-diagram--node-shape ${className} ${selected ? "selected" : ""}`}>
          <StartEventNodeSvg
            {...nodeDimensions}
            x={0}
            y={0}
            variant={startEvent.eventDefinition?.[0]?.__$$element ?? "none"}
            isInterrupting={
              getShouldDisplayIsInterruptingFlag(parentXyFlowNode?.data.bpmnElement, startEvent)
                ? startEvent["@_isInterrupting"] ??
                  START_EVENT_NODE_ON_EVENT_SUB_PROCESSES_IS_INTERRUPTING_DEFAULT_VALUE
                : START_EVENT_NODE_ON_EVENT_SUB_PROCESSES_IS_INTERRUPTING_DEFAULT_VALUE
            }
          />
        </svg>
        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />
        <div
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
          className={`kie-bpmn-editor--task-node ${className} kie-bpmn-editor--selected-task-node`}
          ref={ref}
          tabIndex={-1}
          data-nodehref={id}
          data-nodelabel={startEvent["@_name"]}
        >
          {/* {`render count: ${renderCount.current}`}
          <br /> */}
          <div className={"xyflow-react-kie-diagram--node"}>
            <InfoNodePanel
              isVisible={!isMorphingPanelExpanded && !isTargeted && shouldActLikeHovered}
              onClick={useCallback(() => {
                bpmnEditorStoreApi.setState((state) => {
                  state.propertiesPanel.isOpen = true;
                });
              }, [bpmnEditorStoreApi])}
            />

            <OutgoingStuffNodePanel
              nodeMapping={bpmnNodesOutgoingStuffNodePanelMapping}
              edgeMapping={bpmnEdgesOutgoingStuffNodePanelMapping}
              nodeHref={id}
              isVisible={!isMorphingPanelExpanded && !isTargeted && shouldActLikeHovered}
              nodeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.startEvent].nodes}
              edgeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.startEvent].edges}
            />

            <NodeMorphingPanel
              disabledActionIds={disabledMorphingActionIds}
              isToggleVisible={!isTargeted && shouldActLikeHovered}
              isExpanded={isMorphingPanelExpanded}
              setExpanded={setMorphingPanelExpanded}
              actions={morphingActions}
              primaryColor={NODE_COLORS.startEvent.foreground}
              secondaryColor={NODE_COLORS.startEvent.background}
              selectedActionId={startEvent.eventDefinition?.[0].__$$element ?? "none"}
            />
          </div>
          {/* Creates a div element with the node size to push down the <EditableNodeLabel /> */}
          {<div style={{ height: nodeDimensions.height, flexShrink: 0 }} />}
          {(startEvent["@_name"] || isEditingLabel) && !isMorphingPanelExpanded && (
            <NodeLabelAtTheBottom>
              <EditableNodeLabel
                id={id}
                name={startEvent["@_name"]}
                isEditing={isEditingLabel}
                setEditing={setEditingLabel}
                position={getNodeLabelPosition({ nodeType: type as BpmnNodeType })}
                value={startEvent["@_name"]}
                onChange={setName}
                validate={() => true} // FIXME: Tiago
                shouldCommitOnBlur={true}
                // Keeps the text on top of the selected layer
                fontCssProperties={{ zIndex: 2000 }}
              />
            </NodeLabelAtTheBottom>
          )}
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

export const IntermediateCatchEventNode = React.memo(
  ({
    data: { bpmnElement: intermediateCatchEvent, shape, shapeIndex },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<
    BpmnDiagramNodeData<
      | (Normalized<BPMN20__tIntermediateCatchEvent> & { __$$element: "intermediateCatchEvent" })
      | (Normalized<BPMN20__tBoundaryEvent> & { __$$element: "boundaryEvent" })
    >
  >) => {
    const renderCount = useRef<number>(0);
    renderCount.current++;

    const ref = useRef<HTMLDivElement>(null);

    const isHovered = useIsHovered(ref);
    const isResizing = useNodeResizing(id);
    const shouldActLikeHovered = useBpmnEditorStore(
      (s) => (isHovered || isResizing) && s.xyFlowReactKieDiagram.draggingNodes.length === 0
    );

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(id);
    useHoveredNodeAlwaysOnTop(ref, zIndex, shouldActLikeHovered, dragging, selected, isEditingLabel);

    const bpmnEditorStoreApi = useBpmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id, NODE_TYPES, EDGE_TYPES);
    const nodeDimensions = useNodeDimensions({ shape, nodeType: type as BpmnNodeType, MIN_NODE_SIZES });

    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        bpmnEditorStoreApi.setState((state) => {
          updateFlowElement({
            definitions: state.bpmn.model.definitions,
            newFlowElement: { "@_name": newName.trim() },
            id,
          });
        });
      },
      [bpmnEditorStoreApi, id]
    );

    const [isMorphingPanelExpanded, setMorphingPanelExpanded] = useState(false);
    useEffect(() => setMorphingPanelExpanded(false), [isHovered]);
    const morphingActions = useEventNodeMorphingActions(intermediateCatchEvent);
    const disabledMorphingActionIds = useMemo<Set<Unpacked<typeof morphingActions>["id"]>>(
      () =>
        intermediateCatchEvent.__$$element === "intermediateCatchEvent"
          ? new Set(["none", "terminateEventDefinition"])
          : intermediateCatchEvent.__$$element === "boundaryEvent"
            ? new Set(["none", "linkEventDefinition", "terminateEventDefinition"])
            : new Set(),
      [intermediateCatchEvent.__$$element]
    );
    useKeyboardShortcutsForMorphingActions(ref, morphingActions, disabledMorphingActionIds);

    return (
      <>
        <svg className={`xyflow-react-kie-diagram--node-shape ${className} ${selected ? "selected" : ""}`}>
          <IntermediateCatchEventNodeSvg
            {...nodeDimensions}
            x={0}
            y={0}
            variant={intermediateCatchEvent.eventDefinition?.[0].__$$element ?? "none"}
            isInterrupting={
              intermediateCatchEvent.__$$element === "boundaryEvent"
                ? intermediateCatchEvent["@_cancelActivity"] ?? BOUNDARY_EVENT_CANCEL_ACTIVITY_DEFAULT_VALUE
                : true
            }
          />
        </svg>
        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />
        <div
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
          className={`kie-bpmn-editor--intermediate-catch-event-node ${className} kie-bpmn-editor--selected-intermediate-catch-event-node`}
          ref={ref}
          tabIndex={-1}
          data-nodehref={id}
          data-nodelabel={id}
        >
          {/* {`render count: ${renderCount.current}`}
          <br /> */}
          <div className={"xyflow-react-kie-diagram--node"}>
            <InfoNodePanel
              isVisible={!isMorphingPanelExpanded && !isTargeted && shouldActLikeHovered}
              onClick={useCallback(() => {
                bpmnEditorStoreApi.setState((state) => {
                  state.propertiesPanel.isOpen = true;
                });
              }, [bpmnEditorStoreApi])}
            />

            <OutgoingStuffNodePanel
              nodeMapping={bpmnNodesOutgoingStuffNodePanelMapping}
              edgeMapping={bpmnEdgesOutgoingStuffNodePanelMapping}
              nodeHref={id}
              isVisible={!isMorphingPanelExpanded && !isTargeted && shouldActLikeHovered}
              nodeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.intermediateCatchEvent].nodes}
              edgeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.intermediateCatchEvent].edges}
            />

            <NodeMorphingPanel
              disabledActionIds={disabledMorphingActionIds}
              isToggleVisible={!isTargeted && shouldActLikeHovered}
              isExpanded={isMorphingPanelExpanded}
              setExpanded={setMorphingPanelExpanded}
              actions={morphingActions}
              primaryColor={NODE_COLORS.intermediateCatchEvent.foreground}
              secondaryColor={NODE_COLORS.intermediateCatchEvent.background}
              selectedActionId={intermediateCatchEvent.eventDefinition?.[0].__$$element ?? "none"}
            />
          </div>
          {/* Creates a div element with the node size to push down the <EditableNodeLabel /> */}
          {<div style={{ height: nodeDimensions.height, flexShrink: 0 }} />}
          {(intermediateCatchEvent["@_name"] || isEditingLabel) && !isMorphingPanelExpanded && (
            <NodeLabelAtTheBottom>
              <EditableNodeLabel
                id={id}
                name={intermediateCatchEvent["@_name"]}
                isEditing={isEditingLabel}
                setEditing={setEditingLabel}
                position={getNodeLabelPosition({ nodeType: type as BpmnNodeType })}
                value={intermediateCatchEvent["@_name"]}
                onChange={setName}
                validate={() => true} // FIXME: Tiago
                shouldCommitOnBlur={true}
                // Keeps the text on top of the selected layer
                fontCssProperties={{ zIndex: 2000 }}
              />
            </NodeLabelAtTheBottom>
          )}
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

export const IntermediateThrowEventNode = React.memo(
  ({
    data: { bpmnElement: intermediateThrowEvent, shape, shapeIndex },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<
    BpmnDiagramNodeData<Normalized<BPMN20__tIntermediateThrowEvent> & { __$$element: "intermediateThrowEvent" }>
  >) => {
    const renderCount = useRef<number>(0);
    renderCount.current++;

    const ref = useRef<HTMLDivElement>(null);

    const isHovered = useIsHovered(ref);
    const isResizing = useNodeResizing(id);
    const shouldActLikeHovered = useBpmnEditorStore(
      (s) => (isHovered || isResizing) && s.xyFlowReactKieDiagram.draggingNodes.length === 0
    );

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(id);
    useHoveredNodeAlwaysOnTop(ref, zIndex, shouldActLikeHovered, dragging, selected, isEditingLabel);

    const bpmnEditorStoreApi = useBpmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id, NODE_TYPES, EDGE_TYPES);
    const nodeDimensions = useNodeDimensions({ shape, nodeType: type as BpmnNodeType, MIN_NODE_SIZES });

    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        bpmnEditorStoreApi.setState((state) => {
          updateFlowElement({
            definitions: state.bpmn.model.definitions,
            newFlowElement: { "@_name": newName.trim() },
            id,
          });
        });
      },
      [bpmnEditorStoreApi, id]
    );

    const [isMorphingPanelExpanded, setMorphingPanelExpanded] = useState(false);
    useEffect(() => setMorphingPanelExpanded(false), [isHovered]);
    const morphingActions = useEventNodeMorphingActions(intermediateThrowEvent);
    const disabledMorphingActionIds = useMemo<Set<Unpacked<typeof morphingActions>["id"]>>(
      () =>
        new Set([
          "none",
          "timerEventDefinition",
          "errorEventDefinition",
          "conditionalEventDefinition",
          "terminateEventDefinition",
        ]),
      []
    );
    useKeyboardShortcutsForMorphingActions(ref, morphingActions, disabledMorphingActionIds);

    return (
      <>
        <svg className={`xyflow-react-kie-diagram--node-shape ${className} ${selected ? "selected" : ""}`}>
          <IntermediateThrowEventNodeSvg
            {...nodeDimensions}
            x={0}
            y={0}
            variant={intermediateThrowEvent.eventDefinition?.[0]?.__$$element ?? "none"}
          />
        </svg>
        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />
        <div
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
          className={`kie-bpmn-editor--intermediate-throw-event-node ${className} kie-bpmn-editor--selected-intermediate-throw-event-node`}
          ref={ref}
          tabIndex={-1}
          data-nodehref={id}
          data-nodelabel={id}
        >
          {/* {`render count: ${renderCount.current}`}
          <br /> */}
          <div className={"xyflow-react-kie-diagram--node"}>
            <InfoNodePanel
              isVisible={!isMorphingPanelExpanded && !isTargeted && shouldActLikeHovered}
              onClick={useCallback(() => {
                bpmnEditorStoreApi.setState((state) => {
                  state.propertiesPanel.isOpen = true;
                });
              }, [bpmnEditorStoreApi])}
            />

            <OutgoingStuffNodePanel
              nodeMapping={bpmnNodesOutgoingStuffNodePanelMapping}
              edgeMapping={bpmnEdgesOutgoingStuffNodePanelMapping}
              nodeHref={id}
              isVisible={!isMorphingPanelExpanded && !isTargeted && shouldActLikeHovered}
              nodeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.intermediateThrowEvent].nodes}
              edgeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.intermediateThrowEvent].edges}
            />

            <NodeMorphingPanel
              disabledActionIds={disabledMorphingActionIds}
              isToggleVisible={!isTargeted && shouldActLikeHovered}
              isExpanded={isMorphingPanelExpanded}
              setExpanded={setMorphingPanelExpanded}
              actions={morphingActions}
              primaryColor={NODE_COLORS.intermediateThrowEvent.foreground}
              secondaryColor={NODE_COLORS.intermediateThrowEvent.background}
              selectedActionId={intermediateThrowEvent.eventDefinition?.[0].__$$element ?? "none"}
            />
          </div>
          {/* Creates a div element with the node size to push down the <EditableNodeLabel /> */}
          {<div style={{ height: nodeDimensions.height, flexShrink: 0 }} />}
          {(intermediateThrowEvent["@_name"] || isEditingLabel) && !isMorphingPanelExpanded && (
            <NodeLabelAtTheBottom>
              <EditableNodeLabel
                id={id}
                name={intermediateThrowEvent["@_name"]}
                isEditing={isEditingLabel}
                setEditing={setEditingLabel}
                position={getNodeLabelPosition({ nodeType: type as BpmnNodeType })}
                value={intermediateThrowEvent["@_name"]}
                onChange={setName}
                validate={() => true} // FIXME: Tiago
                shouldCommitOnBlur={true}
                // Keeps the text on top of the selected layer
                fontCssProperties={{ zIndex: 2000 }}
              />
            </NodeLabelAtTheBottom>
          )}
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

export const EndEventNode = React.memo(
  ({
    data: { bpmnElement: endEvent, shape, shapeIndex },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<BpmnDiagramNodeData<Normalized<BPMN20__tEndEvent> & { __$$element: "endEvent" }>>) => {
    const renderCount = useRef<number>(0);
    renderCount.current++;

    const ref = useRef<HTMLDivElement>(null);

    const isHovered = useIsHovered(ref);
    const isResizing = useNodeResizing(id);
    const shouldActLikeHovered = useBpmnEditorStore(
      (s) => (isHovered || isResizing) && s.xyFlowReactKieDiagram.draggingNodes.length === 0
    );

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(id);
    useHoveredNodeAlwaysOnTop(ref, zIndex, shouldActLikeHovered, dragging, selected, isEditingLabel);

    const bpmnEditorStoreApi = useBpmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id, NODE_TYPES, EDGE_TYPES);
    const nodeDimensions = useNodeDimensions({ shape, nodeType: type as BpmnNodeType, MIN_NODE_SIZES });

    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        bpmnEditorStoreApi.setState((state) => {
          updateFlowElement({
            definitions: state.bpmn.model.definitions,
            newFlowElement: { "@_name": newName.trim() },
            id,
          });
        });
      },
      [bpmnEditorStoreApi, id]
    );

    const [isMorphingPanelExpanded, setMorphingPanelExpanded] = useState(false);
    useEffect(() => setMorphingPanelExpanded(false), [isHovered]);
    const morphingActions = useEventNodeMorphingActions(endEvent);
    const disabledMorphingActionIds = useMemo<Set<Unpacked<typeof morphingActions>["id"]>>(
      () => new Set(["timerEventDefinition", "conditionalEventDefinition", "linkEventDefinition"]),
      []
    );
    useKeyboardShortcutsForMorphingActions(ref, morphingActions, disabledMorphingActionIds);

    return (
      <>
        <svg className={`xyflow-react-kie-diagram--node-shape ${className} ${selected ? "selected" : ""}`}>
          <EndEventNodeSvg
            {...nodeDimensions}
            x={0}
            y={0}
            variant={endEvent.eventDefinition?.[0]?.__$$element ?? "none"}
            strokeWidth={6}
          />
        </svg>
        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />
        <div
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
          className={`kie-bpmn-editor--end-event-node ${className} kie-bpmn-editor--selected-end-event-node`}
          ref={ref}
          tabIndex={-1}
          data-nodehref={id}
          data-nodelabel={id}
        >
          {/* {`render count: ${renderCount.current}`}
          <br /> */}
          <div className={"xyflow-react-kie-diagram--node"}>
            <InfoNodePanel
              isVisible={!isMorphingPanelExpanded && !isTargeted && shouldActLikeHovered}
              onClick={useCallback(() => {
                bpmnEditorStoreApi.setState((state) => {
                  state.propertiesPanel.isOpen = true;
                });
              }, [bpmnEditorStoreApi])}
            />

            <OutgoingStuffNodePanel
              nodeMapping={bpmnNodesOutgoingStuffNodePanelMapping}
              edgeMapping={bpmnEdgesOutgoingStuffNodePanelMapping}
              nodeHref={id}
              isVisible={!isMorphingPanelExpanded && !isTargeted && shouldActLikeHovered}
              nodeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.endEvent].nodes}
              edgeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.endEvent].edges}
            />

            <NodeMorphingPanel
              disabledActionIds={disabledMorphingActionIds}
              isToggleVisible={!isTargeted && shouldActLikeHovered}
              isExpanded={isMorphingPanelExpanded}
              setExpanded={setMorphingPanelExpanded}
              actions={morphingActions}
              primaryColor={NODE_COLORS.endEvent.foreground}
              secondaryColor={NODE_COLORS.endEvent.background}
              selectedActionId={endEvent.eventDefinition?.[0].__$$element ?? "none"}
            />
          </div>
          {/* Creates a div element with the node size to push down the <EditableNodeLabel /> */}
          {<div style={{ height: nodeDimensions.height, flexShrink: 0 }} />}
          {(endEvent["@_name"] || isEditingLabel) && !isMorphingPanelExpanded && (
            <NodeLabelAtTheBottom>
              <EditableNodeLabel
                id={id}
                name={endEvent["@_name"]}
                isEditing={isEditingLabel}
                setEditing={setEditingLabel}
                position={getNodeLabelPosition({ nodeType: type as BpmnNodeType })}
                value={endEvent["@_name"]}
                onChange={setName}
                validate={() => true} // FIXME: Tiago
                shouldCommitOnBlur={true}
                // Keeps the text on top of the selected layer
                fontCssProperties={{ zIndex: 2000 }}
              />
            </NodeLabelAtTheBottom>
          )}
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

export const TaskNode = React.memo(
  ({
    data: { bpmnElement: task, shape, shapeIndex },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<
    BpmnDiagramNodeData<
      Normalized<
        ElementFilter<
          Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
          "task" | "scriptTask" | "serviceTask" | "businessRuleTask" | "userTask" | "callActivity"
        >
      >
    >
  >) => {
    const renderCount = useRef<number>(0);
    renderCount.current++;

    const ref = useRef<HTMLDivElement>(null);

    const enableCustomNodeStyles = useBpmnEditorStore((s) => s.diagram.overlays.enableCustomNodeStyles);
    const isHovered = useIsHovered(ref);
    const isResizing = useNodeResizing(id);
    const shouldActLikeHovered = useBpmnEditorStore(
      (s) => (isHovered || isResizing) && s.xyFlowReactKieDiagram.draggingNodes.length === 0
    );

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(id);
    useHoveredNodeAlwaysOnTop(ref, zIndex, shouldActLikeHovered, dragging, selected, isEditingLabel);

    const bpmnEditorStoreApi = useBpmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id, NODE_TYPES, EDGE_TYPES);
    const nodeDimensions = useNodeDimensions({ shape, nodeType: type as BpmnNodeType, MIN_NODE_SIZES });

    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        bpmnEditorStoreApi.setState((state) => {
          updateFlowElement({
            definitions: state.bpmn.model.definitions,
            newFlowElement: { "@_name": newName.trim() },
            id,
          });
        });
      },
      [bpmnEditorStoreApi, id]
    );

    const { fontCssProperties } = useNodeStyle({
      nodeType: type as BpmnNodeType,
      isEnabled: enableCustomNodeStyles,
    });

    const icons = useActivityIcons(task);

    const [isMorphingPanelExpanded, setMorphingPanelExpanded] = useState(false);
    useEffect(() => setMorphingPanelExpanded(false), [isHovered]);
    const morphingActions = useTaskNodeMorphingActions(task);
    const disabledMorphingActionIds = useMemo<Set<Unpacked<typeof morphingActions>["id"]>>(() => new Set(), []);
    useKeyboardShortcutsForMorphingActions(ref, morphingActions, disabledMorphingActionIds);
    const { customTasks } = useCustomTasks();

    const icon = useMemo(() => {
      if (task.__$$element === "task") {
        for (const ct of customTasks ?? []) {
          if (ct.matches(task)) {
            return <>{ct.iconSvgElement}</>;
          }
        }
      }
    }, [customTasks, task]);

    const selectedActionIdForMorphingPanel = useMemo(() => {
      if (task.__$$element === "task") {
        for (const ct of customTasks ?? []) {
          if (ct.matches(task)) {
            return ct.id;
          }
        }
      }

      return task.__$$element;
    }, [customTasks, task]);

    return (
      <>
        <svg className={`xyflow-react-kie-diagram--node-shape ${className} ${selected ? "selected" : ""}`}>
          <TaskNodeSvg
            {...nodeDimensions}
            x={0}
            y={0}
            strokeWidth={task.__$$element === "callActivity" ? 5 : undefined}
            markers={icons}
            variant={task.__$$element}
            icon={icon}
          />
        </svg>
        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />
        <div
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
          className={`kie-bpmn-editor--task-node ${className} kie-bpmn-editor--selected-task-node`}
          ref={ref}
          tabIndex={-1}
          data-nodehref={id}
          data-nodelabel={task["@_name"]}
        >
          {/* {`render count: ${renderCount.current}`}
          <br /> */}
          <div className={"xyflow-react-kie-diagram--node"}>
            <InfoNodePanel
              isVisible={!isMorphingPanelExpanded && !isTargeted && shouldActLikeHovered}
              onClick={useCallback(() => {
                bpmnEditorStoreApi.setState((state) => {
                  state.propertiesPanel.isOpen = true;
                });
              }, [bpmnEditorStoreApi])}
            />

            <EditableNodeLabel
              id={id}
              name={task["@_name"]}
              isEditing={isEditingLabel}
              setEditing={setEditingLabel}
              position={getNodeLabelPosition({ nodeType: type as BpmnNodeType })}
              value={task["@_name"]}
              onChange={setName}
              validate={() => true} // FIXME: Tiago
              shouldCommitOnBlur={true}
              // Keeps the text on top of the selected layer
              fontCssProperties={{ ...fontCssProperties, zIndex: 2000 }}
            />

            {shouldActLikeHovered && (
              <NodeResizerHandle
                nodeType={type as typeof NODE_TYPES.task}
                nodeId={id}
                nodeShapeIndex={shapeIndex}
                MIN_NODE_SIZES={MIN_NODE_SIZES}
              />
            )}

            <OutgoingStuffNodePanel
              nodeMapping={bpmnNodesOutgoingStuffNodePanelMapping}
              edgeMapping={bpmnEdgesOutgoingStuffNodePanelMapping}
              nodeHref={id}
              isVisible={!isMorphingPanelExpanded && !isTargeted && shouldActLikeHovered}
              nodeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.task].nodes}
              edgeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.task].edges}
            />

            <NodeMorphingPanel
              disabledActionIds={disabledMorphingActionIds}
              isToggleVisible={!isTargeted && shouldActLikeHovered}
              isExpanded={isMorphingPanelExpanded}
              setExpanded={setMorphingPanelExpanded}
              actions={morphingActions}
              primaryColor={NODE_COLORS.task.foreground}
              secondaryColor={NODE_COLORS.task.background}
              selectedActionId={selectedActionIdForMorphingPanel}
            />
          </div>
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

export const SubProcessNode = React.memo(
  ({
    data: { bpmnElement: subProcess, shape, shapeIndex },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<
    BpmnDiagramNodeData<Normalized<BPMN20__tSubProcess> & { __$$element: "adHocSubProcess" | "subProcess" }>
  >) => {
    const renderCount = useRef<number>(0);
    renderCount.current++;

    const ref = useRef<HTMLDivElement>(null);
    const interactionRectRef = useRef<SVGRectElement>(null);

    const enableCustomNodeStyles = useBpmnEditorStore((s) => s.diagram.overlays.enableCustomNodeStyles);
    const isOnlySelectedNode = useBpmnEditorStore(
      (s) => s.xyFlowReactKieDiagram._selectedNodes.length === 1 && selected
    );
    const isHovered = useIsHovered(ref);
    const isResizing = useNodeResizing(id);
    const shouldActLikeHovered = useBpmnEditorStore(
      (s) => (isHovered || isResizing) && s.xyFlowReactKieDiagram.draggingNodes.length === 0
    );

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(id);
    useHoveredNodeAlwaysOnTop(ref, zIndex, shouldActLikeHovered, dragging, selected, isEditingLabel);

    const bpmnEditorStoreApi = useBpmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id, NODE_TYPES, EDGE_TYPES);
    const nodeDimensions = useNodeDimensions({ shape, nodeType: type as BpmnNodeType, MIN_NODE_SIZES });

    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        bpmnEditorStoreApi.setState((state) => {
          updateFlowElement({
            definitions: state.bpmn.model.definitions,
            newFlowElement: { "@_name": newName.trim() },
            id,
          });
        });
      },
      [bpmnEditorStoreApi, id]
    );

    const { fontCssProperties } = useNodeStyle({
      nodeType: type as BpmnNodeType,
      isEnabled: enableCustomNodeStyles,
    });

    const icons = useActivityIcons(subProcess);

    const [isMorphingPanelExpanded, setMorphingPanelExpanded] = useState(false);
    useEffect(() => setMorphingPanelExpanded(false), [isHovered]);
    const morphingActions = useSubProcessNodeMorphingActions(subProcess);
    const disabledMorphingActionIds = useMemo<Set<Unpacked<typeof morphingActions>["id"]>>(() => new Set(), []);
    useKeyboardShortcutsForMorphingActions(ref, morphingActions, disabledMorphingActionIds);

    useEffect(() => {
      // Defer focus from interactionRect on
      // `SubProcesNodeSvg` to the
      // `.xyflow-react-kie-diagram--node` div.
      const s = interactionRectRef.current;
      const r = ref.current;
      const f = () => {
        r?.focus();
      };

      s?.addEventListener("focus", f);
      return () => {
        s?.removeEventListener("focus", f);
      };
    }, []);

    return (
      <>
        <svg className={`xyflow-react-kie-diagram--node-shape ${className} ${selected ? "selected" : ""}`}>
          <SubProcessNodeSvg
            {...nodeDimensions}
            ref={interactionRectRef}
            x={0}
            y={0}
            icons={icons}
            variant={
              subProcess["@_triggeredByEvent"]
                ? "event"
                : subProcess.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics"
                  ? "multi-instance"
                  : "other"
            }
          />
        </svg>
        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />
        <div
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
          className={`kie-bpmn-editor--sub-process-node ${className} kie-bpmn-editor--selected-sub-process-node`}
          ref={ref}
          tabIndex={-1}
          data-nodehref={id}
          data-nodelabel={subProcess["@_name"]}
        >
          {/* {`render count: ${renderCount.current}`}
          <br /> */}
          <div className={"xyflow-react-kie-diagram--node"}>
            <InfoNodePanel
              isVisible={!isMorphingPanelExpanded && !isTargeted && isOnlySelectedNode && !dragging}
              onClick={useCallback(() => {
                bpmnEditorStoreApi.setState((state) => {
                  state.propertiesPanel.isOpen = true;
                });
              }, [bpmnEditorStoreApi])}
            />

            <EditableNodeLabel
              id={id}
              name={subProcess["@_name"]}
              isEditing={isEditingLabel}
              setEditing={setEditingLabel}
              position={getNodeLabelPosition({ nodeType: type as BpmnNodeType })}
              value={subProcess["@_name"]}
              onChange={setName}
              validate={() => true} // FIXME: Tiago
              shouldCommitOnBlur={true}
              // Keeps the text on top of the selected layer
              fontCssProperties={{ ...fontCssProperties, zIndex: 2000 }}
            />

            {isOnlySelectedNode && !dragging && (
              <NodeResizerHandle
                nodeType={type as typeof NODE_TYPES.subProcess}
                nodeId={id}
                nodeShapeIndex={shapeIndex}
                MIN_NODE_SIZES={MIN_NODE_SIZES}
              />
            )}

            <OutgoingStuffNodePanel
              nodeMapping={bpmnNodesOutgoingStuffNodePanelMapping}
              edgeMapping={bpmnEdgesOutgoingStuffNodePanelMapping}
              nodeHref={id}
              isVisible={!isMorphingPanelExpanded && !isTargeted && isOnlySelectedNode && !dragging}
              nodeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.subProcess].nodes}
              edgeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.subProcess].edges}
            />

            <NodeMorphingPanel
              disabledActionIds={disabledMorphingActionIds}
              isToggleVisible={!isTargeted && isOnlySelectedNode && !dragging}
              isExpanded={isMorphingPanelExpanded}
              setExpanded={setMorphingPanelExpanded}
              actions={morphingActions}
              primaryColor={NODE_COLORS.subProcess.foreground}
              secondaryColor={NODE_COLORS.subProcess.background}
              selectedActionId={
                subProcess["@_triggeredByEvent"] === true
                  ? "eventSubProcess"
                  : subProcess.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics"
                    ? "multiInstanceSubProcess"
                    : subProcess.__$$element
              }
            />
          </div>
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

export const GatewayNode = React.memo(
  ({
    data: { bpmnElement: gateway, shape, shapeIndex },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<
    BpmnDiagramNodeData<
      Normalized<
        ElementFilter<
          Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
          "exclusiveGateway" | "inclusiveGateway" | "parallelGateway" | "eventBasedGateway" | "complexGateway"
        >
      >
    >
  >) => {
    const renderCount = useRef<number>(0);
    renderCount.current++;

    const ref = useRef<HTMLDivElement>(null);

    const isHovered = useIsHovered(ref);
    const isResizing = useNodeResizing(id);
    const shouldActLikeHovered = useBpmnEditorStore(
      (s) => (isHovered || isResizing) && s.xyFlowReactKieDiagram.draggingNodes.length === 0
    );

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(id);
    useHoveredNodeAlwaysOnTop(ref, zIndex, shouldActLikeHovered, dragging, selected, isEditingLabel);

    const bpmnEditorStoreApi = useBpmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id, NODE_TYPES, EDGE_TYPES);
    const nodeDimensions = useNodeDimensions({ shape, nodeType: type as BpmnNodeType, MIN_NODE_SIZES });

    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        bpmnEditorStoreApi.setState((state) => {
          updateFlowElement({
            definitions: state.bpmn.model.definitions,
            newFlowElement: { "@_name": newName.trim() },
            id,
          });
        });
      },
      [bpmnEditorStoreApi, id]
    );

    const [isMorphingPanelExpanded, setMorphingPanelExpanded] = useState(false);
    useEffect(() => setMorphingPanelExpanded(false), [isHovered]);
    const morphingActions = useGatewayNodeMorphingActions(gateway);
    const disabledMorphingActionIds = useMemo<Set<Unpacked<typeof morphingActions>["id"]>>(() => new Set(), []);
    useKeyboardShortcutsForMorphingActions(ref, morphingActions, disabledMorphingActionIds);

    return (
      <>
        <svg className={`xyflow-react-kie-diagram--node-shape ${className} ${selected ? "selected" : ""}`}>
          <GatewayNodeSvg {...nodeDimensions} x={0} y={0} variant={gateway.__$$element} />
        </svg>
        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />
        <div
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
          className={`kie-bpmn-editor--gateway-node ${className} kie-bpmn-editor--selected-gateway-node`}
          ref={ref}
          tabIndex={-1}
          data-nodehref={id}
          data-nodelabel={gateway["@_name"]}
        >
          {/* {`render count: ${renderCount.current}`}
          <br /> */}
          <div className={"xyflow-react-kie-diagram--node"}>
            <InfoNodePanel
              isVisible={!isMorphingPanelExpanded && !isTargeted && shouldActLikeHovered}
              onClick={useCallback(() => {
                bpmnEditorStoreApi.setState((state) => {
                  state.propertiesPanel.isOpen = true;
                });
              }, [bpmnEditorStoreApi])}
            />

            <OutgoingStuffNodePanel
              nodeMapping={bpmnNodesOutgoingStuffNodePanelMapping}
              edgeMapping={bpmnEdgesOutgoingStuffNodePanelMapping}
              nodeHref={id}
              isVisible={!isMorphingPanelExpanded && !isTargeted && shouldActLikeHovered}
              nodeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.gateway].nodes}
              edgeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.gateway].edges}
            />

            <NodeMorphingPanel
              disabledActionIds={disabledMorphingActionIds}
              isToggleVisible={!isTargeted && shouldActLikeHovered}
              isExpanded={isMorphingPanelExpanded}
              setExpanded={setMorphingPanelExpanded}
              actions={morphingActions}
              primaryColor={NODE_COLORS.gateway.foreground}
              secondaryColor={NODE_COLORS.gateway.background}
              selectedActionId={gateway.__$$element}
            />
          </div>
          {/* Creates a div element with the node size to push down the <EditableNodeLabel /> */}
          {<div style={{ height: nodeDimensions.height, flexShrink: 0 }} />}
          {(gateway["@_name"] || isEditingLabel) && !isMorphingPanelExpanded && (
            <NodeLabelAtTheBottom>
              <EditableNodeLabel
                id={id}
                name={gateway["@_name"]}
                isEditing={isEditingLabel}
                setEditing={setEditingLabel}
                position={getNodeLabelPosition({ nodeType: type as BpmnNodeType })}
                value={gateway["@_name"]}
                onChange={setName}
                validate={() => true} // FIXME: Tiago
                shouldCommitOnBlur={true}
                // Keeps the text on top of the selected layer
                fontCssProperties={{ zIndex: 2000 }}
              />
            </NodeLabelAtTheBottom>
          )}
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

export const DataObjectNode = React.memo(
  ({
    data: { bpmnElement: dataObject, shape, shapeIndex },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<BpmnDiagramNodeData<Normalized<BPMN20__tDataObject> & { __$$element: "dataObject" }>>) => {
    const renderCount = useRef<number>(0);
    renderCount.current++;

    const ref = useRef<HTMLDivElement>(null);

    const isHovered = useIsHovered(ref);
    const isResizing = useNodeResizing(id);
    const shouldActLikeHovered = useBpmnEditorStore(
      (s) => (isHovered || isResizing) && s.xyFlowReactKieDiagram.draggingNodes.length === 0
    );

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(id);
    useHoveredNodeAlwaysOnTop(ref, zIndex, shouldActLikeHovered, dragging, selected, isEditingLabel);

    const bpmnEditorStoreApi = useBpmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id, NODE_TYPES, EDGE_TYPES);
    const nodeDimensions = useNodeDimensions({ shape, nodeType: type as BpmnNodeType, MIN_NODE_SIZES });

    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        bpmnEditorStoreApi.setState((state) => {
          updateFlowElement({
            definitions: state.bpmn.model.definitions,
            newFlowElement: { "@_name": newName.trim() },
            id,
          });
        });
      },
      [bpmnEditorStoreApi, id]
    );

    // This is used to modify a css from a :before element.
    // The --height is a css var which is used by when this node is selected class
    const [nodeHeight, setNodeHeight] = React.useState<number>(0);
    const style = useMemo<React.CSSProperties>(
      () => ({
        display: "flex",
        flexDirection: "column",
        outline: "none",
        "--selected-data-object-node-shape--height": `${nodeDimensions.height + 20 + 26 + (isEditingLabel ? 20 : nodeHeight ?? 0)}px`,
      }),
      [nodeDimensions, isEditingLabel, nodeHeight]
    );

    return (
      <>
        <svg className={`xyflow-react-kie-diagram--node-shape ${className} ${selected ? "selected" : ""}`}>
          <DataObjectNodeSvg {...nodeDimensions} x={0} y={0} showArrow={false} showFoldedPage={true} />
        </svg>
        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />
        <div
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
          style={style}
          className={`kie-bpmn-editor--data-object-node-content ${className} ${selected ? "selected" : ""}`}
          ref={ref}
          tabIndex={-1}
          data-nodehref={id}
          data-nodelabel={dataObject["@_name"]}
        >
          {/* {`render count: ${renderCount.current}`}
          <br /> */}
          <div className={"xyflow-react-kie-diagram--node"}>
            <InfoNodePanel
              isVisible={!isTargeted && shouldActLikeHovered}
              onClick={useCallback(() => {
                bpmnEditorStoreApi.setState((state) => {
                  state.propertiesPanel.isOpen = true;
                });
              }, [bpmnEditorStoreApi])}
            />

            {shouldActLikeHovered && (
              <NodeResizerHandle
                nodeType={type as typeof NODE_TYPES.dataObject}
                nodeId={id}
                nodeShapeIndex={shapeIndex}
                MIN_NODE_SIZES={MIN_NODE_SIZES}
              />
            )}

            <OutgoingStuffNodePanel
              nodeMapping={bpmnNodesOutgoingStuffNodePanelMapping}
              edgeMapping={bpmnEdgesOutgoingStuffNodePanelMapping}
              nodeHref={id}
              isVisible={!isTargeted && shouldActLikeHovered}
              nodeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.dataObject].nodes}
              edgeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.dataObject].edges}
            />
          </div>
          {/* Creates a div element with the node size to push down the <EditableNodeLabel /> */}
          {<div style={{ height: nodeDimensions.height, flexShrink: 0 }} />}
          {(dataObject["@_name"] || isEditingLabel) && (
            <NodeLabelAtTheBottom>
              <EditableNodeLabel
                id={id}
                name={dataObject["@_name"]}
                isEditing={isEditingLabel}
                setEditing={setEditingLabel}
                position={getNodeLabelPosition({ nodeType: type as BpmnNodeType })}
                value={dataObject["@_name"]}
                onChange={setName}
                validate={() => true} // FIXME: Tiago
                shouldCommitOnBlur={true}
                // Keeps the text on top of the selected layer
                fontCssProperties={{ zIndex: 2000 }}
                setLabelHeight={setNodeHeight}
              />
            </NodeLabelAtTheBottom>
          )}
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

export const GroupNode = React.memo(
  ({
    data: { bpmnElement: group, shape, shapeIndex },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<BpmnDiagramNodeData<Normalized<BPMN20__tGroup> & { __$$element: "group" }>>) => {
    const renderCount = useRef<number>(0);
    renderCount.current++;

    const ref = useRef<SVGRectElement>(null);

    const isHovered = useIsHovered(ref);
    const isResizing = useNodeResizing(id);
    const shouldActLikeHovered = useBpmnEditorStore(
      (s) => (isHovered || isResizing) && s.xyFlowReactKieDiagram.draggingNodes.length === 0
    );
    const bpmnEditorStoreApi = useBpmnEditorStoreApi();
    const reactFlow = RF.useReactFlow<BpmnDiagramNodeData, BpmnDiagramEdgeData>();

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id, NODE_TYPES, EDGE_TYPES, true);
    const nodeDimensions = useNodeDimensions({ shape, nodeType: type as BpmnNodeType, MIN_NODE_SIZES });

    // Select nodes that are visually entirely inside the group.
    useEffect(() => {
      const onDoubleClick = () => {
        bpmnEditorStoreApi.setState((state) => {
          state.xyFlowReactKieDiagram._selectedNodes = reactFlow.getNodes().flatMap((n) =>
            getContainmentRelationship({
              bounds: n.data.shape["dc:Bounds"]!,
              container: shape["dc:Bounds"]!,
              snapGrid: state.xyFlowReactKieDiagram.snapGrid,
              containerMinSizes: MIN_NODE_SIZES[NODE_TYPES.group],
              boundsMinSizes: MIN_NODE_SIZES[n.type as BpmnNodeType],
              borderAllowanceInPx: 0, // We only care about nodes that are completelyInside the Group node.
            }).isCompletelyInside
              ? [n.id]
              : []
          );
        });
      };

      const r = ref.current;
      r?.addEventListener("dblclick", onDoubleClick);
      return () => {
        r?.removeEventListener("dblclick", onDoubleClick);
      };
    }, [bpmnEditorStoreApi, reactFlow, shape]);

    return (
      <>
        <svg className={`xyflow-react-kie-diagram--node-shape ${className}`}>
          <GroupNodeSvg ref={ref} {...nodeDimensions} x={0} y={0} strokeWidth={3} />
        </svg>

        <div
          className={`xyflow-react-kie-diagram--node kie-bpmn-editor--group-node ${className}`}
          tabIndex={-1}
          data-nodehref={id}
          data-nodelabel={id}
        >
          {/* {`render count: ${renderCount.current}`}
          <br /> */}

          {selected && !dragging && (
            <NodeResizerHandle
              nodeType={type as typeof NODE_TYPES.group}
              nodeId={id}
              nodeShapeIndex={shapeIndex}
              MIN_NODE_SIZES={MIN_NODE_SIZES}
            />
          )}

          <OutgoingStuffNodePanel
            nodeMapping={bpmnNodesOutgoingStuffNodePanelMapping}
            edgeMapping={bpmnEdgesOutgoingStuffNodePanelMapping}
            nodeHref={id}
            isVisible={!isTargeted && selected && !dragging}
            nodeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.group].nodes}
            edgeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.group].edges}
          />
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

export const LaneNode = React.memo(
  ({
    data: { bpmnElement: lane, shape, shapeIndex },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<BpmnDiagramNodeData<Normalized<BPMN20__tLane> & { __$$element: "lane" }>>) => {
    const renderCount = useRef<number>(0);
    renderCount.current++;

    const ref = useRef<SVGRectElement>(null);

    const isOnlySelectedNode = useBpmnEditorStore(
      (s) => s.xyFlowReactKieDiagram._selectedNodes.length === 1 && selected
    );
    const isHovered = useIsHovered(ref);
    const isResizing = useNodeResizing(id);
    const shouldActLikeHovered = useBpmnEditorStore(
      (s) => (isHovered || isResizing) && s.xyFlowReactKieDiagram.draggingNodes.length === 0
    );

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(id);
    useHoveredNodeAlwaysOnTop(ref, zIndex, shouldActLikeHovered, dragging, selected, isEditingLabel);

    const bpmnEditorStoreApi = useBpmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id, NODE_TYPES, EDGE_TYPES);
    const nodeDimensions = useNodeDimensions({ shape, nodeType: type as BpmnNodeType, MIN_NODE_SIZES });

    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        bpmnEditorStoreApi.setState((state) => {
          updateLane({ definitions: state.bpmn.model.definitions, newLane: { "@_name": newName.trim() }, id });
        });
      },
      [bpmnEditorStoreApi, id]
    );

    return (
      <>
        <svg className={`xyflow-react-kie-diagram--node-shape ${className} ${selected ? "selected" : ""}`}>
          <LaneNodeSvg {...nodeDimensions} x={0} y={0} ref={ref} />
        </svg>
        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />
        <div
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
          className={`kie-bpmn-editor--lane-node ${className} kie-bpmn-editor--selected-lane-node`}
          tabIndex={-1}
          data-nodehref={id}
          data-nodelabel={lane["@_name"]}
        >
          {/* {`render count: ${renderCount.current}`}
          <br /> */}
          <div className={"xyflow-react-kie-diagram--node"}>
            <InfoNodePanel
              isVisible={!isTargeted && isOnlySelectedNode && !dragging}
              onClick={useCallback(() => {
                bpmnEditorStoreApi.setState((state) => {
                  state.propertiesPanel.isOpen = true;
                });
              }, [bpmnEditorStoreApi])}
            />

            <EditableNodeLabel
              id={id}
              name={lane["@_name"]}
              isEditing={isEditingLabel}
              setEditing={setEditingLabel}
              position={getNodeLabelPosition({ nodeType: type as BpmnNodeType })}
              value={lane["@_name"]}
              onChange={setName}
              validate={() => true} // FIXME: Tiago
              shouldCommitOnBlur={true}
              // Keeps the text on top of the selected layer
              fontCssProperties={{ zIndex: 2000 }}
            />

            {isOnlySelectedNode && !dragging && (
              <NodeResizerHandle
                nodeType={type as typeof NODE_TYPES.lane}
                nodeId={id}
                nodeShapeIndex={shapeIndex}
                MIN_NODE_SIZES={MIN_NODE_SIZES}
              />
            )}

            <OutgoingStuffNodePanel
              nodeMapping={bpmnNodesOutgoingStuffNodePanelMapping}
              edgeMapping={bpmnEdgesOutgoingStuffNodePanelMapping}
              nodeHref={id}
              isVisible={!isTargeted && isOnlySelectedNode && !dragging}
              nodeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.lane].nodes}
              edgeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.lane].edges}
            />
          </div>
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

export const TextAnnotationNode = React.memo(
  ({
    data: { bpmnElement: textAnnotation, shape, shapeIndex },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<BpmnDiagramNodeData<Normalized<BPMN20__tTextAnnotation> & { __$$element: "textAnnotation" }>>) => {
    const renderCount = useRef<number>(0);
    renderCount.current++;

    const ref = useRef<HTMLDivElement>(null);

    const enableCustomNodeStyles = useBpmnEditorStore((s) => s.diagram.overlays.enableCustomNodeStyles);
    const isHovered = useIsHovered(ref);
    const isResizing = useNodeResizing(id);
    const shouldActLikeHovered = useBpmnEditorStore(
      (s) => (isHovered || isResizing) && s.xyFlowReactKieDiagram.draggingNodes.length === 0
    );

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(id);
    useHoveredNodeAlwaysOnTop(ref, zIndex, shouldActLikeHovered, dragging, selected, isEditingLabel);

    const bpmnEditorStoreApi = useBpmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id, NODE_TYPES, EDGE_TYPES);
    const nodeDimensions = useNodeDimensions({
      nodeType: type as typeof NODE_TYPES.textAnnotation,
      shape,
      MIN_NODE_SIZES,
    });

    const setText = useCallback(
      (newText: string) => {
        bpmnEditorStoreApi.setState((state) => {
          updateTextAnnotation({
            definitions: state.bpmn.model.definitions,
            id,
            newTextAnnotation: { text: { __$$text: newText } },
          });
        });
      },
      [bpmnEditorStoreApi, id]
    );

    const { fontCssProperties } = useNodeStyle({
      nodeType: type as BpmnNodeType,
      isEnabled: enableCustomNodeStyles,
    });

    const content = useMemo(() => String(textAnnotation.text?.__$$text ?? ""), [textAnnotation]);

    return (
      <>
        <svg className={`xyflow-react-kie-diagram--node-shape ${className}`}>
          <TextAnnotationNodeSvg {...nodeDimensions} x={0} y={0} />
        </svg>

        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />

        <div
          ref={ref}
          className={`xyflow-react-kie-diagram--node kie-bpmn-editor--text-annotation-node ${className}`}
          tabIndex={-1}
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
          data-nodehref={id}
          data-nodelabel={String(textAnnotation.text)}
        >
          {/* {`render count: ${renderCount.current}`}
          <br /> */}
          <InfoNodePanel
            isVisible={!isTargeted && shouldActLikeHovered}
            onClick={useCallback(() => {
              bpmnEditorStoreApi.setState((state) => {
                state.propertiesPanel.isOpen = true;
              });
            }, [bpmnEditorStoreApi])}
          />

          <EditableNodeLabel
            id={id}
            name={content}
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            position={getNodeLabelPosition({ nodeType: type as BpmnNodeType })}
            value={content}
            onChange={setText}
            validate={() => true} // FIXME: Tiago
            shouldCommitOnBlur={true}
            // Keeps the text on top of the selected layer
            fontCssProperties={{ ...fontCssProperties, zIndex: 2000 }}
          />

          {shouldActLikeHovered && (
            <NodeResizerHandle
              nodeType={type as typeof NODE_TYPES.textAnnotation}
              nodeId={id}
              nodeShapeIndex={shapeIndex}
              MIN_NODE_SIZES={MIN_NODE_SIZES}
            />
          )}

          <OutgoingStuffNodePanel
            nodeMapping={bpmnNodesOutgoingStuffNodePanelMapping}
            edgeMapping={bpmnEdgesOutgoingStuffNodePanelMapping}
            nodeHref={id}
            isVisible={!isTargeted && shouldActLikeHovered}
            nodeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.textAnnotation].nodes}
            edgeTypes={BPMN_OUTGOING_STRUCTURE[NODE_TYPES.textAnnotation].edges}
          />
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

export const UnknownNode = React.memo(
  ({ data: { shape, shapeIndex }, selected, dragging, zIndex, type, id }: RF.NodeProps<BpmnDiagramNodeData<any>>) => {
    const renderCount = useRef<number>(0);
    renderCount.current++;

    const ref = useRef<HTMLDivElement>(null);

    const isHovered = useIsHovered(ref);
    const isResizing = useNodeResizing(id);
    const shouldActLikeHovered = useBpmnEditorStore(
      (s) => (isHovered || isResizing) && s.xyFlowReactKieDiagram.draggingNodes.length === 0
    );

    const bpmnEditorStoreApi = useBpmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id, NODE_TYPES, EDGE_TYPES);
    const nodeDimensions = useNodeDimensions({
      nodeType: type as typeof NODE_TYPES.unknown,
      shape,
      MIN_NODE_SIZES,
    });

    return (
      <>
        <svg className={`xyflow-react-kie-diagram--node-shape ${className}`}>
          <UnknownNodeSvg {...nodeDimensions} x={0} y={0} />
        </svg>

        <RF.Handle key={"unknown"} id={"unknown"} type={"source"} style={{ opacity: 0 }} position={RF.Position.Top} />

        <div
          ref={ref}
          className={`xyflow-react-kie-diagram--node kie-bpmn-editor--unknown-node ${className}`}
          tabIndex={-1}
          data-nodehref={id}
        >
          {/* {`render count: ${renderCount.current}`}
          <br /> */}
          <InfoNodePanel
            isVisible={!isTargeted && shouldActLikeHovered}
            onClick={useCallback(() => {
              bpmnEditorStoreApi.setState((state) => {
                state.propertiesPanel.isOpen = true;
              });
            }, [bpmnEditorStoreApi])}
          />

          <EditableNodeLabel
            id={id}
            name={undefined}
            position={getNodeLabelPosition({ nodeType: type as typeof NODE_TYPES.unknown })}
            isEditing={false}
            setEditing={() => {}}
            value={`? `}
            onChange={() => {}}
            skipValidation={false}
            validate={useCallback((value) => true, [])}
            shouldCommitOnBlur={true}
          />
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

export function useActivityIcons(
  activity: ElementFilter<
    Unpacked<Normalized<BPMN20__tProcess>["flowElement"]>,
    | "adHocSubProcess"
    | "subProcess"
    | "task"
    | "serviceTask"
    | "userTask"
    | "businessRuleTask"
    | "scriptTask"
    | "callActivity"
  >
) {
  return useMemo(() => {
    const icons: ActivityNodeMarker[] = [];
    if (activity.__$$element === "adHocSubProcess") {
      icons.push(ActivityNodeMarker.AdHocSubProcess);
    }

    if (activity["@_isForCompensation"]) {
      icons.push(ActivityNodeMarker.Compensation);
    }

    if (activity.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics") {
      icons.push(
        activity.loopCharacteristics["@_isSequential"]
          ? ActivityNodeMarker.MultiInstanceSequential
          : ActivityNodeMarker.MultiInstanceParallel
      );
    }

    if (activity.loopCharacteristics?.__$$element === "standardLoopCharacteristics") {
      icons.push(ActivityNodeMarker.Loop);
    }

    if (activity.__$$element === "callActivity") {
      icons.push(ActivityNodeMarker.Collapsed);
    }

    return icons;
  }, [activity]);
}

export function NodeLabelAtTheBottom({ children }: React.PropsWithChildren<{}>) {
  return <div className={"kie-bpmn-editor--floating-node-label"}>{children}</div>;
}
