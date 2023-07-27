import {
  DMN14__tBusinessKnowledgeModel,
  DMN14__tDecision,
  DMN14__tDecisionService,
  DMN14__tGroup,
  DMN14__tInputData,
  DMN14__tKnowledgeSource,
  DMN14__tTextAnnotation,
  DMNDI13__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef } from "react";
import * as RF from "reactflow";
import { NodeHandles } from "../connections/NodeHandles";
import { outgoing } from "../connections/graphStructure";
import { MIN_SIZE_FOR_NODES, snapShapeDimensions } from "../SnapGrid";
import { EDGE_TYPES } from "../edges/EdgeTypes";
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
import { DataTypeNodePanel } from "./DataTypeNodePanel";
import { useDmnEditor } from "../../store/Store";

export const InputDataNode = React.memo(
  ({
    data: { inputData, shape, index },
    selected,
    dragging,
    id,
  }: RF.NodeProps<{ inputData: DMN14__tInputData; shape: DMNDI13__DMNShape; index: number }>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isResizing = useNodeResizing(id);
    const isHovered = (useNodeHovered(ref) || isResizing) && !dragging;

    useHoveredNodeAlwaysOnTop(ref, isHovered, dragging, selected);

    const { isTargeted, isValidTarget, isConnecting } = useTargetStatus(id, isHovered);
    const className = useNodeClassName(isConnecting, isValidTarget, id);
    const nodeDimensions = useNodeDimensions(id, shape);

    const { dispatch } = useDmnEditor();
    const setName = useCallback(
      (name: string) => {
        dispatch.dmn.set((state) => (state.definitions.drgElement![index]["@_name"] = name));
      },
      [dispatch.dmn, index]
    );

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className}`}>
          <InputDataNodeSvg {...nodeDimensions} x={0} y={0} />
        </svg>
        <NodeHandles isTargeted={isTargeted && isValidTarget} />

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
            nodes={outgoing.inputData.nodes}
            edges={outgoing.inputData.edges}
          />
          <EditableNodeLabel
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            value={inputData["@_label"] ?? inputData["@_name"]}
            onChange={setName}
          />
          {isHovered && <NodeResizerHandle />}
        </div>
      </>
    );
  }
);

export const DecisionNode = React.memo(
  ({
    data: { decision, shape, index },
    selected,
    dragging,
    id,
  }: RF.NodeProps<{
    decision: DMN14__tDecision;
    shape: DMNDI13__DMNShape;
    index: number;
  }>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isResizing = useNodeResizing(id);
    const isHovered = (useNodeHovered(ref) || isResizing) && !dragging;

    useHoveredNodeAlwaysOnTop(ref, isHovered, dragging, selected);

    const { isTargeted, isValidTarget, isConnecting } = useTargetStatus(id, isHovered);
    const className = useNodeClassName(isConnecting, isValidTarget, id);

    const nodeDimensions = useNodeDimensions(id, shape);

    const { dispatch } = useDmnEditor();
    const setName = useCallback(
      (name: string) => {
        dispatch.dmn.set((state) => (state.definitions.drgElement![index]["@_name"] = name));
      },
      [dispatch.dmn, index]
    );

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className}`}>
          <DecisionNodeSvg {...nodeDimensions} x={0} y={0} />
        </svg>
        <NodeHandles isTargeted={isTargeted && isValidTarget} />

        <div
          ref={ref}
          className={`kie-dmn-editor--node kie-dmn-editor--decision-node ${className}`}
          tabIndex={-1}
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
        >
          <InfoNodePanel isVisible={!isTargeted && isHovered} />
          <DataTypeNodePanel isVisible={!isTargeted && isHovered} variable={decision.variable} shape={shape} />
          <EditExpressionNodePanel
            isVisible={!isTargeted && isHovered}
            nodeWithExpression={useMemo(() => ({ type: NODE_TYPES.decision, content: decision }), [decision])}
          />
          <OutgoingStuffNodePanel
            isVisible={!isConnecting && !isTargeted && isHovered}
            nodes={outgoing.decision.nodes}
            edges={outgoing.decision.edges}
          />
          <EditableNodeLabel
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            value={decision["@_label"] ?? decision["@_name"]}
            onChange={setName}
          />
          {isHovered && <NodeResizerHandle />}
        </div>
      </>
    );
  }
);

