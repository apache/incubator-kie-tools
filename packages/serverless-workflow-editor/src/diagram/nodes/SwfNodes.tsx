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

import { Specification } from "@serverlessworkflow/sdk-typescript";
import * as React from "react";
import { useCallback, useLayoutEffect, useMemo, useRef } from "react";
import * as RF from "reactflow";
import { renameElement } from "../../mutations/renameNode";
import { useSwfEditorStore, useSwfEditorStoreApi } from "../../store/StoreContext";
import { Unpacked } from "../../tsExt/tsExt";
import { PositionalNodeHandles } from "../connections/PositionalNodeHandles";
import { NodeType, containment, outgoingStructure } from "../connections/graphStructure";
import { EDGE_TYPES } from "../edges/SwfEdgeTypes";
import { useIsHovered } from "../useIsHovered";
import { DEFAULT_NODE_SIZES } from "./SwfDefaultSizes";
import { EditableNodeLabel, OnEditableNodeLabelChange, useEditableNodeLabel } from "./EditableNodeLabel";
import { getNodeLabelPosition } from "./NodeStyle";
import {
  CallbackstateSvg,
  EventstateSvg,
  ForeachstateSvg,
  InjectstateSvg,
  OperationstateSvg,
  ParallelstateSvg,
  SleepstateSvg,
  SwitchstateSvg,
  UnknownNodeSvg,
} from "./SwfNodeSvgs";
import { NODE_TYPES } from "./SwfNodeTypes";
import { OutgoingStuffNodePanel } from "./OutgoingStuffNodePanel";
import { propsHaveSameValuesDeep } from "../memoization/memoization";
import { useSwfEditorI18n } from "../../i18n";

export type ElementFilter<E extends { __$$element: string }, Filter extends string> = E extends any
  ? E["__$$element"] extends Filter
    ? E
    : never
  : never;

export type NodeSwfObjects = null | Unpacked<Specification.States>;

export type SwfDiagramNodeData<T extends NodeSwfObjects = NodeSwfObjects> = {
  swfObject: T;
  index: number;
  /**
   * We don't use Reactflow's parenting mechanism because it is
   * too opinionated on how it deletes nodes/edges that are
   * inside/connected to nodes with parents
   * */
  parentRfNode: RF.Node<SwfDiagramNodeData> | undefined;
};

