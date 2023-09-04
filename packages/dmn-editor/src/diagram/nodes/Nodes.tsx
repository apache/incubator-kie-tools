import {
  DMN15__tBusinessKnowledgeModel,
  DMN15__tDecision,
  DMN15__tDecisionService,
  DMN15__tGroup,
  DMN15__tInputData,
  DMN15__tKnowledgeSource,
  DMN15__tTextAnnotation,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import * as React from "react";
import { useCallback, useEffect, useRef } from "react";
import * as RF from "reactflow";
import { renameDrgElement, updateTextAnnotation } from "../../mutations/renameNode";
import { DropTargetNode, SnapGrid, useDmnEditorStore, useDmnEditorStoreApi } from "../../store/Store";
import { MIN_SIZE_FOR_NODES, snapShapeDimensions } from "../SnapGrid";
import { NodeHandles } from "../connections/NodeHandles";
import { NodeType, containment, outgoing } from "../connections/graphStructure";
import { EDGE_TYPES } from "../edges/EdgeTypes";
import { DataTypeNodePanel } from "./DataTypeNodePanel";
import { EditExpressionNodePanel } from "./EditExpressionNodePanel";
import { EditableNodeLabel, useEditableNodeLabel } from "./EditableNodeLabel";
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
import { getDecisionServiceDivierLineLocalY as getDecisionServiceDividerLineLocalY } from "../maths/DmnMaths";
import { isValidContainment } from "../connections/isValidContainment";
import { useDmnEditorDerivedStore } from "../../store/DerivedStore";

export type DmnEditorDiagramNodeData<T> = {
  dmnObject: T;
  shape: DMNDI15__DMNShape & { index: number };
  index: number;
};

export const InputDataNode = React.memo(
  ({
    data: { dmnObject: inputData, shape, index },
    selected,
    dragging,
    zIndex,
    id,
  }: RF.NodeProps<DmnEditorDiagramNodeData<DMN15__tInputData>>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isResizing = useNodeResizing(id);
    const isHovered = (useIsHovered(ref) || isResizing) && !dragging;

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();
    useHoveredNodeAlwaysOnTop(ref, zIndex, isHovered, dragging, selected, isEditingLabel);

    const dmnEditorStoreApi = useDmnEditorStoreApi();
    const diagram = useDmnEditorStore((s) => s.diagram);

    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);
    const nodeDimensions = useNodeDimensions(diagram.snapGrid, id, shape);

    const setName = useCallback(
      (newName: string) => {
        dmnEditorStoreApi.setState((state) => {
          renameDrgElement({ definitions: state.dmn.model.definitions, newName, index });
        });
      },
      [dmnEditorStoreApi, index]
    );

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className}`}>
          <InputDataNodeSvg {...nodeDimensions} x={0} y={0} />
        </svg>
        <NodeHandles isTargeted={isTargeted && isValidConnectionTarget} />

        <div
          ref={ref}
          className={`kie-dmn-editor--node kie-dmn-editor--input-data-node ${className}`}
          tabIndex={-1}
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
        >
          <InfoNodePanel isVisible={!isTargeted && isHovered} />
          <DataTypeNodePanel isVisible={!isTargeted && isHovered} variable={inputData.variable} shape={shape} />
          <OutgoingStuffNodePanel
            isVisible={!isConnecting && !isTargeted && isHovered}
            nodes={outgoing[NODE_TYPES.inputData].nodes}
            edges={outgoing[NODE_TYPES.inputData].edges}
          />
          <EditableNodeLabel
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            value={inputData["@_label"] ?? inputData["@_name"]}
            onChange={setName}
          />
          {isHovered && <NodeResizerHandle snapGrid={diagram.snapGrid} nodeId={id} nodeShapeIndex={shape.index} />}
        </div>
      </>
    );
  }
);

export const DecisionNode = React.memo(
  ({
    data: { dmnObject: decision, shape, index },
    selected,
    dragging,
    zIndex,
    id,
  }: RF.NodeProps<DmnEditorDiagramNodeData<DMN15__tDecision>>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isResizing = useNodeResizing(id);
    const isHovered = (useIsHovered(ref) || isResizing) && !dragging;

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();
    useHoveredNodeAlwaysOnTop(ref, zIndex, isHovered, dragging, selected, isEditingLabel);

    const dmnEditorStoreApi = useDmnEditorStoreApi();
    const diagram = useDmnEditorStore((s) => s.diagram);

    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);
    const nodeDimensions = useNodeDimensions(diagram.snapGrid, id, shape);
    const setName = useCallback(
      (newName: string) => {
        dmnEditorStoreApi.setState((state) => {
          renameDrgElement({ definitions: state.dmn.model.definitions, newName, index });
        });
      },
      [dmnEditorStoreApi, index]
    );

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className}`}>
          <DecisionNodeSvg {...nodeDimensions} x={0} y={0} />
        </svg>
        <NodeHandles isTargeted={isTargeted && isValidConnectionTarget} />

        <div
          ref={ref}
          className={`kie-dmn-editor--node kie-dmn-editor--decision-node ${className}`}
          tabIndex={-1}
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
        >
          <InfoNodePanel isVisible={!isTargeted && isHovered} />
          <DataTypeNodePanel isVisible={!isTargeted && isHovered} variable={decision.variable} shape={shape} />
          <EditExpressionNodePanel isVisible={!isTargeted && isHovered} id={decision["@_id"]!} />
          <OutgoingStuffNodePanel
            isVisible={!isConnecting && !isTargeted && isHovered}
            nodes={outgoing[NODE_TYPES.decision].nodes}
            edges={outgoing[NODE_TYPES.decision].edges}
          />
          <EditableNodeLabel
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            value={decision["@_label"] ?? decision["@_name"]}
            onChange={setName}
          />
          {isHovered && <NodeResizerHandle snapGrid={diagram.snapGrid} nodeId={id} nodeShapeIndex={shape.index} />}
        </div>
      </>
    );
  }
);