export const BkmNode = React.memo(
  ({
    data: { bkm, shape, index },
    selected,
    dragging,
    id,
  }: RF.NodeProps<{
    bkm: DMN14__tBusinessKnowledgeModel;
    shape: DMNDI13__DMNShape;
    index: number;
  }>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isResizing = useNodeResizing(id);
    const isHovered = (useNodeHovered(ref) || isResizing) && !dragging;

    useHoveredNodeAlwaysOnTop(ref, isHovered, dragging, selected);

    const { isTargeted, isValidTarget, isConnecting } = useTargetStatus(id, isHovered);
    const className = useNodeClassName(isConnecting, isValidTarget, id);

    const nodeDimensions = useNodeDimensions(id, shape);

    const { dispatch } = useDmnEditor();
    const setName = useCallback(
      (name: string) => {
        dispatch.dmn.set((state) => (state.definitions.drgElement![index]["@_name"] = name));
      },
      [dispatch.dmn, index]
    );

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className}`}>
          <BkmNodeSvg {...nodeDimensions} x={0} y={0} />
        </svg>
        <NodeHandles isTargeted={isTargeted && isValidTarget} />

        <div
          ref={ref}
          className={`kie-dmn-editor--node kie-dmn-editor--bkm-node ${className}`}
          tabIndex={-1}
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
        >
          <InfoNodePanel isVisible={!isTargeted && isHovered} />
          <DataTypeNodePanel isVisible={!isTargeted && isHovered} variable={bkm.variable} shape={shape} />
          <EditExpressionNodePanel
            isVisible={!isTargeted && isHovered}
            nodeWithExpression={useMemo(() => ({ type: NODE_TYPES.bkm, content: bkm }), [bkm])}
          />
          <OutgoingStuffNodePanel
            isVisible={!isConnecting && !isTargeted && isHovered}
            nodes={outgoing.bkm.nodes}
            edges={outgoing.bkm.edges}
          />
          <EditableNodeLabel
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            value={bkm["@_label"] ?? bkm["@_name"]}
            onChange={setName}
          />
          {isHovered && <NodeResizerHandle />}
        </div>
      </>
    );
  }
);

export const KnowledgeSourceNode = React.memo(
  ({
    data: { knowledgeSource, shape, index },
    selected,
    dragging,
    id,
  }: RF.NodeProps<{ knowledgeSource: DMN14__tKnowledgeSource; shape: DMNDI13__DMNShape; index: number }>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isResizing = useNodeResizing(id);
    const isHovered = (useNodeHovered(ref) || isResizing) && !dragging;

    useHoveredNodeAlwaysOnTop(ref, isHovered, dragging, selected);

    const { isTargeted, isValidTarget, isConnecting } = useTargetStatus(id, isHovered);
    const className = useNodeClassName(isConnecting, isValidTarget, id);

    const nodeDimensions = useNodeDimensions(id, shape);

    const { dispatch } = useDmnEditor();
    const setName = useCallback(
      (name: string) => {
        dispatch.dmn.set((state) => (state.definitions.drgElement![index]["@_name"] = name));
      },
      [dispatch.dmn, index]
    );

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className}`}>
          <KnowledgeSourceNodeSvg {...nodeDimensions} x={0} y={0} />
        </svg>
        <NodeHandles isTargeted={isTargeted && isValidTarget} />

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
            nodes={outgoing.knowledgeSource.nodes}
            edges={outgoing.knowledgeSource.edges}
          />
          <EditableNodeLabel
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            value={knowledgeSource["@_label"] ?? knowledgeSource["@_name"]}
            onChange={setName}
          />
          {isHovered && <NodeResizerHandle />}
        </div>
      </>
    );
  }
);

