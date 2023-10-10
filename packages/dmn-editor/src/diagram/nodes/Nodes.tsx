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
import { DropTargetNode, SnapGrid, useDmnEditorStore, useDmnEditorStoreApi } from "../../store/Store";
import { DECISION_SERVICE_COLLAPSED_DIMENSIONS, MIN_SIZE_FOR_NODES, snapShapeDimensions } from "../SnapGrid";
import { PositionalTargetNodeHandles } from "../connections/PositionalTargetNodeHandles";
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
} from "./NodeSvgs";
import { NODE_TYPES } from "./NodeTypes";
import { OutgoingStuffNodePanel } from "./OutgoingStuffNodePanel";
import { useIsHovered } from "../useIsHovered";
import { getContainmentRelationship, getDecisionServiceDividerLineLocalY } from "../maths/DmnMaths";
import { useDmnEditorDerivedStore } from "../../store/DerivedStore";
import { DmnDiagramEdgeData } from "../edges/Edges";
import { XmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";
import { Unpacked } from "../../tsExt/tsExt";
import { OnTypeRefChange } from "../../dataTypes/TypeRefSelector";

export type NodeDmnObjects = Unpacked<DMN15__tDefinitions["drgElement"] | DMN15__tDefinitions["artifact"]>;

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
    data: { dmnObject: inputData, shape, index, dmnObjectQName },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<DmnDiagramNodeData<DMN15__tInputData & { __$$element: "inputData" }>>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isResizing = useNodeResizing(id);

    const diagram = useDmnEditorStore((s) => s.diagram);
    const isHovered = (useIsHovered(ref) || isResizing) && diagram.draggingNodes.length === 0;

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();
    useHoveredNodeAlwaysOnTop(ref, zIndex, isHovered, dragging, selected, isEditingLabel);

    const dmnEditorStoreApi = useDmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);
    const nodeDimensions = useNodeDimensions(type as NodeType, diagram.snapGrid, id, shape);

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
          (state.dmn.model.definitions.drgElement![index] as DMN15__tInputData).variable!["@_typeRef"] = newTypeRef;
        });
      },
      [dmnEditorStoreApi, index]
    );

    const { allFeelVariableUniqueNames } = useDmnEditorDerivedStore();

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className} ${dmnObjectQName.prefix ? "external" : ""}`}>
          <InputDataNodeSvg {...nodeDimensions} x={0} y={0} />
        </svg>

        <PositionalTargetNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />

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
            value={inputData["@_label"] ?? inputData["@_name"]}
            onChange={setName}
            allUniqueNames={allFeelVariableUniqueNames}
          />
          {isHovered && <NodeResizerHandle snapGrid={diagram.snapGrid} nodeId={id} nodeShapeIndex={shape.index} />}
          <DataTypeNodePanel
            isVisible={!isTargeted && isHovered}
            variable={inputData.variable}
            shape={shape}
            onChange={onTypeRefChange}
          />
        </div>
      </>
    );
  }
);

export const DecisionNode = React.memo(
  ({
    data: { parentRfNode, dmnObject: decision, shape, index, dmnObjectQName },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<DmnDiagramNodeData<DMN15__tDecision & { __$$element: "decision" }>>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isResizing = useNodeResizing(id);

    const diagram = useDmnEditorStore((s) => s.diagram);
    const isHovered = (useIsHovered(ref) || isResizing) && diagram.draggingNodes.length === 0;

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();
    useHoveredNodeAlwaysOnTop(ref, zIndex, isHovered, dragging, selected, isEditingLabel);

    const dmnEditorStoreApi = useDmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);
    const nodeDimensions = useNodeDimensions(type as NodeType, diagram.snapGrid, id, shape);
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
          (state.dmn.model.definitions.drgElement![index] as DMN15__tInputData).variable!["@_typeRef"] = newTypeRef;
        });
      },
      [dmnEditorStoreApi, index]
    );

    const { allFeelVariableUniqueNames } = useDmnEditorDerivedStore();

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className} ${dmnObjectQName.prefix ? "external" : ""}`}>
          <DecisionNodeSvg {...nodeDimensions} x={0} y={0} strokeWidth={parentRfNode ? 3 : undefined} />
        </svg>

        <PositionalTargetNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />

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

          <EditExpressionNodePanel isVisible={!isTargeted && isHovered} id={decision["@_id"]!} />
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
            value={decision["@_label"] ?? decision["@_name"]}
            onChange={setName}
            allUniqueNames={allFeelVariableUniqueNames}
          />
          {isHovered && <NodeResizerHandle snapGrid={diagram.snapGrid} nodeId={id} nodeShapeIndex={shape.index} />}
          <DataTypeNodePanel
            isVisible={!isTargeted && isHovered}
            variable={decision.variable}
            shape={shape}
            onChange={onTypeRefChange}
          />
        </div>
      </>
    );
  }
);

