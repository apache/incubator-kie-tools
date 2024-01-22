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

import {
  DMN15__tBusinessKnowledgeModel,
  DMN15__tDecision,
  DMN15__tDecisionService,
  DMN15__tDefinitions,
  DMN15__tGroup,
  DMN15__tInputData,
  DMN15__tKnowledgeSource,
  DMN15__tTextAnnotation,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import * as React from "react";
import { useCallback, useEffect, useRef } from "react";
import * as RF from "reactflow";
import { renameDrgElement, renameGroupNode, updateTextAnnotation } from "../../mutations/renameNode";
import { DmnEditorTab, DropTargetNode, SnapGrid, useDmnEditorStore, useDmnEditorStoreApi } from "../../store/Store";
import { snapShapeDimensions } from "../SnapGrid";
import { DECISION_SERVICE_COLLAPSED_DIMENSIONS } from "./DefaultSizes";
import { PositionalNodeHandles } from "../connections/PositionalNodeHandles";
import { NodeType, containment, outgoingStructure } from "../connections/graphStructure";
import { EDGE_TYPES } from "../edges/EdgeTypes";
import { DataTypeNodePanel } from "./DataTypeNodePanel";
import { EditExpressionNodePanel } from "./EditExpressionNodePanel";
import { EditableNodeLabel, OnEditableNodeLabelChange, useEditableNodeLabel } from "./EditableNodeLabel";
import { InfoNodePanel } from "./InfoNodePanel";
import {
  BkmNodeSvg,
  DecisionNodeSvg,
  DecisionServiceNodeSvg,
  GroupNodeSvg,
  InputDataNodeSvg,
  KnowledgeSourceNodeSvg,
  TextAnnotationNodeSvg,
  UnknownNodeSvg,
} from "./NodeSvgs";
import { NODE_TYPES } from "./NodeTypes";
import { OutgoingStuffNodePanel } from "./OutgoingStuffNodePanel";
import { useIsHovered } from "../useIsHovered";
import { getContainmentRelationship, getDecisionServiceDividerLineLocalY } from "../maths/DmnMaths";
import { useDmnEditorDerivedStore } from "../../store/DerivedStore";
import { DmnDiagramEdgeData } from "../edges/Edges";
import { XmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";
import { Unpacked } from "../../tsExt/tsExt";
import { OnCreateDataType, OnTypeRefChange } from "../../dataTypes/TypeRefSelector";
import { MIN_NODE_SIZES } from "./DefaultSizes";
import { select } from "d3-selection";
import { drag } from "d3-drag";
import { updateDecisionServiceDividerLine } from "../../mutations/updateDecisionServiceDividerLine";
import { addTopLevelItemDefinition } from "../../mutations/addTopLevelItemDefinition";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { getNodeLabelPosition, useNodeStyle } from "./NodeStyle";

export type ElementFilter<E extends { __$$element: string }, Filter extends string> = E extends any
  ? E["__$$element"] extends Filter
    ? E
    : never
  : never;

export type NodeDmnObjects =
  | null
  | Unpacked<DMN15__tDefinitions["drgElement"]>
  | ElementFilter<Unpacked<DMN15__tDefinitions["artifact"]>, "textAnnotation" | "group">;

export type DmnDiagramNodeData<T extends NodeDmnObjects = NodeDmnObjects> = {
  dmnObjectNamespace: string | undefined;
  dmnObjectQName: XmlQName;
  dmnObject: T;
  shape: DMNDI15__DMNShape & { index: number };
  index: number;
  /**
   * We don't use Reactflow's parenting mechanism because it is
   * too opinionated on how it deletes nodes/edges that are
   * inside/connected to nodes with parents
   * */
  parentRfNode: RF.Node<DmnDiagramNodeData> | undefined;
};

export const InputDataNode = React.memo(
  ({
    data: { dmnObject: inputData, shape, index, dmnObjectQName, dmnObjectNamespace },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<DmnDiagramNodeData<DMN15__tInputData & { __$$element: "inputData" }>>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isExternal = !!dmnObjectQName.prefix;
    const isResizing = useNodeResizing(id);

    const diagram = useDmnEditorStore((s) => s.diagram);
    const isHovered = (useIsHovered(ref) || isResizing) && diagram.draggingNodes.length === 0;

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(
      inputData["@_id"]
    );
    useHoveredNodeAlwaysOnTop(ref, zIndex, isHovered, dragging, selected, isEditingLabel);

    const dmnEditorStoreApi = useDmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);
    const nodeDimensions = useNodeDimensions(type as NodeType, diagram.snapGrid, id, shape, isExternal);

    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        dmnEditorStoreApi.setState((state) => {
          renameDrgElement({ definitions: state.dmn.model.definitions, newName, index });
        });
      },
      [dmnEditorStoreApi, index]
    );

    const onTypeRefChange = useCallback<OnTypeRefChange>(
      (newTypeRef) => {
        dmnEditorStoreApi.setState((state) => {
          const drgElement = state.dmn.model.definitions.drgElement![index] as DMN15__tInputData;
          drgElement.variable ??= { "@_name": inputData["@_name"] };
          drgElement.variable["@_typeRef"] = newTypeRef;
        });
      },
      [dmnEditorStoreApi, index, inputData]
    );

    const { allFeelVariableUniqueNames } = useDmnEditorDerivedStore();

    const onCreateDataType = useDataTypeCreationCallbackForNodes(index, inputData["@_name"]);

    const { fontCssProperties, shapeStyle } = useNodeStyle({
      dmnStyle: shape["di:Style"],
      nodeType: type as NodeType,
      isEnabled: diagram.overlays.enableStyles,
    });

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className} ${dmnObjectQName.prefix ? "external" : ""}`}>
          <InputDataNodeSvg
            {...nodeDimensions}
            x={0}
            y={0}
            strokeWidth={shapeStyle.strokeWidth}
            fillColor={shapeStyle.fillColor}
            strokeColor={shapeStyle.strokeColor}
          />
        </svg>
        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />
        <div
          ref={ref}
          className={`kie-dmn-editor--node kie-dmn-editor--input-data-node ${className} ${
            dmnObjectQName.prefix ? "external" : ""
          }`}
          tabIndex={-1}
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
        >
          <InfoNodePanel isVisible={!isTargeted && isHovered} />

          <OutgoingStuffNodePanel
            isVisible={!isConnecting && !isTargeted && isHovered}
            nodeTypes={outgoingStructure[NODE_TYPES.inputData].nodes}
            edgeTypes={outgoingStructure[NODE_TYPES.inputData].edges}
          />
          <EditableNodeLabel
            namedElement={inputData}
            namedElementQName={dmnObjectQName}
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            position={getNodeLabelPosition(type as NodeType)}
            value={inputData["@_label"] ?? inputData["@_name"]}
            onChange={setName}
            allUniqueNames={allFeelVariableUniqueNames}
            shouldCommitOnBlur={true}
            fontCssProperties={fontCssProperties}
          />
          {isHovered && (
            <NodeResizerHandle
              nodeType={type as NodeType}
              snapGrid={diagram.snapGrid}
              nodeId={id}
              nodeShapeIndex={shape.index}
            />
          )}
          <DataTypeNodePanel
            isVisible={!isTargeted && isHovered}
            variable={inputData.variable}
            namespace={dmnObjectNamespace}
            shape={shape}
            onCreate={onCreateDataType}
            onChange={onTypeRefChange}
          />
        </div>
      </>
    );
  }
);

export const DecisionNode = React.memo(
  ({
    data: { parentRfNode, dmnObject: decision, shape, index, dmnObjectQName, dmnObjectNamespace },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<DmnDiagramNodeData<DMN15__tDecision & { __$$element: "decision" }>>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isExternal = !!dmnObjectQName.prefix;
    const isResizing = useNodeResizing(id);

    const diagram = useDmnEditorStore((s) => s.diagram);
    const isHovered = (useIsHovered(ref) || isResizing) && diagram.draggingNodes.length === 0;

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(
      decision["@_id"]
    );
    useHoveredNodeAlwaysOnTop(ref, zIndex, isHovered, dragging, selected, isEditingLabel);

    const dmnEditorStoreApi = useDmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);
    const nodeDimensions = useNodeDimensions(type as NodeType, diagram.snapGrid, id, shape, isExternal);
    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        dmnEditorStoreApi.setState((state) => {
          renameDrgElement({ definitions: state.dmn.model.definitions, newName, index });
        });
      },
      [dmnEditorStoreApi, index]
    );

    const onTypeRefChange = useCallback<OnTypeRefChange>(
      (newTypeRef) => {
        dmnEditorStoreApi.setState((state) => {
          const drgElement = state.dmn.model.definitions.drgElement![index] as DMN15__tInputData;
          drgElement.variable ??= { "@_name": decision["@_name"] };
          drgElement.variable["@_typeRef"] = newTypeRef;
        });
      },
      [decision, dmnEditorStoreApi, index]
    );

    const { allFeelVariableUniqueNames } = useDmnEditorDerivedStore();

    const onCreateDataType = useDataTypeCreationCallbackForNodes(index, decision["@_name"]);

    const { fontCssProperties, shapeStyle } = useNodeStyle({
      dmnStyle: shape["di:Style"],
      nodeType: type as NodeType,
      isEnabled: diagram.overlays.enableStyles,
    });

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className} ${dmnObjectQName.prefix ? "external" : ""}`}>
          <DecisionNodeSvg
            {...nodeDimensions}
            x={0}
            y={0}
            strokeWidth={parentRfNode ? 3 : shapeStyle.strokeWidth}
            fillColor={shapeStyle.fillColor}
            strokeColor={shapeStyle.strokeColor}
          />
        </svg>

        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />

        <div
          ref={ref}
          className={`kie-dmn-editor--node kie-dmn-editor--decision-node ${className} ${
            dmnObjectQName.prefix ? "external" : ""
          }`}
          tabIndex={-1}
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
        >
          <InfoNodePanel isVisible={!isTargeted && isHovered} />

          {!isExternal && <EditExpressionNodePanel isVisible={!isTargeted && isHovered} id={decision["@_id"]!} />}
          <OutgoingStuffNodePanel
            isVisible={!isConnecting && !isTargeted && isHovered}
            nodeTypes={outgoingStructure[NODE_TYPES.decision].nodes}
            edgeTypes={outgoingStructure[NODE_TYPES.decision].edges}
          />
          <EditableNodeLabel
            namedElement={decision}
            namedElementQName={dmnObjectQName}
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            position={getNodeLabelPosition(type as NodeType)}
            value={decision["@_label"] ?? decision["@_name"]}
            onChange={setName}
            allUniqueNames={allFeelVariableUniqueNames}
            shouldCommitOnBlur={true}
            fontCssProperties={fontCssProperties}
          />
          {isHovered && (
            <NodeResizerHandle
              nodeType={type as NodeType}
              snapGrid={diagram.snapGrid}
              nodeId={id}
              nodeShapeIndex={shape.index}
            />
          )}
          <DataTypeNodePanel
            isVisible={!isTargeted && isHovered}
            variable={decision.variable}
            namespace={dmnObjectNamespace}
            shape={shape}
            onChange={onTypeRefChange}
            onCreate={onCreateDataType}
          />
        </div>
      </>
    );
  }
);