export const TextAnnotationNode = React.memo(
  ({
    data: { textAnnotation, shape, index },
    selected,
    dragging,
    id,
  }: RF.NodeProps<{ textAnnotation: DMN14__tTextAnnotation; shape: DMNDI13__DMNShape; index: number }>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isResizing = useNodeResizing(id);
    const isHovered = (useNodeHovered(ref) || isResizing) && !dragging;

    useHoveredNodeAlwaysOnTop(ref, isHovered, dragging, selected);

    const { isTargeted, isValidTarget, isConnecting } = useTargetStatus(id, isHovered);
    const className = useNodeClassName(isConnecting, isValidTarget, id);

    const nodeDimensions = useNodeDimensions(id, shape);

    const { dispatch } = useDmnEditor();
    const setText = useCallback(
      (newText: string) => {
        dispatch.dmn.set((state) => ((state.definitions.artifact![index] as DMN14__tTextAnnotation).text = newText));
      },
      [dispatch.dmn, index]
    );

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className}`}>
          <TextAnnotationNodeSvg {...nodeDimensions} x={0} y={0} />
        </svg>
        <NodeHandles isTargeted={isTargeted && isValidTarget} />

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
            nodes={outgoing.textAnnotation.nodes}
            edges={outgoing.textAnnotation.edges}
          />
          <EditableNodeLabel
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            value={textAnnotation["@_label"] ?? textAnnotation.text}
            onChange={setText}
          />
          {isHovered && <NodeResizerHandle />}
        </div>
      </>
    );
  }
);

export const DecisionServiceNode = React.memo(
  ({
    data: { decisionService, shape, index },
    selected,
    dragging,
    id,
  }: RF.NodeProps<{ decisionService: DMN14__tDecisionService; shape: DMNDI13__DMNShape; index: number }>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isResizing = useNodeResizing(id);
    const isHovered = (useNodeHovered(ref) || isResizing) && !dragging;

    useHoveredNodeAlwaysOnTop(ref, isHovered, dragging, selected);

    const { isTargeted, isValidTarget, isConnecting } = useTargetStatus(id, isHovered);
    const className = useNodeClassName(isConnecting, isValidTarget, id);

    const nodeDimensions = useNodeDimensions(id, shape);

    const { dispatch } = useDmnEditor();
    const setName = useCallback(
      (name: string) => {
        dispatch.dmn.set((state) => (state.definitions.drgElement![index]["@_name"] = name));
      },
      [dispatch.dmn, index]
    );

    const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();

    return (
      <>
        <svg className={`kie-dmn-editor--node-shape ${className}`}>
          <DecisionServiceNodeSvg {...nodeDimensions} x={0} y={0} />
        </svg>
        <NodeHandles isTargeted={isTargeted && isValidTarget} />

        {isHovered && <NodeResizerHandle />}
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
            nodes={outgoing.decisionService.nodes}
            edges={outgoing.decisionService.edges}
          />
          <EditableNodeLabel
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            value={decisionService["@_label"] ?? decisionService["@_name"]}
            onChange={setName}
          />
        </div>
      </>
    );
  }
);

export const GroupNode = React.memo(
  ({
    data: { group, shape, index },
    selected,
    dragging,
    id,
  }: RF.NodeProps<{ group: DMN14__tGroup; shape: DMNDI13__DMNShape; index: number }>) => {
    const ref = useRef<HTMLDivElement>(null);
    const isResizing = useNodeResizing(id);
    const isHovered = (useNodeHovered(ref) || isResizing) && !dragging;

    const { isTargeted, isValidTarget, isConnecting } = useTargetStatus(id, isHovered);
    const className = useNodeClassName(isConnecting, isValidTarget, id);

    const nodeDimensions = useNodeDimensions(id, shape);
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

export function NodeResizerHandle(props: {}) {
  return (
    <RF.NodeResizeControl
      style={resizerControlStyle}
      minWidth={MIN_SIZE_FOR_NODES.width}
      minHeight={MIN_SIZE_FOR_NODES.height}
    >
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

// Hooks

export function useNodeHovered(ref: React.RefObject<HTMLElement>) {
  const [isHovered, setHovered] = React.useState(false);

  useEffect(() => {
    function onEnter() {
      setHovered(true);
    }

    function onLeave() {
      setHovered(false);
    }

    const r = ref.current;

    r?.addEventListener("mouseenter", onEnter);
    r?.addEventListener("mouseleave", onLeave);
    return () => {
      r?.removeEventListener("mouseleave", onLeave);
      r?.removeEventListener("mouseenter", onEnter);
    };
  }, [ref]);

  return isHovered;
}

function useNodeResizing(id: string): boolean {
  const node = RF.useStore(useCallback((state) => state.nodeInternals.get(id), [id]));
  if (!node) {
    throw new Error("Can't use nodeInternals of non-existent node " + id);
  }

  return node.resizing ?? false;
}
function useNodeDimensions(id: string, shape: DMNDI13__DMNShape): RF.Dimensions {
  const node = RF.useStore(useCallback((state) => state.nodeInternals.get(id), [id]));
  if (!node) {
    throw new Error("Can't use nodeInternals of non-existent node " + id);
  }

  return {
    width: node.width ?? snapShapeDimensions(shape).width,
    height: node.height ?? snapShapeDimensions(shape).height,
  };
}

// FIXME: Minor blinking occurs when node is selected & hovered and Esc is pressed to deselect. Not always, though.
function useHoveredNodeAlwaysOnTop(
  ref: React.RefObject<HTMLDivElement>,
  isHovered: boolean,
  dragging: boolean,
  selected: boolean
) {
  useEffect(() => {
    setTimeout(() => {
      ref.current!.parentElement!.style.zIndex = `${isHovered || dragging ? 1200 : 10}`;
    }, 0);
  }, [dragging, isHovered, ref, selected]);
}

export function useTargetStatus(id: string, isHovered: boolean) {
  const connectionNodeId = RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const connectionHandleId = RF.useStore(useCallback((state) => state.connectionHandleId, []));
  const isValidConnection = RF.useStore(useCallback((state) => state.isValidConnection, []));
  const isTargeted = !!connectionNodeId && connectionNodeId !== id && isHovered;

  const isValidTarget =
    isValidConnection?.({
      source: connectionNodeId,
      target: id,
      sourceHandle: connectionHandleId,
      targetHandle: null, // We don't use targetHandles, as target handles are only different in position, not in semantic.
    }) ?? false;

  return { isTargeted, isValidTarget, isConnecting: !!connectionNodeId };
}

export function useNodeClassName(isConnecting: boolean, isValidTarget: boolean, id: string) {
  const connectionHandleId = RF.useStore(useCallback((state) => state.connectionHandleId, []));
  const connectionNodeId = RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const isEdgeConnection = !!Object.values(EDGE_TYPES).find((s) => s === connectionHandleId);
  const isNodeConnection = !!Object.values(NODE_TYPES).find((s) => s === connectionHandleId);

  if (isNodeConnection && isConnecting && connectionNodeId !== id) {
    return "dimmed";
  }

  if (isEdgeConnection && isConnecting && !isValidTarget) {
    return "dimmed";
  }

  return "normal";
}