export const BkmNode = React.memo(
  ({
    data: { dmnObject: bkm, shape, index },
    selected,
    dragging,
    zIndex,
    id,
  }: RF.NodeProps<DmnEditorDiagramNodeData<DMN15__tBusinessKnowledgeModel>>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isResizing = useNodeResizing(id);
    const isHovered = (useIsHovered(ref) || isResizing) && !dragging;

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();
    useHoveredNodeAlwaysOnTop(ref, zIndex, isHovered, dragging, selected, isEditingLabel);

    const dmnEditorStoreApi = useDmnEditorStoreApi();
    const diagram = useDmnEditorStore((s) => s.diagram);

    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);
    const nodeDimensions = useNodeDimensions(diagram.snapGrid, id, shape);
    const setName = useCallback(
      (newName: string) => {
        dmnEditorStoreApi.setState((state) => {
          renameDrgElement({ definitions: state.dmn.model.definitions, newName, index });
        });
      },
      [dmnEditorStoreApi, index]
    );

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className}`}>
          <BkmNodeSvg {...nodeDimensions} x={0} y={0} />
        </svg>
        <NodeHandles isTargeted={isTargeted && isValidConnectionTarget} />

        <div
          ref={ref}
          className={`kie-dmn-editor--node kie-dmn-editor--bkm-node ${className}`}
          tabIndex={-1}
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
        >
          <InfoNodePanel isVisible={!isTargeted && isHovered} />
          <DataTypeNodePanel isVisible={!isTargeted && isHovered} variable={bkm.variable} shape={shape} />
          <EditExpressionNodePanel isVisible={!isTargeted && isHovered} id={bkm["@_id"]!} />
          <OutgoingStuffNodePanel
            isVisible={!isConnecting && !isTargeted && isHovered}
            nodes={outgoing[NODE_TYPES.bkm].nodes}
            edges={outgoing[NODE_TYPES.bkm].edges}
          />
          <EditableNodeLabel
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            value={bkm["@_label"] ?? bkm["@_name"]}
            onChange={setName}
          />
          {isHovered && <NodeResizerHandle snapGrid={diagram.snapGrid} nodeId={id} nodeShapeIndex={shape.index} />}
        </div>
      </>
    );
  }
);

export const KnowledgeSourceNode = React.memo(
  ({
    data: { dmnObject: knowledgeSource, shape, index },
    selected,
    dragging,
    zIndex,
    id,
  }: RF.NodeProps<DmnEditorDiagramNodeData<DMN15__tKnowledgeSource>>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isResizing = useNodeResizing(id);
    const isHovered = (useIsHovered(ref) || isResizing) && !dragging;

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();
    useHoveredNodeAlwaysOnTop(ref, zIndex, isHovered, dragging, selected, isEditingLabel);

    const dmnEditorStoreApi = useDmnEditorStoreApi();
    const diagram = useDmnEditorStore((s) => s.diagram);

    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);
    const nodeDimensions = useNodeDimensions(diagram.snapGrid, id, shape);
    const setName = useCallback(
      (newName: string) => {
        dmnEditorStoreApi.setState((state) => {
          renameDrgElement({ definitions: state.dmn.model.definitions, newName, index });
        });
      },
      [dmnEditorStoreApi, index]
    );

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className}`}>
          <KnowledgeSourceNodeSvg {...nodeDimensions} x={0} y={0} />
        </svg>
        <NodeHandles isTargeted={isTargeted && isValidConnectionTarget} />

        <div
          ref={ref}
          className={`kie-dmn-editor--node kie-dmn-editor--knowledge-source-node ${className}`}
          tabIndex={-1}
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
        >
          <InfoNodePanel isVisible={!isTargeted && isHovered} />
          <OutgoingStuffNodePanel
            isVisible={!isConnecting && !isTargeted && isHovered}
            nodes={outgoing[NODE_TYPES.knowledgeSource].nodes}
            edges={outgoing[NODE_TYPES.knowledgeSource].edges}
          />
          <EditableNodeLabel
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            value={knowledgeSource["@_label"] ?? knowledgeSource["@_name"]}
            onChange={setName}
          />
          {isHovered && <NodeResizerHandle snapGrid={diagram.snapGrid} nodeId={id} nodeShapeIndex={shape.index} />}
        </div>
      </>
    );
  }
);