//Specification.IEventstate;
export const EventState = React.memo(
  ({
    data: { swfObject: eventstate, index, parentRfNode },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<SwfDiagramNodeData<Specification.IEventstate & { __$$element: "eventstate" }>>) => {
    const ref = useRef<HTMLDivElement>(null);

    const snapGrid = useSwfEditorStore((s) => s.diagram.snapGrid);
    const isHovered = useIsHovered(ref);
    const shouldActLikeHovered = useSwfEditorStore((s) => isHovered && s.diagram.draggingNodes.length === 0);

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(id);
    useHoveredNodeAlwaysOnTop(ref, zIndex, shouldActLikeHovered, dragging, selected, isEditingLabel);

    const swfEditorStoreApi = useSwfEditorStoreApi();

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id);

    // use default node sizes
    const nodeDimensions = DEFAULT_NODE_SIZES[NODE_TYPES.eventState]({ snapGrid });

    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        swfEditorStoreApi.setState((state) => {
          renameElement({ definitions: state.swf.model.states, newName, index });
        });
      },
      [swfEditorStoreApi, index]
    );

    return (
      <>
        <svg className={`kie-swf-editor--node-shape ${className} ${selected ? "selected" : ""}`}>
          {
            <EventstateSvg
              {...nodeDimensions}
              x={0}
              y={0}
              strokeWidth={undefined}
              fillColor={undefined}
              strokeColor={undefined}
            />
          }
        </svg>
        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />
        <div
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
          className={`kie-swf-editor--generic-node ${className}`}
          ref={ref}
          tabIndex={-1}
          data-nodehref={id}
          data-nodelabel={eventstate["name"]}
        >
          <div className={`kie-swf-editor--node `}>
            <OutgoingStuffNodePanel
              nodeHref={id}
              isVisible={false}
              nodeTypes={outgoingStructure[NODE_TYPES.eventState].nodes}
              edgeTypes={outgoingStructure[NODE_TYPES.eventState].edges}
            />
            {
              <EditableNodeLabel
                id={id}
                namedElement={eventstate}
                isEditing={isEditingLabel}
                setEditing={setEditingLabel}
                position={getNodeLabelPosition({
                  nodeType: type as typeof NODE_TYPES.eventState,
                })}
                value={eventstate["name"]}
                onChange={setName}
                shouldCommitOnBlur={true}
              />
            }
          </div>
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

//Specification.IOperationstate;
export const OperationState = React.memo(
  ({
    data: { swfObject: operationstate, index, parentRfNode },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<SwfDiagramNodeData<Specification.IOperationstate & { __$$element: "operationstate" }>>) => {
    const ref = useRef<HTMLDivElement>(null);

    const snapGrid = useSwfEditorStore((s) => s.diagram.snapGrid);
    const isHovered = useIsHovered(ref);
    const shouldActLikeHovered = useSwfEditorStore((s) => isHovered && s.diagram.draggingNodes.length === 0);

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(id);
    useHoveredNodeAlwaysOnTop(ref, zIndex, shouldActLikeHovered, dragging, selected, isEditingLabel);

    const swfEditorStoreApi = useSwfEditorStoreApi();

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id);

    // use default node sizes
    const nodeDimensions = DEFAULT_NODE_SIZES[NODE_TYPES.operationState]({ snapGrid });

    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        swfEditorStoreApi.setState((state) => {
          renameElement({ definitions: state.swf.model.states, newName, index });
        });
      },
      [swfEditorStoreApi, index]
    );

    return (
      <>
        <svg className={`kie-swf-editor--node-shape ${className} ${selected ? "selected" : ""}`}>
          {
            <OperationstateSvg
              {...nodeDimensions}
              x={0}
              y={0}
              strokeWidth={undefined}
              fillColor={undefined}
              strokeColor={undefined}
            />
          }
        </svg>
        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />
        <div
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
          className={`kie-swf-editor--generic-node ${className}`}
          ref={ref}
          tabIndex={-1}
          data-nodehref={id}
          data-nodelabel={operationstate["name"]}
        >
          <div className={`kie-swf-editor--node `}>
            <OutgoingStuffNodePanel
              nodeHref={id}
              isVisible={false}
              nodeTypes={outgoingStructure[NODE_TYPES.operationState].nodes}
              edgeTypes={outgoingStructure[NODE_TYPES.operationState].edges}
            />
            {
              <EditableNodeLabel
                id={id}
                namedElement={operationstate}
                isEditing={isEditingLabel}
                setEditing={setEditingLabel}
                position={getNodeLabelPosition({
                  nodeType: type as typeof NODE_TYPES.operationState,
                })}
                value={operationstate["name"]}
                onChange={setName}
                shouldCommitOnBlur={true}
              />
            }
          </div>
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

//Specification.Switchstate;
export const SwitchState = React.memo(
  ({
    data: { swfObject: switchstate, index, parentRfNode },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<SwfDiagramNodeData<Specification.Switchstate & { __$$element: "switchstate" }>>) => {
    const ref = useRef<HTMLDivElement>(null);

    const snapGrid = useSwfEditorStore((s) => s.diagram.snapGrid);
    const isHovered = useIsHovered(ref);
    const shouldActLikeHovered = useSwfEditorStore((s) => isHovered && s.diagram.draggingNodes.length === 0);

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(id);
    useHoveredNodeAlwaysOnTop(ref, zIndex, shouldActLikeHovered, dragging, selected, isEditingLabel);

    const swfEditorStoreApi = useSwfEditorStoreApi();

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id);

    // use default node sizes
    const nodeDimensions = DEFAULT_NODE_SIZES[NODE_TYPES.switchState]({ snapGrid });

    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        swfEditorStoreApi.setState((state) => {
          renameElement({ definitions: state.swf.model.states, newName, index });
        });
      },
      [swfEditorStoreApi, index]
    );

    return (
      <>
        <svg className={`kie-swf-editor--node-shape ${className} ${selected ? "selected" : ""}`}>
          {
            <SwitchstateSvg
              {...nodeDimensions}
              x={0}
              y={0}
              strokeWidth={undefined}
              fillColor={undefined}
              strokeColor={undefined}
            />
          }
        </svg>
        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />
        <div
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
          className={`kie-swf-editor--generic-node ${className}`}
          ref={ref}
          tabIndex={-1}
          data-nodehref={id}
          data-nodelabel={switchstate["name"]}
        >
          <div className={`kie-swf-editor--node `}>
            <OutgoingStuffNodePanel
              nodeHref={id}
              isVisible={false}
              nodeTypes={outgoingStructure[NODE_TYPES.switchState].nodes}
              edgeTypes={outgoingStructure[NODE_TYPES.switchState].edges}
            />
            {
              <EditableNodeLabel
                id={id}
                namedElement={switchstate}
                isEditing={isEditingLabel}
                setEditing={setEditingLabel}
                position={getNodeLabelPosition({
                  nodeType: type as typeof NODE_TYPES.switchState,
                })}
                value={switchstate["name"]}
                onChange={setName}
                shouldCommitOnBlur={true}
              />
            }
          </div>
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

//Specification.ISleepstate;
export const SleepState = React.memo(
  ({
    data: { swfObject: sleepstate, index, parentRfNode },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<SwfDiagramNodeData<Specification.ISleepstate & { __$$element: "sleepstate" }>>) => {
    const ref = useRef<HTMLDivElement>(null);

    const snapGrid = useSwfEditorStore((s) => s.diagram.snapGrid);
    const isHovered = useIsHovered(ref);
    const shouldActLikeHovered = useSwfEditorStore((s) => isHovered && s.diagram.draggingNodes.length === 0);

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(id);
    useHoveredNodeAlwaysOnTop(ref, zIndex, shouldActLikeHovered, dragging, selected, isEditingLabel);

    const swfEditorStoreApi = useSwfEditorStoreApi();

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id);

    // use default node sizes
    const nodeDimensions = DEFAULT_NODE_SIZES[NODE_TYPES.sleepState]({ snapGrid });

    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        swfEditorStoreApi.setState((state) => {
          renameElement({ definitions: state.swf.model.states, newName, index });
        });
      },
      [swfEditorStoreApi, index]
    );

    return (
      <>
        <svg className={`kie-swf-editor--node-shape ${className} ${selected ? "selected" : ""}`}>
          {
            <SleepstateSvg
              {...nodeDimensions}
              x={0}
              y={0}
              strokeWidth={undefined}
              fillColor={undefined}
              strokeColor={undefined}
            />
          }
        </svg>
        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />
        <div
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
          className={`kie-swf-editor--generic-node ${className}`}
          ref={ref}
          tabIndex={-1}
          data-nodehref={id}
          data-nodelabel={sleepstate["name"]}
        >
          <div className={`kie-swf-editor--node `}>
            <OutgoingStuffNodePanel
              nodeHref={id}
              isVisible={false}
              nodeTypes={outgoingStructure[NODE_TYPES.sleepState].nodes}
              edgeTypes={outgoingStructure[NODE_TYPES.sleepState].edges}
            />
            {
              <EditableNodeLabel
                id={id}
                namedElement={sleepstate}
                isEditing={isEditingLabel}
                setEditing={setEditingLabel}
                position={getNodeLabelPosition({
                  nodeType: type as typeof NODE_TYPES.sleepState,
                })}
                value={sleepstate["name"]}
                onChange={setName}
                shouldCommitOnBlur={true}
              />
            }
          </div>
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

//Specification.IParallelstate;
export const ParallelState = React.memo(
  ({
    data: { swfObject: parallelstate, index, parentRfNode },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<SwfDiagramNodeData<Specification.IParallelstate & { __$$element: "parallelstate" }>>) => {
    const ref = useRef<HTMLDivElement>(null);

    const snapGrid = useSwfEditorStore((s) => s.diagram.snapGrid);
    const isHovered = useIsHovered(ref);
    const shouldActLikeHovered = useSwfEditorStore((s) => isHovered && s.diagram.draggingNodes.length === 0);

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(id);
    useHoveredNodeAlwaysOnTop(ref, zIndex, shouldActLikeHovered, dragging, selected, isEditingLabel);

    const swfEditorStoreApi = useSwfEditorStoreApi();

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id);

    // use default node sizes
    const nodeDimensions = DEFAULT_NODE_SIZES[NODE_TYPES.parallelState]({ snapGrid });

    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        swfEditorStoreApi.setState((state) => {
          renameElement({ definitions: state.swf.model.states, newName, index });
        });
      },
      [swfEditorStoreApi, index]
    );

    return (
      <>
        <svg className={`kie-swf-editor--node-shape ${className} ${selected ? "selected" : ""}`}>
          {
            <ParallelstateSvg
              {...nodeDimensions}
              x={0}
              y={0}
              strokeWidth={undefined}
              fillColor={undefined}
              strokeColor={undefined}
            />
          }
        </svg>
        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />
        <div
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
          className={`kie-swf-editor--generic-node ${className}`}
          ref={ref}
          tabIndex={-1}
          data-nodehref={id}
          data-nodelabel={parallelstate["name"]}
        >
          <div className={`kie-swf-editor--node `}>
            <OutgoingStuffNodePanel
              nodeHref={id}
              isVisible={false}
              nodeTypes={outgoingStructure[NODE_TYPES.parallelState].nodes}
              edgeTypes={outgoingStructure[NODE_TYPES.parallelState].edges}
            />
            {
              <EditableNodeLabel
                id={id}
                namedElement={parallelstate}
                isEditing={isEditingLabel}
                setEditing={setEditingLabel}
                position={getNodeLabelPosition({
                  nodeType: type as typeof NODE_TYPES.parallelState,
                })}
                value={parallelstate["name"]}
                onChange={setName}
                shouldCommitOnBlur={true}
              />
            }
          </div>
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

//Specification.IInjectstate;
export const InjectState = React.memo(
  ({
    data: { swfObject: injectstate, index, parentRfNode },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<SwfDiagramNodeData<Specification.IInjectstate & { __$$element: "injectstate" }>>) => {
    const ref = useRef<HTMLDivElement>(null);

    const snapGrid = useSwfEditorStore((s) => s.diagram.snapGrid);
    const isHovered = useIsHovered(ref);
    const shouldActLikeHovered = useSwfEditorStore((s) => isHovered && s.diagram.draggingNodes.length === 0);

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(id);
    useHoveredNodeAlwaysOnTop(ref, zIndex, shouldActLikeHovered, dragging, selected, isEditingLabel);

    const swfEditorStoreApi = useSwfEditorStoreApi();

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id);

    // use default node sizes
    const nodeDimensions = DEFAULT_NODE_SIZES[NODE_TYPES.injectState]({ snapGrid });

    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        swfEditorStoreApi.setState((state) => {
          renameElement({ definitions: state.swf.model.states, newName, index });
        });
      },
      [swfEditorStoreApi, index]
    );

    return (
      <>
        <svg className={`kie-swf-editor--node-shape ${className} ${selected ? "selected" : ""}`}>
          {
            <InjectstateSvg
              {...nodeDimensions}
              x={0}
              y={0}
              strokeWidth={undefined}
              fillColor={undefined}
              strokeColor={undefined}
            />
          }
        </svg>
        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />
        <div
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
          className={`kie-swf-editor--generic-node ${className}`}
          ref={ref}
          tabIndex={-1}
          data-nodehref={id}
          data-nodelabel={injectstate["name"]}
        >
          <div className={`kie-swf-editor--node `}>
            <OutgoingStuffNodePanel
              nodeHref={id}
              isVisible={false}
              nodeTypes={outgoingStructure[NODE_TYPES.injectState].nodes}
              edgeTypes={outgoingStructure[NODE_TYPES.injectState].edges}
            />
            {
              <EditableNodeLabel
                id={id}
                namedElement={injectstate}
                isEditing={isEditingLabel}
                setEditing={setEditingLabel}
                position={getNodeLabelPosition({
                  nodeType: type as typeof NODE_TYPES.injectState,
                })}
                value={injectstate["name"]}
                onChange={setName}
                shouldCommitOnBlur={true}
              />
            }
          </div>
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

//Specification.IForeachstate;
export const ForEachState = React.memo(
  ({
    data: { swfObject: foreachstate, index, parentRfNode },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<SwfDiagramNodeData<Specification.IForeachstate & { __$$element: "foreachstate" }>>) => {
    const ref = useRef<HTMLDivElement>(null);

    const snapGrid = useSwfEditorStore((s) => s.diagram.snapGrid);
    const isHovered = useIsHovered(ref);
    const shouldActLikeHovered = useSwfEditorStore((s) => isHovered && s.diagram.draggingNodes.length === 0);

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(id);
    useHoveredNodeAlwaysOnTop(ref, zIndex, shouldActLikeHovered, dragging, selected, isEditingLabel);

    const swfEditorStoreApi = useSwfEditorStoreApi();

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id);

    // use default node sizes
    const nodeDimensions = DEFAULT_NODE_SIZES[NODE_TYPES.foreachState]({ snapGrid });

    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        swfEditorStoreApi.setState((state) => {
          renameElement({ definitions: state.swf.model.states, newName, index });
        });
      },
      [swfEditorStoreApi, index]
    );

    return (
      <>
        <svg className={`kie-swf-editor--node-shape ${className} ${selected ? "selected" : ""}`}>
          {
            <ForeachstateSvg
              {...nodeDimensions}
              x={0}
              y={0}
              strokeWidth={undefined}
              fillColor={undefined}
              strokeColor={undefined}
            />
          }
        </svg>
        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />
        <div
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
          className={`kie-swf-editor--generic-node ${className}`}
          ref={ref}
          tabIndex={-1}
          data-nodehref={id}
          data-nodelabel={foreachstate["name"]}
        >
          <div className={`kie-swf-editor--node `}>
            <OutgoingStuffNodePanel
              nodeHref={id}
              isVisible={false}
              nodeTypes={outgoingStructure[NODE_TYPES.foreachState].nodes}
              edgeTypes={outgoingStructure[NODE_TYPES.foreachState].edges}
            />
            {
              <EditableNodeLabel
                id={id}
                namedElement={foreachstate}
                isEditing={isEditingLabel}
                setEditing={setEditingLabel}
                position={getNodeLabelPosition({
                  nodeType: type as typeof NODE_TYPES.foreachState,
                })}
                value={foreachstate["name"]}
                onChange={setName}
                shouldCommitOnBlur={true}
              />
            }
          </div>
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

//Specification.ICallbackstate;
export const CallbackState = React.memo(
  ({
    data: { swfObject: callbackstate, index, parentRfNode },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<SwfDiagramNodeData<Specification.ICallbackstate & { __$$element: "callbackstate" }>>) => {
    const ref = useRef<HTMLDivElement>(null);

    const snapGrid = useSwfEditorStore((s) => s.diagram.snapGrid);
    const isHovered = useIsHovered(ref);
    const shouldActLikeHovered = useSwfEditorStore((s) => isHovered && s.diagram.draggingNodes.length === 0);

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(id);
    useHoveredNodeAlwaysOnTop(ref, zIndex, shouldActLikeHovered, dragging, selected, isEditingLabel);

    const swfEditorStoreApi = useSwfEditorStoreApi();

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id);

    // use default node sizes
    const nodeDimensions = DEFAULT_NODE_SIZES[NODE_TYPES.callbackState]({ snapGrid });

    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        swfEditorStoreApi.setState((state) => {
          renameElement({ definitions: state.swf.model.states, newName, index });
        });
      },
      [swfEditorStoreApi, index]
    );

    return (
      <>
        <svg className={`kie-swf-editor--node-shape ${className} ${selected ? "selected" : ""}`}>
          {
            <CallbackstateSvg
              {...nodeDimensions}
              x={0}
              y={0}
              strokeWidth={undefined}
              fillColor={undefined}
              strokeColor={undefined}
            />
          }
        </svg>
        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />
        <div
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
          className={`kie-swf-editor--generic-node ${className}`}
          ref={ref}
          tabIndex={-1}
          data-nodehref={id}
          data-nodelabel={callbackstate["name"]}
        >
          <div className={`kie-swf-editor--node `}>
            <OutgoingStuffNodePanel
              nodeHref={id}
              isVisible={false}
              nodeTypes={outgoingStructure[NODE_TYPES.callbackState].nodes}
              edgeTypes={outgoingStructure[NODE_TYPES.callbackState].edges}
            />
            {
              <EditableNodeLabel
                id={id}
                namedElement={callbackstate}
                isEditing={isEditingLabel}
                setEditing={setEditingLabel}
                position={getNodeLabelPosition({
                  nodeType: type as typeof NODE_TYPES.callbackState,
                })}
                value={callbackstate["name"]}
                onChange={setName}
                shouldCommitOnBlur={true}
              />
            }
          </div>
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

export const UnknownNode = React.memo(
  ({ data: { index }, selected, dragging, zIndex, type, id }: RF.NodeProps<SwfDiagramNodeData<null>>) => {
    const ref = useRef<HTMLDivElement>(null);

    const snapGrid = useSwfEditorStore((s) => s.diagram.snapGrid);
    const isHovered = useIsHovered(ref);
    const shouldActLikeHovered = useSwfEditorStore((s) => isHovered && s.diagram.draggingNodes.length === 0);

    const { isTargeted, isValidConnectionTarget } = useConnectionTargetStatus(id, shouldActLikeHovered);
    const className = useNodeClassName(isValidConnectionTarget, id);

    // use default node sizes
    const nodeDimensions = DEFAULT_NODE_SIZES[NODE_TYPES.unknown]({ snapGrid });

    return (
      <>
        <svg className={`kie-swf-editor--node-shape ${className}`}>
          <UnknownNodeSvg {...nodeDimensions} x={0} y={0} />
        </svg>

        <RF.Handle key={"unknown"} id={"unknown"} type={"source"} style={{ opacity: 0 }} position={RF.Position.Top} />

        <div
          ref={ref}
          className={`kie-swf-editor--node kie-swf-editor--unknown-node ${className}`}
          tabIndex={-1}
          data-nodehref={id}
        >
          {/* {`render count: ${renderCount.current}`}
          <br /> */}
          <EditableNodeLabel
            id={id}
            namedElement={undefined}
            position={getNodeLabelPosition({ nodeType: type as typeof NODE_TYPES.unknown })}
            isEditing={false}
            setEditing={() => {}}
            value={`? `}
            onChange={() => {}}
            skipValidation={false}
            shouldCommitOnBlur={true}
          />
        </div>
      </>
    );
  },
  propsHaveSameValuesDeep
);

///

export function EmptyLabel() {
  const { i18n } = useSwfEditorI18n();
  return (
    <span style={{ fontFamily: "serif" }}>
      <i style={{ opacity: 0.8 }}>{i18n.nodes.empty}</i>
      <br />
      <i style={{ opacity: 0.5, fontSize: "0.8em", lineHeight: "0.8em" }}>{i18n.nodes.doubleClickToName}</i>
    </span>
  );
}

function useHoveredNodeAlwaysOnTop(
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
  }, [dragging, shouldActLikeHovered, ref, zIndex, selected, isEditing]);
}

export function useConnection(nodeId: string) {
  const connectionNodeId = RF.useStore((s) => s.connectionNodeId);
  const connectionHandleType = RF.useStore((s) => s.connectionHandleType);

  const source = connectionNodeId;
  const target = nodeId;

  const edgeIdBeingUpdated = useSwfEditorStore((s) => s.diagram.edgeIdBeingUpdated);
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

export function useNodeClassName(isValidConnectionTarget: boolean, nodeId: string) {
  const isDropTarget = useSwfEditorStore(
    (s) => s.diagram.dropTargetNode?.id === nodeId && containment.get(s.diagram.dropTargetNode?.type as NodeType)
  );
  const isDropTargetNodeValidForSelection = useSwfEditorStore((s) => s.computed(s).isDropTargetNodeValidForSelection());
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

  if (isDropTarget) {
    return isDropTargetNodeValidForSelection ? "drop-target" : "drop-target-invalid";
  }

  return "normal";
}