export const BkmNode = React.memo(
  ({
    data: { dmnObject: bkm, shape, index, dmnObjectQName, dmnObjectNamespace },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<DmnDiagramNodeData<DMN15__tBusinessKnowledgeModel & { __$$element: "businessKnowledgeModel" }>>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isExternal = !!dmnObjectQName.prefix;
    const isResizing = useNodeResizing(id);

    const diagram = useDmnEditorStore((s) => s.diagram);
    const isHovered = (useIsHovered(ref) || isResizing) && diagram.draggingNodes.length === 0;

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(
      bkm["@_id"]
    );
    useHoveredNodeAlwaysOnTop(ref, zIndex, isHovered, dragging, selected, isEditingLabel);

    const dmnEditorStoreApi = useDmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);
    const nodeDimensions = useNodeDimensions(type as NodeType, diagram.snapGrid, id, shape, isExternal);
    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        dmnEditorStoreApi.setState((state) => {
          renameDrgElement({ definitions: state.dmn.model.definitions, newName, index });
        });
      },
      [dmnEditorStoreApi, index]
    );

    const onTypeRefChange = useCallback<OnTypeRefChange>(
      (newTypeRef) => {
        dmnEditorStoreApi.setState((state) => {
          const drgElement = state.dmn.model.definitions.drgElement![index] as DMN15__tInputData;
          drgElement.variable ??= { "@_name": bkm["@_name"] };
          drgElement.variable["@_typeRef"] = newTypeRef;
        });
      },
      [bkm, dmnEditorStoreApi, index]
    );

    const { allFeelVariableUniqueNames } = useDmnEditorDerivedStore();

    const onCreateDataType = useDataTypeCreationCallbackForNodes(index, bkm["@_name"]);

    const { fontCssProperties, shapeStyle } = useNodeStyle({
      dmnStyle: shape["di:Style"],
      nodeType: type as NodeType,
      isEnabled: diagram.overlays.enableStyles,
    });

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className} ${dmnObjectQName.prefix ? "external" : ""}`}>
          <BkmNodeSvg
            {...nodeDimensions}
            x={0}
            y={0}
            strokeWidth={shapeStyle.strokeWidth}
            fillColor={shapeStyle.fillColor}
            strokeColor={shapeStyle.strokeColor}
          />
        </svg>

        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />

        <div
          ref={ref}
          className={`kie-dmn-editor--node kie-dmn-editor--bkm-node ${className} ${
            dmnObjectQName.prefix ? "external" : ""
          }`}
          tabIndex={-1}
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
        >
          <InfoNodePanel isVisible={!isTargeted && isHovered} />

          {!isExternal && <EditExpressionNodePanel isVisible={!isTargeted && isHovered} id={bkm["@_id"]!} />}
          <OutgoingStuffNodePanel
            isVisible={!isConnecting && !isTargeted && isHovered}
            nodeTypes={outgoingStructure[NODE_TYPES.bkm].nodes}
            edgeTypes={outgoingStructure[NODE_TYPES.bkm].edges}
          />
          <EditableNodeLabel
            namedElement={bkm}
            namedElementQName={dmnObjectQName}
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            position={getNodeLabelPosition(type as NodeType)}
            value={bkm["@_label"] ?? bkm["@_name"]}
            onChange={setName}
            allUniqueNames={allFeelVariableUniqueNames}
            shouldCommitOnBlur={true}
            fontCssProperties={fontCssProperties}
          />
          {isHovered && (
            <NodeResizerHandle
              nodeType={type as NodeType}
              snapGrid={diagram.snapGrid}
              nodeId={id}
              nodeShapeIndex={shape.index}
            />
          )}
          <DataTypeNodePanel
            isVisible={!isTargeted && isHovered}
            variable={bkm.variable}
            namespace={dmnObjectNamespace}
            shape={shape}
            onChange={onTypeRefChange}
            onCreate={onCreateDataType}
          />
        </div>
      </>
    );
  }
);

export const KnowledgeSourceNode = React.memo(
  ({
    data: { dmnObject: knowledgeSource, shape, index, dmnObjectQName },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<DmnDiagramNodeData<DMN15__tKnowledgeSource & { __$$element: "knowledgeSource" }>>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isExternal = !!dmnObjectQName.prefix;
    const isResizing = useNodeResizing(id);

    const diagram = useDmnEditorStore((s) => s.diagram);
    const isHovered = (useIsHovered(ref) || isResizing) && diagram.draggingNodes.length === 0;

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(
      knowledgeSource["@_id"]
    );
    useHoveredNodeAlwaysOnTop(ref, zIndex, isHovered, dragging, selected, isEditingLabel);

    const dmnEditorStoreApi = useDmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);
    const nodeDimensions = useNodeDimensions(type as NodeType, diagram.snapGrid, id, shape, isExternal);
    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        dmnEditorStoreApi.setState((state) => {
          renameDrgElement({ definitions: state.dmn.model.definitions, newName, index });
        });
      },
      [dmnEditorStoreApi, index]
    );

    const { allFeelVariableUniqueNames } = useDmnEditorDerivedStore();

    const { fontCssProperties, shapeStyle } = useNodeStyle({
      dmnStyle: shape["di:Style"],
      nodeType: type as NodeType,
      isEnabled: diagram.overlays.enableStyles,
    });

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className} ${dmnObjectQName.prefix ? "external" : ""}`}>
          <KnowledgeSourceNodeSvg
            {...nodeDimensions}
            x={0}
            y={0}
            strokeWidth={shapeStyle.strokeWidth}
            fillColor={shapeStyle.fillColor}
            strokeColor={shapeStyle.strokeColor}
          />
        </svg>

        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />

        <div
          ref={ref}
          className={`kie-dmn-editor--node kie-dmn-editor--knowledge-source-node ${className} ${
            dmnObjectQName.prefix ? "external" : ""
          }`}
          tabIndex={-1}
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
        >
          <InfoNodePanel isVisible={!isTargeted && isHovered} />
          <OutgoingStuffNodePanel
            isVisible={!isConnecting && !isTargeted && isHovered}
            nodeTypes={outgoingStructure[NODE_TYPES.knowledgeSource].nodes}
            edgeTypes={outgoingStructure[NODE_TYPES.knowledgeSource].edges}
          />
          <EditableNodeLabel
            namedElement={knowledgeSource}
            namedElementQName={dmnObjectQName}
            position={getNodeLabelPosition(type as NodeType)}
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            value={knowledgeSource["@_label"] ?? knowledgeSource["@_name"]}
            onChange={setName}
            skipValidation={true}
            allUniqueNames={allFeelVariableUniqueNames}
            shouldCommitOnBlur={true}
            fontCssProperties={fontCssProperties}
          />
          {isHovered && (
            <NodeResizerHandle
              nodeType={type as NodeType}
              snapGrid={diagram.snapGrid}
              nodeId={id}
              nodeShapeIndex={shape.index}
            />
          )}
        </div>
      </>
    );
  }
);