export const BkmNode = React.memo(
  ({
    data: { dmnObject: bkm, shape, index, dmnObjectQName },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<DmnDiagramNodeData<DMN15__tBusinessKnowledgeModel & { __$$element: "businessKnowledgeModel" }>>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isResizing = useNodeResizing(id);

    const diagram = useDmnEditorStore((s) => s.diagram);
    const isHovered = (useIsHovered(ref) || isResizing) && diagram.draggingNodes.length === 0;

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();
    useHoveredNodeAlwaysOnTop(ref, zIndex, isHovered, dragging, selected, isEditingLabel);

    const dmnEditorStoreApi = useDmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);
    const nodeDimensions = useNodeDimensions(type as NodeType, diagram.snapGrid, id, shape);
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
          (state.dmn.model.definitions.drgElement![index] as DMN15__tInputData).variable!["@_typeRef"] = newTypeRef;
        });
      },
      [dmnEditorStoreApi, index]
    );

    const { allFeelVariableUniqueNames } = useDmnEditorDerivedStore();

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className} ${dmnObjectQName.prefix ? "external" : ""}`}>
          <BkmNodeSvg {...nodeDimensions} x={0} y={0} />
        </svg>

        <PositionalTargetNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />

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

          <EditExpressionNodePanel isVisible={!isTargeted && isHovered} id={bkm["@_id"]!} />
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
            value={bkm["@_label"] ?? bkm["@_name"]}
            onChange={setName}
            allUniqueNames={allFeelVariableUniqueNames}
          />
          {isHovered && <NodeResizerHandle snapGrid={diagram.snapGrid} nodeId={id} nodeShapeIndex={shape.index} />}
          <DataTypeNodePanel
            isVisible={!isTargeted && isHovered}
            variable={bkm.variable}
            shape={shape}
            onChange={onTypeRefChange}
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
    const isResizing = useNodeResizing(id);

    const diagram = useDmnEditorStore((s) => s.diagram);
    const isHovered = (useIsHovered(ref) || isResizing) && diagram.draggingNodes.length === 0;

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();
    useHoveredNodeAlwaysOnTop(ref, zIndex, isHovered, dragging, selected, isEditingLabel);

    const dmnEditorStoreApi = useDmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);
    const nodeDimensions = useNodeDimensions(type as NodeType, diagram.snapGrid, id, shape);
    const setName = useCallback<OnEditableNodeLabelChange>(
      (newName: string) => {
        dmnEditorStoreApi.setState((state) => {
          renameDrgElement({ definitions: state.dmn.model.definitions, newName, index });
        });
      },
      [dmnEditorStoreApi, index]
    );

    const { allFeelVariableUniqueNames } = useDmnEditorDerivedStore();

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className} ${dmnObjectQName.prefix ? "external" : ""}`}>
          <KnowledgeSourceNodeSvg {...nodeDimensions} x={0} y={0} />
        </svg>

        <PositionalTargetNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />

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
            position={"center-left"}
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            value={knowledgeSource["@_label"] ?? knowledgeSource["@_name"]}
            onChange={setName}
            skipValidation={true}
            allUniqueNames={allFeelVariableUniqueNames}
          />
          {isHovered && <NodeResizerHandle snapGrid={diagram.snapGrid} nodeId={id} nodeShapeIndex={shape.index} />}
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
    const isResizing = useNodeResizing(id);

    const diagram = useDmnEditorStore((s) => s.diagram);
    const isHovered = (useIsHovered(ref) || isResizing) && diagram.draggingNodes.length === 0;

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();
    useHoveredNodeAlwaysOnTop(ref, zIndex, isHovered, dragging, selected, isEditingLabel);

    const dmnEditorStoreApi = useDmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);
    const nodeDimensions = useNodeDimensions(type as NodeType, diagram.snapGrid, id, shape);
    const setText = useCallback(
      (newText: string) => {
        dmnEditorStoreApi.setState((state) => {
          updateTextAnnotation({ definitions: state.dmn.model.definitions, newText, index });
        });
      },
      [dmnEditorStoreApi, index]
    );

    const { allFeelVariableUniqueNames } = useDmnEditorDerivedStore();

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className} ${dmnObjectQName.prefix ? "external" : ""}`}>
          <TextAnnotationNodeSvg {...nodeDimensions} x={0} y={0} />
        </svg>

        <PositionalTargetNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />

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
            namedElement={undefined}
            namedElementQName={undefined}
            position={"top-left"}
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            value={textAnnotation["@_label"] ?? textAnnotation.text}
            onChange={setText}
            skipValidation={true}
            allUniqueNames={allFeelVariableUniqueNames}
          />
          {isHovered && <NodeResizerHandle snapGrid={diagram.snapGrid} nodeId={id} nodeShapeIndex={shape.index} />}
        </div>
      </>
    );
  }
);

export const DecisionServiceNode = React.memo(
  ({
    data: { dmnObject: decisionService, shape, index, dmnObjectQName },
    selected,
    dragging,
    zIndex,
    type,
    id,
  }: RF.NodeProps<DmnDiagramNodeData<DMN15__tDecisionService & { __$$element: "decisionService" }>>) => {
    const ref = useRef<SVGRectElement>(null);
    const isResizing = useNodeResizing(id);

    const diagram = useDmnEditorStore((s) => s.diagram);
    const isHovered = (useIsHovered(ref) || isResizing) && diagram.draggingNodes.length === 0;

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();
    useHoveredNodeAlwaysOnTop(ref, zIndex, isHovered, dragging, selected, isEditingLabel);
    const dmnEditorStoreApi = useDmnEditorStoreApi();

    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);

    const nodeDimensions = useNodeDimensions(type as NodeType, diagram.snapGrid, id, shape);
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
          state.diagram.selectedNodes = [
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
          (state.dmn.model.definitions.drgElement![index] as DMN15__tInputData).variable!["@_typeRef"] = newTypeRef;
        });
      },
      [dmnEditorStoreApi, index]
    );

    const { allFeelVariableUniqueNames } = useDmnEditorDerivedStore();

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className} ${dmnObjectQName.prefix ? "external" : ""}`}>
          <DecisionServiceNodeSvg
            ref={ref}
            {...nodeDimensions}
            x={0}
            y={0}
            strokeWidth={3}
            isCollapsed={shape["@_isCollapsed"]}
            showSectionLabels={diagram.dropTargetNode?.id === id}
            dividerLineLocalY={getDecisionServiceDividerLineLocalY(shape)}
          />
        </svg>

        <PositionalTargetNodeHandles isTargeted={isTargeted && isValidConnectionTarget} nodeId={id} />

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
            position={"top-center"}
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            value={decisionService["@_label"] ?? decisionService["@_name"]}
            onChange={setName}
            allUniqueNames={allFeelVariableUniqueNames}
          />
          {selected && !dragging && !shape["@_isCollapsed"] && (
            <NodeResizerHandle snapGrid={diagram.snapGrid} nodeId={id} nodeShapeIndex={shape.index} />
          )}
          {shape["@_isCollapsed"] && <div className={"kie-dmn-editor--decision-service-collapsed-button"}>+</div>}
          <DataTypeNodePanel
            isVisible={!isTargeted && selected && !dragging}
            variable={decisionService.variable}
            shape={shape}
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
    const isResizing = useNodeResizing(id);

    const diagram = useDmnEditorStore((s) => s.diagram);
    const isHovered = (useIsHovered(ref) || isResizing) && diagram.draggingNodes.length === 0;

    const dmnEditorStoreApi = useDmnEditorStoreApi();
    const reactFlow = RF.useReactFlow<DmnDiagramNodeData, DmnDiagramEdgeData>();

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();
    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);
    const nodeDimensions = useNodeDimensions(type as NodeType, diagram.snapGrid, id, shape);
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
          state.diagram.selectedNodes = reactFlow
            .getNodes()
            .flatMap((n) =>
              getContainmentRelationship({ bounds: n.data.shape["dc:Bounds"]!, container: shape["dc:Bounds"]! })
                .isInside
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

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className} ${dmnObjectQName.prefix ? "external" : ""}`}>
          <GroupNodeSvg ref={ref} {...nodeDimensions} x={0} y={0} strokeWidth={3} />
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
            namedElement={undefined}
            namedElementQName={undefined}
            position={"top-left"}
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            value={group["@_label"] ?? group["@_name"]}
            onChange={setName}
            skipValidation={true}
            allUniqueNames={allFeelVariableUniqueNames}
          />
          {selected && !dragging && (
            <NodeResizerHandle snapGrid={diagram.snapGrid} nodeId={id} nodeShapeIndex={shape.index} />
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

export function NodeResizerHandle(props: { snapGrid: SnapGrid; nodeId: string; nodeShapeIndex: number }) {
  const minSize = MIN_SIZE_FOR_NODES(props.snapGrid);
  return (
    <RF.NodeResizeControl style={resizerControlStyle} minWidth={minSize.width} minHeight={minSize.height}>
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
function useNodeDimensions(type: NodeType, snapGrid: SnapGrid, id: string, shape: DMNDI15__DMNShape): RF.Dimensions {
  const node = RF.useStore((s) => s.nodeInternals.get(id));
  if (!node) {
    throw new Error("Can't use nodeInternals of non-existent node " + id);
  }

  if (type === NODE_TYPES.decisionService && shape["@_isCollapsed"]) {
    return DECISION_SERVICE_COLLAPSED_DIMENSIONS;
  }

  return {
    width: node.width ?? snapShapeDimensions(snapGrid, shape).width,
    height: node.height ?? snapShapeDimensions(snapGrid, shape).height,
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