export const TextAnnotationNode = React.memo(
  ({
    data: { dmnObject: textAnnotation, shape, index },
    selected,
    dragging,
    zIndex,
    id,
  }: RF.NodeProps<DmnEditorDiagramNodeData<DMN15__tTextAnnotation>>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isResizing = useNodeResizing(id);
    const isHovered = (useIsHovered(ref) || isResizing) && !dragging;

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();
    useHoveredNodeAlwaysOnTop(ref, zIndex, isHovered, dragging, selected, isEditingLabel);

    const dmnEditorStoreApi = useDmnEditorStoreApi();
    const diagram = useDmnEditorStore((s) => s.diagram);

    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);
    const nodeDimensions = useNodeDimensions(diagram.snapGrid, id, shape);
    const setText = useCallback(
      (newText: string) => {
        dmnEditorStoreApi.setState((state) => {
          updateTextAnnotation({ definitions: state.dmn.model.definitions, newText, index });
        });
      },
      [dmnEditorStoreApi, index]
    );

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className}`}>
          <TextAnnotationNodeSvg {...nodeDimensions} x={0} y={0} />
        </svg>
        <NodeHandles isTargeted={isTargeted && isValidConnectionTarget} />

        <div
          ref={ref}
          className={`kie-dmn-editor--node kie-dmn-editor--text-annotation-node ${className}`}
          tabIndex={-1}
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
        >
          <InfoNodePanel isVisible={!isTargeted && isHovered} />
          <OutgoingStuffNodePanel
            isVisible={!isConnecting && !isTargeted && isHovered}
            nodes={outgoing[NODE_TYPES.textAnnotation].nodes}
            edges={outgoing[NODE_TYPES.textAnnotation].edges}
          />
          <EditableNodeLabel
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            position={"top-center"}
            value={textAnnotation["@_label"] ?? textAnnotation.text}
            onChange={setText}
          />
          {isHovered && <NodeResizerHandle snapGrid={diagram.snapGrid} nodeId={id} nodeShapeIndex={shape.index} />}
        </div>
      </>
    );
  }
);

export const DecisionServiceNode = React.memo(
  ({
    data: { dmnObject: decisionService, shape, index },
    selected,
    dragging,
    zIndex,
    id,
  }: RF.NodeProps<DmnEditorDiagramNodeData<DMN15__tDecisionService>>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isResizing = useNodeResizing(id);
    const isHovered = (useIsHovered(ref) || isResizing) && !dragging;

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();
    useHoveredNodeAlwaysOnTop(ref, zIndex, isHovered, dragging, selected, isEditingLabel);
    const dmnEditorStoreApi = useDmnEditorStoreApi();
    const diagram = useDmnEditorStore((s) => s.diagram);

    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);

    const nodeDimensions = useNodeDimensions(diagram.snapGrid, id, shape);
    const setName = useCallback(
      (newName: string) => {
        dmnEditorStoreApi.setState((state) => {
          renameDrgElement({ definitions: state.dmn.model.definitions, newName, index });
        });
      },
      [dmnEditorStoreApi, index]
    );

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className}`}>
          <DecisionServiceNodeSvg
            {...nodeDimensions}
            x={0}
            y={0}
            showSectionLabels={diagram.dropTargetNode?.id === id}
            dividerLineLocalY={getDecisionServiceDividerLineLocalY(shape)}
          />
        </svg>
        <NodeHandles isTargeted={isTargeted && isValidConnectionTarget} />

        <div
          ref={ref}
          className={`kie-dmn-editor--node kie-dmn-editor--decision-service-node ${className}`}
          tabIndex={-1}
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
        >
          <InfoNodePanel isVisible={!isTargeted && isHovered} />
          <DataTypeNodePanel isVisible={!isTargeted && isHovered} variable={decisionService.variable} shape={shape} />
          <OutgoingStuffNodePanel
            isVisible={!isConnecting && !isTargeted && isHovered}
            nodes={outgoing[NODE_TYPES.decisionService].nodes}
            edges={outgoing[NODE_TYPES.decisionService].edges}
          />
          <EditableNodeLabel
            position={"top-center"}
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            value={decisionService["@_label"] ?? decisionService["@_name"]}
            onChange={setName}
          />
          {isHovered && <NodeResizerHandle snapGrid={diagram.snapGrid} nodeId={id} nodeShapeIndex={shape.index} />}
        </div>
      </>
    );
  }
);