export const TextAnnotationNode = React.memo(
  ({
    data: { dmnObject: textAnnotation, shape, index, dmnObjectQName },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<DmnDiagramNodeData<DMN15__tTextAnnotation & { __$$element: "textAnnotation" }>>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isExternal = !!dmnObjectQName.prefix;
    const isResizing = useNodeResizing(id);

    const diagram = useDmnEditorStore((s) => s.diagram);
    const isHovered = (useIsHovered(ref) || isResizing) && diagram.draggingNodes.length === 0;

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(
      textAnnotation["@_id"]
    );
    useHoveredNodeAlwaysOnTop(ref, zIndex, isHovered, dragging, selected, isEditingLabel);

    const dmnEditorStoreApi = useDmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);
    const nodeDimensions = useNodeDimensions(type as NodeType, diagram.snapGrid, id, shape, isExternal);
    const setText = useCallback(
      (newText: string) => {
        dmnEditorStoreApi.setState((state) => {
          updateTextAnnotation({ definitions: state.dmn.model.definitions, newText, index });
        });
      },
      [dmnEditorStoreApi, index]
    );

    const { allFeelVariableUniqueNames } = useDmnEditorDerivedStore();

    const { fontCssProperties, shapeStyle } = useNodeStyle({
      dmnStyle: shape["di:Style"],
      nodeType: type as NodeType,
      isEnabled: diagram.overlays.enableStyles,
    });

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className} ${dmnObjectQName.prefix ? "external" : ""}`}>
          <TextAnnotationNodeSvg
            {...nodeDimensions}
            x={0}
            y={0}
            strokeColor={shapeStyle.strokeColor}
            strokeWidth={shapeStyle.strokeWidth}
            fillColor={shapeStyle.fillColor}
          />
        </svg>

        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />

        <div
          ref={ref}
          className={`kie-dmn-editor--node kie-dmn-editor--text-annotation-node ${className} ${
            dmnObjectQName.prefix ? "external" : ""
          }`}
          tabIndex={-1}
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
        >
          <InfoNodePanel isVisible={!isTargeted && isHovered} />
          <OutgoingStuffNodePanel
            isVisible={!isConnecting && !isTargeted && isHovered}
            nodeTypes={outgoingStructure[NODE_TYPES.textAnnotation].nodes}
            edgeTypes={outgoingStructure[NODE_TYPES.textAnnotation].edges}
          />
          <EditableNodeLabel
            id={textAnnotation["@_id"]}
            namedElement={undefined}
            namedElementQName={undefined}
            position={getNodeLabelPosition(type as NodeType)}
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            value={textAnnotation["@_label"] ?? textAnnotation.text?.__$$text}
            onChange={setText}
            skipValidation={true}
            allUniqueNames={allFeelVariableUniqueNames}
            shouldCommitOnBlur={true}
            fontCssProperties={fontCssProperties}
          />
          {isHovered && (
            <NodeResizerHandle
              nodeType={type as NodeType}
              snapGrid={diagram.snapGrid}
              nodeId={id}
              nodeShapeIndex={shape.index}
            />
          )}
        </div>
      </>
    );
  }
);

export const DecisionServiceNode = React.memo(
  ({
    data: { dmnObject: decisionService, shape, index, dmnObjectQName, dmnObjectNamespace },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<DmnDiagramNodeData<DMN15__tDecisionService & { __$$element: "decisionService" }>>) => {
    const ref = useRef<SVGRectElement>(null);
    const isExternal = !!dmnObjectQName.prefix;

    const isResizing = useNodeResizing(id);

    const diagram = useDmnEditorStore((s) => s.diagram);
    const isHovered = (useIsHovered(ref) || isResizing) && diagram.draggingNodes.length === 0;

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(
      decisionService["@_id"]
    );
    useHoveredNodeAlwaysOnTop(ref, zIndex, isHovered, dragging, selected, isEditingLabel);
    const dmnEditorStoreApi = useDmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);

    const nodeDimensions = useNodeDimensions(type as NodeType, diagram.snapGrid, id, shape, isExternal);
    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        dmnEditorStoreApi.setState((state) => {
          renameDrgElement({ definitions: state.dmn.model.definitions, newName, index });
        });
      },
      [dmnEditorStoreApi, index]
    );

    // Select nodes representing output and encapsulated decisions contained by the Decision Service
    useEffect(() => {
      const onDoubleClick = () => {
        dmnEditorStoreApi.setState((state) => {
          state.diagram._selectedNodes = [
            id, // Include the Decision Service itself.
            ...(decisionService.outputDecision ?? []).map((od) => od["@_href"]),
            ...(decisionService.encapsulatedDecision ?? []).map((ed) => ed["@_href"]),
          ];
        });
      };

      const r = ref.current;
      r?.addEventListener("dblclick", onDoubleClick);
      return () => {
        r?.removeEventListener("dblclick", onDoubleClick);
      };
    }, [decisionService.encapsulatedDecision, decisionService.outputDecision, dmnEditorStoreApi, id]);

    const onTypeRefChange = useCallback<OnTypeRefChange>(
      (newTypeRef) => {
        dmnEditorStoreApi.setState((state) => {
          const drgElement = state.dmn.model.definitions.drgElement![index] as DMN15__tInputData;
          drgElement.variable ??= { "@_name": decisionService["@_name"] };
          drgElement.variable["@_typeRef"] = newTypeRef;
        });
      },
      [decisionService, dmnEditorStoreApi, index]
    );

    const { allFeelVariableUniqueNames, dmnShapesByHref } = useDmnEditorDerivedStore();

    const dividerLineRef = useRef<SVGPathElement>(null);

    // External Decision Service nodes are always collapsed.
    const isCollapsed = isExternal || shape["@_isCollapsed"];

    const onCreateDataType = useDataTypeCreationCallbackForNodes(index, decisionService["@_name"]);

    useEffect(() => {
      if (!dividerLineRef.current) {
        return;
      }

      const selection = select(dividerLineRef.current);
      const dragHandler = drag<SVGCircleElement, unknown>()
        .on("start", () => {
          dmnEditorStoreApi.setState((state) =>
            state.dispatch.diagram.setDividerLineStatus(state, id, { moving: true })
          );
        })
        .on("drag", (e) => {
          dmnEditorStoreApi.setState((state) => {
            updateDecisionServiceDividerLine({
              definitions: state.dmn.model.definitions,
              drdIndex: diagram.drdIndex,
              dmnShapesByHref,
              drgElementIndex: index,
              shapeIndex: shape.index,
              localYPosition: e.y,
              snapGrid: diagram.snapGrid,
            });
          });
        })
        .on("end", (e) => {
          dmnEditorStoreApi.setState((state) =>
            state.dispatch.diagram.setDividerLineStatus(state, id, { moving: false })
          );
        });

      selection.call(dragHandler);
      return () => {
        selection.on(".drag", null);
      };
    }, [
      decisionService,
      diagram.drdIndex,
      diagram.snapGrid,
      dmnEditorStoreApi,
      dmnShapesByHref,
      id,
      index,
      shape.index,
    ]);

    const { fontCssProperties, shapeStyle } = useNodeStyle({
      dmnStyle: shape["di:Style"],
      nodeType: type as NodeType,
      isEnabled: diagram.overlays.enableStyles,
    });

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className} ${dmnObjectQName.prefix ? "external" : ""}`}>
          <DecisionServiceNodeSvg
            dividerLineRef={dividerLineRef}
            ref={ref}
            {...nodeDimensions}
            x={0}
            y={0}
            strokeWidth={3}
            fillColor={shapeStyle.fillColor}
            strokeColor={shapeStyle.strokeColor}
            isReadonly={false}
            isCollapsed={isCollapsed}
            showSectionLabels={diagram.dropTargetNode?.id === id}
            dividerLineLocalY={getDecisionServiceDividerLineLocalY(shape)}
          />
        </svg>

        <PositionalNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />

        <div
          className={`kie-dmn-editor--node kie-dmn-editor--decision-service-node ${className} ${
            dmnObjectQName.prefix ? "external" : ""
          }`}
          tabIndex={-1}
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
        >
          <InfoNodePanel isVisible={!isTargeted && selected && !dragging} />

          <OutgoingStuffNodePanel
            isVisible={!isConnecting && !isTargeted && selected && !dragging}
            nodeTypes={outgoingStructure[NODE_TYPES.decisionService].nodes}
            edgeTypes={outgoingStructure[NODE_TYPES.decisionService].edges}
          />
          <EditableNodeLabel
            namedElement={decisionService}
            namedElementQName={dmnObjectQName}
            position={getNodeLabelPosition(type as NodeType)}
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            value={decisionService["@_label"] ?? decisionService["@_name"]}
            onChange={setName}
            allUniqueNames={allFeelVariableUniqueNames}
            shouldCommitOnBlur={true}
            fontCssProperties={fontCssProperties}
          />
          {selected && !dragging && !isCollapsed && (
            <NodeResizerHandle
              nodeType={type as NodeType}
              snapGrid={diagram.snapGrid}
              nodeId={id}
              nodeShapeIndex={shape.index}
            />
          )}
          {isCollapsed && <div className={"kie-dmn-editor--decision-service-collapsed-button"}>+</div>}
          <DataTypeNodePanel
            isVisible={!isTargeted && selected && !dragging}
            variable={decisionService.variable}
            namespace={dmnObjectNamespace}
            shape={shape}
            onCreate={onCreateDataType}
            onChange={onTypeRefChange}
          />
        </div>
      </>
    );
  }
);

export const GroupNode = React.memo(
  ({
    data: { dmnObject: group, shape, index, dmnObjectQName },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<DmnDiagramNodeData<DMN15__tGroup & { __$$element: "group" }>>) => {
    const ref = useRef<SVGRectElement>(null);
    const isExternal = !!dmnObjectQName.prefix;
    const isResizing = useNodeResizing(id);

    const diagram = useDmnEditorStore((s) => s.diagram);
    const isHovered = (useIsHovered(ref) || isResizing) && diagram.draggingNodes.length === 0;

    const dmnEditorStoreApi = useDmnEditorStoreApi();
    const reactFlow = RF.useReactFlow<DmnDiagramNodeData, DmnDiagramEdgeData>();

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(
      group["@_id"]
    );
    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);
    const nodeDimensions = useNodeDimensions(type as NodeType, diagram.snapGrid, id, shape, isExternal);
    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        dmnEditorStoreApi.setState((state) => {
          renameGroupNode({ definitions: state.dmn.model.definitions, newName, index });
        });
      },
      [dmnEditorStoreApi, index]
    );

    // Select nodes that are visually entirely inside the group.
    useEffect(() => {
      const onDoubleClick = () => {
        dmnEditorStoreApi.setState((state) => {
          state.diagram._selectedNodes = reactFlow.getNodes().flatMap((n) =>
            getContainmentRelationship({
              bounds: n.data.shape["dc:Bounds"]!,
              container: shape["dc:Bounds"]!,
              snapGrid: state.diagram.snapGrid,
              containerMinSizes: MIN_NODE_SIZES[NODE_TYPES.group],
              boundsMinSizes: MIN_NODE_SIZES[n.type as NodeType],
            }).isInside
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
    }, [dmnEditorStoreApi, reactFlow, shape]);

    const { allFeelVariableUniqueNames } = useDmnEditorDerivedStore();

    const { fontCssProperties, shapeStyle } = useNodeStyle({
      dmnStyle: shape["di:Style"],
      nodeType: type as NodeType,
      isEnabled: diagram.overlays.enableStyles,
    });

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className} ${dmnObjectQName.prefix ? "external" : ""}`}>
          <GroupNodeSvg
            ref={ref}
            {...nodeDimensions}
            x={0}
            y={0}
            strokeWidth={3}
            fillColor={shapeStyle.fillColor}
            strokeColor={shapeStyle.strokeColor}
          />
        </svg>

        <div
          className={`kie-dmn-editor--node kie-dmn-editor--group-node ${className} ${
            dmnObjectQName.prefix ? "external" : ""
          }`}
          tabIndex={-1}
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
        >
          <OutgoingStuffNodePanel
            isVisible={!isConnecting && !isTargeted && selected && !dragging}
            nodeTypes={outgoingStructure[NODE_TYPES.group].nodes}
            edgeTypes={outgoingStructure[NODE_TYPES.group].edges}
          />
          <EditableNodeLabel
            id={group["@_id"]}
            namedElement={undefined}
            namedElementQName={undefined}
            position={getNodeLabelPosition(type as NodeType)}
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            value={group["@_label"] ?? group["@_name"]}
            onChange={setName}
            skipValidation={true}
            allUniqueNames={allFeelVariableUniqueNames}
            shouldCommitOnBlur={true}
            fontCssProperties={fontCssProperties}
          />
          {selected && !dragging && (
            <NodeResizerHandle
              nodeType={type as NodeType}
              snapGrid={diagram.snapGrid}
              nodeId={id}
              nodeShapeIndex={shape.index}
            />
          )}
        </div>
      </>
    );
  }
);

export const UnknownNode = React.memo(
  ({
    data: { shape, dmnObjectQName },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<DmnDiagramNodeData<null>>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isExternal = !!dmnObjectQName.prefix;
    const isResizing = useNodeResizing(id);

    const diagram = useDmnEditorStore((s) => s.diagram);
    const isHovered = (useIsHovered(ref) || isResizing) && diagram.draggingNodes.length === 0;

    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);
    const nodeDimensions = useNodeDimensions(type as NodeType, diagram.snapGrid, id, shape, isExternal);

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className}`}>
          <UnknownNodeSvg {...nodeDimensions} x={0} y={0} />
        </svg>

        <RF.Handle key={"unknown"} id={"unknown"} type={"source"} style={{ opacity: 0 }} position={RF.Position.Top} />

        <div ref={ref} className={`kie-dmn-editor--node kie-dmn-editor--unknown-node ${className}`} tabIndex={-1}>
          <InfoNodePanel isVisible={!isTargeted && isHovered} />

          <EditableNodeLabel
            namedElement={undefined}
            namedElementQName={undefined}
            position={getNodeLabelPosition(type as NodeType)}
            isEditing={false}
            setEditing={() => {}}
            value={`? `}
            onChange={() => {}}
            skipValidation={false}
            allUniqueNames={new Map()}
            shouldCommitOnBlur={true}
          />
          {selected && !dragging && (
            <NodeResizerHandle
              nodeType={type as NodeType}
              snapGrid={diagram.snapGrid}
              nodeId={id}
              nodeShapeIndex={shape.index}
            />
          )}
        </div>
      </>
    );
  }
);