export const GroupNode = React.memo(
  ({
    data: { dmnObject: group, shape, index },
    selected,
    dragging,
    zIndex,
    id,
  }: RF.NodeProps<DmnEditorDiagramNodeData<DMN15__tGroup>>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isResizing = useNodeResizing(id);
    const isHovered = (useIsHovered(ref) || isResizing) && !dragging;

    const dmnEditorStoreApi = useDmnEditorStoreApi();
    const diagram = useDmnEditorStore((s) => s.diagram);

    const { isTargeted, isValidConnectionTarget, isConnecting } = useConnectionTargetStatus(id, isHovered);
    const className = useNodeClassName(diagram.dropTargetNode, isConnecting, isValidConnectionTarget, id);
    const nodeDimensions = useNodeDimensions(diagram.snapGrid, id, shape);
    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className}`}>
          <GroupNodeSvg {...nodeDimensions} x={0} y={0} />
        </svg>
        <div className={`kie-dmn-editor--node kie-dmn-editor--group-node`}>
          {group["@_label"] ?? group["@_name"] ?? <EmptyLabel />}
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
  const node = RF.useStore(useCallback((state) => state.nodeInternals.get(id), [id]));
  if (!node) {
    throw new Error("Can't use nodeInternals of non-existent node " + id);
  }

  return node.resizing ?? false;
}
function useNodeDimensions(snapGrid: SnapGrid, id: string, shape: DMNDI15__DMNShape): RF.Dimensions {
  const node = RF.useStore(useCallback((state) => state.nodeInternals.get(id), [id]));
  if (!node) {
    throw new Error("Can't use nodeInternals of non-existent node " + id);
  }

  return {
    width: node.width ?? snapShapeDimensions(snapGrid, shape).width,
    height: node.height ?? snapShapeDimensions(snapGrid, shape).height,
  };
}

function useHoveredNodeAlwaysOnTop(
  ref: React.RefObject<HTMLDivElement>,
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

export function useConnectionTargetStatus(id: string, isHovered: boolean) {
  const connectionNodeId = RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const connectionHandleId = RF.useStore(useCallback((state) => state.connectionHandleId, []));
  const isValidConnection = RF.useStore(useCallback((state) => state.isValidConnection, []));
  const isTargeted = !!connectionNodeId && connectionNodeId !== id && isHovered;

  const isValidConnectionTarget =
    isValidConnection?.({
      source: connectionNodeId,
      target: id,
      sourceHandle: connectionHandleId,
      targetHandle: null, // We don't use targetHandles, as target handles are only different in position, not in semantic.
    }) ?? false;

  return { isTargeted, isValidConnectionTarget, isConnecting: !!connectionNodeId };
}

export function useNodeClassName(
  dropTargetNode: DropTargetNode,
  isConnecting: boolean,
  isValidConnectionTarget: boolean,
  id: string
) {
  const { isDropTargetNodeValidForSelection } = useDmnEditorDerivedStore();
  const connectionHandleId = RF.useStore(useCallback((state) => state.connectionHandleId, []));
  const connectionNodeId = RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const isEdgeConnection = !!Object.values(EDGE_TYPES).find((s) => s === connectionHandleId);
  const isNodeConnection = !!Object.values(NODE_TYPES).find((s) => s === connectionHandleId);

  if (isNodeConnection && isConnecting && connectionNodeId !== id) {
    return "dimmed";
  }

  if (isEdgeConnection && isConnecting && !isValidConnectionTarget) {
    return "dimmed";
  }

  if (dropTargetNode?.id === id && containment.get(dropTargetNode.type)) {
    return isDropTargetNodeValidForSelection ? "drop-target" : "drop-target-invalid";
  }

  return "normal";
}