///

export function EmptyLabel() {
  return (
    <span style={{ fontFamily: "serif" }}>
      <i style={{ opacity: 0.8 }}>{`<Empty>`}</i>
      <br />
      <i style={{ opacity: 0.5, fontSize: "0.8em", lineHeight: "0.8em" }}>{`Double-click to name`}</i>
    </span>
  );
}

const resizerControlStyle = {
  background: "transparent",
  border: "none",
};

export function NodeResizerHandle(props: {
  snapGrid: SnapGrid;
  nodeId: string;
  nodeType: NodeType;
  nodeShapeIndex: number;
}) {
  const minSize = MIN_NODE_SIZES[props.nodeType](props.snapGrid);
  return (
    <RF.NodeResizeControl style={resizerControlStyle} minWidth={minSize["@_width"]} minHeight={minSize["@_height"]}>
      <div
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

function useNodeResizing(id: string): boolean {
  const node = RF.useStore((s) => s.nodeInternals.get(id));
  if (!node) {
    throw new Error("Can't use nodeInternals of non-existent node " + id);
  }

  return node.resizing ?? false;
}
function useNodeDimensions(
  type: NodeType,
  snapGrid: SnapGrid,
  id: string,
  shape: DMNDI15__DMNShape,
  isExternal: boolean
): RF.Dimensions {
  const node = RF.useStore((s) => s.nodeInternals.get(id));
  if (!node) {
    throw new Error("Can't use nodeInternals of non-existent node " + id);
  }

  if (type === NODE_TYPES.decisionService && (isExternal || shape["@_isCollapsed"])) {
    return DECISION_SERVICE_COLLAPSED_DIMENSIONS;
  }

  const minSizes = MIN_NODE_SIZES[node.type as NodeType](snapGrid);

  return {
    width: node.width ?? snapShapeDimensions(snapGrid, shape, minSizes).width,
    height: node.height ?? snapShapeDimensions(snapGrid, shape, minSizes).height,
  };
}

function useHoveredNodeAlwaysOnTop(
  ref: React.RefObject<HTMLDivElement | SVGElement>,
  layer: number,
  isHovered: boolean,
  dragging: boolean,
  selected: boolean,
  isEditing: boolean
) {
  useEffect(() => {
    setTimeout(() => {
      if (selected && !isEditing) {
        ref.current?.focus();
      }
      if (ref.current) {
        ref.current.parentElement!.style.zIndex = `${isHovered || dragging ? layer + 1000 + 1 : layer}`;
      }
    }, 0);
  }, [dragging, isHovered, ref, selected, layer, isEditing]);
}

export function useConnection(nodeId: string) {
  const connectionNodeId = RF.useStore((s) => s.connectionNodeId);
  const connectionHandleId = RF.useStore((s) => s.connectionHandleId);
  const connectionHandleType = RF.useStore((s) => s.connectionHandleType);
  const edgeIdBeingUpdated = useDmnEditorStore((s) => s.diagram.edgeIdBeingUpdated);
  const { edgesById } = useDmnEditorDerivedStore();

  const edge = edgeIdBeingUpdated ? edgesById.get(edgeIdBeingUpdated) : null;
  const source = connectionNodeId;
  const target = nodeId;
  const sourceHandle = connectionHandleId ?? edge?.type ?? null;

  const connection = {
    source: connectionHandleType === "source" ? source : target,
    target: connectionHandleType === "source" ? target : source,
    sourceHandle,
    targetHandle: null, // We don't use targetHandles, as target handles are only different in position, not in semantic.
  };

  return connection;
}

export function useConnectionTargetStatus(nodeId: string, isHovered: boolean) {
  const connectionNodeId = RF.useStore((s) => s.connectionNodeId);
  const isValidConnection = RF.useStore((s) => s.isValidConnection);
  const connection = useConnection(nodeId);

  return {
    isTargeted: !!connectionNodeId && connectionNodeId !== nodeId && isHovered,
    isValidConnectionTarget: isValidConnection?.(connection) ?? false,
    isConnecting: !!connectionNodeId,
  };
}

export function useNodeClassName(
  dropTargetNode: DropTargetNode,
  isConnecting: boolean,
  isValidConnectionTarget: boolean,
  nodeId: string
) {
  const { isDropTargetNodeValidForSelection } = useDmnEditorDerivedStore();
  const connectionNodeId = RF.useStore((s) => s.connectionNodeId);
  const connection = useConnection(nodeId);
  const isEdgeConnection = !!Object.values(EDGE_TYPES).find((s) => s === connection.sourceHandle);
  const isNodeConnection = !!Object.values(NODE_TYPES).find((s) => s === connection.sourceHandle);

  if (isNodeConnection && isConnecting && connectionNodeId !== nodeId) {
    return "dimmed";
  }

  if (isEdgeConnection && isConnecting && (!isValidConnectionTarget || connectionNodeId === nodeId)) {
    return "dimmed";
  }

  if (dropTargetNode?.id === nodeId && containment.get(dropTargetNode.type as NodeType)) {
    return isDropTargetNodeValidForSelection ? "drop-target" : "drop-target-invalid";
  }

  return "normal";
}

export function useDataTypeCreationCallbackForNodes(index: number, drgElementName: string) {
  const dmnEditorStoreApi = useDmnEditorStoreApi();

  return useCallback<OnCreateDataType>(
    (newDataTypeName) => {
      dmnEditorStoreApi.setState((state) => {
        const drgElement = state.dmn.model.definitions.drgElement![index] as DMN15__tInputData;
        drgElement.variable ??= { "@_name": drgElementName };
        drgElement.variable["@_typeRef"] = newDataTypeName;
        const newItemDefinition = addTopLevelItemDefinition({
          definitions: state.dmn.model.definitions,
          partial: { "@_name": newDataTypeName, typeRef: { __$$text: DmnBuiltInDataType.Undefined } },
        });
        state.dataTypesEditor.activeItemDefinitionId = newItemDefinition["@_id"];
        state.navigation.tab = DmnEditorTab.DATA_TYPES;
        state.focus.consumableId = newItemDefinition["@_id"];
      });
    },
    [dmnEditorStoreApi, drgElementName, index]
  );
}
