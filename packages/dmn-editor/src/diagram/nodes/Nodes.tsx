import {
  DMN14__tBusinessKnowledgeModel,
  DMN14__tDecision,
  DMN14__tDecisionService,
  DMN14__tGroup,
  DMN14__tInformationItem,
  DMN14__tInputData,
  DMN14__tKnowledgeSource,
  DMN14__tTextAnnotation,
  DMNDI13__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import * as React from "react";
import { useCallback, useEffect, useLayoutEffect, useRef, useState } from "react";
import * as RF from "reactflow";
import { NodeHandles } from "../connections/NodeHandles";
import { outgoing } from "../connections/graphStructure";

import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { InfoIcon } from "@patternfly/react-icons/dist/js/icons/info-icon";
import { MIN_SIZE_FOR_NODES, snapShapeDimensions } from "../SnapGrid";
import { DmnNodeWithExpression } from "./DmnNodeWithExpression";
import { NODE_TYPES } from "./NodeTypes";
import { EDGE_TYPES } from "../edges/EdgeTypes";
import { OutgoingStuffNodePanel } from "./OutgoingStuffNodePanel";
import { EditableNodeLabel, useEditableNodeLabel as useEditableNodeLabel } from "./EditableNodeLabel";

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
function useHoveredNodeAlwaysOnTop(ref: React.RefObject<HTMLDivElement>, isHovered: boolean, selected: boolean) {
  useEffect(() => {
    setTimeout(() => {
      ref.current!.parentElement!.style.zIndex = `${isHovered ? 1200 : 10}`;
    }, 0);
  }, [isHovered, ref, selected]);
}

export function InputDataNode({
  data: { inputData, shape, onInfo },
  selected,
  dragging,
  id,
}: RF.NodeProps<{ inputData: DMN14__tInputData; shape: DMNDI13__DMNShape; onInfo: () => void }>) {
  const ref = useRef<HTMLDivElement>(null);
  const isResizing = useNodeResizing(id);
  const isHovered = (useNodeHovered(ref) || isResizing) && !dragging;

  useHoveredNodeAlwaysOnTop(ref, isHovered, selected);

  const { isTargeted, isValidTarget, isConnecting } = useTargetStatus(id, isHovered);
  const className = useNodeClassName(isConnecting, isValidTarget, id);
  const nodeDimensions = useNodeDimensions(id, shape);

  const setName = useCallback((name: string) => {
    console.log(`TIAGO WRITE: Updating InputData name to ${name}`);
  }, []);

  const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();

  return (
    <>
      <svg className={`kie-dmn-editor--node-shape ${className}`}>
        <InputDataNodeSvg {...nodeDimensions} x={0} y={0} />
      </svg>
      <NodeHandles isTargeted={isTargeted && isValidTarget} />

      {/* <DataTypeToolbar variable={inputData.variable} shape={shape} /> */}
      <div
        ref={ref}
        className={`kie-dmn-editor--node kie-dmn-editor--input-data-node ${className}`}
        tabIndex={-1}
        onDoubleClick={triggerEditing}
        onKeyDown={triggerEditingIfEnter}
      >
        <OutgoingStuffNodePanel
          isVisible={!isConnecting && !isTargeted && isHovered}
          nodes={outgoing.inputData.nodes}
          edges={outgoing.inputData.edges}
        />
        <InfoNodePanel isVisible={!isTargeted && isHovered} onClick={onInfo} />
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

export function DecisionNode({
  data: { decision, shape, setOpenNodeWithExpression, onInfo },
  selected,
  dragging,
  id,
}: RF.NodeProps<{
  decision: DMN14__tDecision;
  shape: DMNDI13__DMNShape;
  setOpenNodeWithExpression: React.Dispatch<React.SetStateAction<DmnNodeWithExpression | undefined>>;
  onInfo: () => void;
}>) {
  const ref = useRef<HTMLDivElement>(null);
  const isResizing = useNodeResizing(id);
  const isHovered = (useNodeHovered(ref) || isResizing) && !dragging;

  useHoveredNodeAlwaysOnTop(ref, isHovered, selected);

  const { isTargeted, isValidTarget, isConnecting } = useTargetStatus(id, isHovered);
  const className = useNodeClassName(isConnecting, isValidTarget, id);

  const nodeDimensions = useNodeDimensions(id, shape);

  const setName = useCallback((name: string) => {
    console.log(`TIAGO WRITE: Updating Decision name to ${name}`);
  }, []);

  const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();

  return (
    <>
      <svg className={`kie-dmn-editor--node-shape ${className}`}>
        <DecisionNodeSvg {...nodeDimensions} x={0} y={0} />
      </svg>
      <NodeHandles isTargeted={isTargeted && isValidTarget} />

      {/* <DataTypeToolbar variable={decision.variable} shape={shape} /> */}
      <div
        ref={ref}
        className={`kie-dmn-editor--node kie-dmn-editor--decision-node ${className}`}
        tabIndex={-1}
        onDoubleClick={triggerEditing}
        onKeyDown={triggerEditingIfEnter}
      >
        <EditExpressionNodePanel
          isVisible={!isTargeted && isHovered}
          onClick={() => setOpenNodeWithExpression({ type: NODE_TYPES.decision, content: decision })}
        />
        <OutgoingStuffNodePanel
          isVisible={!isConnecting && !isTargeted && isHovered}
          nodes={outgoing.decision.nodes}
          edges={outgoing.decision.edges}
        />
        <InfoNodePanel isVisible={!isTargeted && isHovered} onClick={onInfo} />
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

export function BkmNode({
  data: { bkm, shape, setOpenNodeWithExpression, onInfo },
  selected,
  dragging,
  id,
}: RF.NodeProps<{
  bkm: DMN14__tBusinessKnowledgeModel;
  shape: DMNDI13__DMNShape;
  onInfo: () => void;
  setOpenNodeWithExpression: React.Dispatch<React.SetStateAction<DmnNodeWithExpression | undefined>>;
}>) {
  const ref = useRef<HTMLDivElement>(null);
  const isResizing = useNodeResizing(id);
  const isHovered = (useNodeHovered(ref) || isResizing) && !dragging;

  useHoveredNodeAlwaysOnTop(ref, isHovered, selected);

  const { isTargeted, isValidTarget, isConnecting } = useTargetStatus(id, isHovered);
  const className = useNodeClassName(isConnecting, isValidTarget, id);

  const nodeDimensions = useNodeDimensions(id, shape);

  const setName = useCallback((name: string) => {
    console.log(`TIAGO WRITE: Updating Bkm name to ${name}`);
  }, []);

  const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();

  return (
    <>
      <svg className={`kie-dmn-editor--node-shape ${className}`}>
        <BkmNodeSvg {...nodeDimensions} x={0} y={0} />
      </svg>
      <NodeHandles isTargeted={isTargeted && isValidTarget} />

      {/* <DataTypeToolbar variable={bkm.variable} shape={shape} /> */}
      <div
        ref={ref}
        className={`kie-dmn-editor--node kie-dmn-editor--bkm-node ${className}`}
        tabIndex={-1}
        onDoubleClick={triggerEditing}
        onKeyDown={triggerEditingIfEnter}
      >
        <EditExpressionNodePanel
          isVisible={!isTargeted && isHovered}
          onClick={() => setOpenNodeWithExpression({ type: NODE_TYPES.bkm, content: bkm })}
        />
        <OutgoingStuffNodePanel
          isVisible={!isConnecting && !isTargeted && isHovered}
          nodes={outgoing.bkm.nodes}
          edges={outgoing.bkm.edges}
        />
        <InfoNodePanel isVisible={!isTargeted && isHovered} onClick={onInfo} />
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

export function KnowledgeSourceNode({
  data: { knowledgeSource, shape, onInfo },
  selected,
  dragging,
  id,
}: RF.NodeProps<{ knowledgeSource: DMN14__tKnowledgeSource; shape: DMNDI13__DMNShape; onInfo: () => void }>) {
  const ref = useRef<HTMLDivElement>(null);
  const isResizing = useNodeResizing(id);
  const isHovered = (useNodeHovered(ref) || isResizing) && !dragging;

  useHoveredNodeAlwaysOnTop(ref, isHovered, selected);

  const { isTargeted, isValidTarget, isConnecting } = useTargetStatus(id, isHovered);
  const className = useNodeClassName(isConnecting, isValidTarget, id);

  const nodeDimensions = useNodeDimensions(id, shape);

  const setName = useCallback((name: string) => {
    console.log(`TIAGO WRITE: Updating KnowledgeSource name to ${name}`);
  }, []);

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
        <OutgoingStuffNodePanel
          isVisible={!isConnecting && !isTargeted && isHovered}
          nodes={outgoing.knowledgeSource.nodes}
          edges={outgoing.knowledgeSource.edges}
        />
        <InfoNodePanel isVisible={!isTargeted && isHovered} onClick={onInfo} />
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

export function TextAnnotationNode({
  data: { textAnnotation, shape, onInfo },
  selected,
  dragging,
  id,
}: RF.NodeProps<{ textAnnotation: DMN14__tTextAnnotation; shape: DMNDI13__DMNShape; onInfo: () => void }>) {
  const ref = useRef<HTMLDivElement>(null);
  const isResizing = useNodeResizing(id);
  const isHovered = (useNodeHovered(ref) || isResizing) && !dragging;

  useHoveredNodeAlwaysOnTop(ref, isHovered, selected);

  const { isTargeted, isValidTarget, isConnecting } = useTargetStatus(id, isHovered);
  const className = useNodeClassName(isConnecting, isValidTarget, id);

  const nodeDimensions = useNodeDimensions(id, shape);

  const setName = useCallback((name: string) => {
    console.log(`TIAGO WRITE: Updating TextAnnotation text to ${name}`);
  }, []);

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
        <OutgoingStuffNodePanel
          isVisible={!isConnecting && !isTargeted && isHovered}
          nodes={outgoing.textAnnotation.nodes}
          edges={outgoing.textAnnotation.edges}
        />
        <InfoNodePanel isVisible={!isTargeted && isHovered} onClick={onInfo} />
        <EditableNodeLabel
          isEditing={isEditingLabel}
          setEditing={setEditingLabel}
          value={textAnnotation["@_label"] ?? textAnnotation.text}
          onChange={setName}
        />
        {isHovered && <NodeResizerHandle />}
      </div>
    </>
  );
}

export function DecisionServiceNode({
  data: { decisionService, shape, onInfo },
  selected,
  dragging,
  id,
}: RF.NodeProps<{ decisionService: DMN14__tDecisionService; shape: DMNDI13__DMNShape; onInfo: () => void }>) {
  const ref = useRef<HTMLDivElement>(null);
  const isResizing = useNodeResizing(id);
  const isHovered = (useNodeHovered(ref) || isResizing) && !dragging;

  const { isTargeted, isValidTarget, isConnecting } = useTargetStatus(id, isHovered);
  const className = useNodeClassName(isConnecting, isValidTarget, id);

  const nodeDimensions = useNodeDimensions(id, shape);

  const setName = useCallback((name: string) => {
    console.log(`TIAGO WRITE: Updating DecisionService name to ${name}`);
  }, []);

  const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();

  return (
    <>
      <svg className={`kie-dmn-editor--node-shape ${className}`}>
        <DecisionServiceNodeSvg {...nodeDimensions} x={0} y={0} />
      </svg>
      <NodeHandles isTargeted={isTargeted && isValidTarget} />

      {isHovered && <NodeResizerHandle />}
      {/* <DataTypeToolbar variable={decisionService.variable} shape={shape} /> */}
      <div
        ref={ref}
        className={`kie-dmn-editor--node kie-dmn-editor--decision-service-node ${className}`}
        tabIndex={-1}
        onDoubleClick={triggerEditing}
        onKeyDown={triggerEditingIfEnter}
      >
        <OutgoingStuffNodePanel
          isVisible={!isConnecting && !isTargeted && isHovered}
          nodes={outgoing.decisionService.nodes}
          edges={outgoing.decisionService.edges}
        />
        <InfoNodePanel isVisible={!isTargeted && isHovered} onClick={onInfo} />
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

export function GroupNode({
  data: { group, shape },
  selected,
  dragging,
  id,
}: RF.NodeProps<{ group: DMN14__tGroup; shape: DMNDI13__DMNShape }>) {
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

export function DataTypeToolbar(props: {
  variable: DMN14__tInformationItem | undefined;
  shape: DMNDI13__DMNShape | undefined;
}) {
  return (
    <RF.NodeToolbar position={RF.Position.Bottom} align={"start"}>
      <Label
        style={{
          maxWidth: (props.shape?.["dc:Bounds"]?.["@_width"] ?? 0) - 16,
          background: "white",
          fontFamily: "monospace",
          paddingRight: "16px",
        }}
        isCompact={true}
      >{`🔹 ${props.variable?.["@_typeRef"] ?? "<Undefined>"}`}</Label>
    </RF.NodeToolbar>
  );
}

export const handleStyle: React.CSSProperties = {
  display: "flex",
  position: "unset",
  transform: "unset",
  // position: "relative",
};

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

export function EditExpressionNodePanel(props: { isVisible: boolean; onClick: () => void }) {
  return (
    <>
      {props.isVisible && (
        <Label onClick={props.onClick} className={"kie-dmn-editor--edit-expression-label"}>
          Edit
        </Label>
      )}
    </>
  );
}

export function InfoNodePanel(props: { isVisible: boolean; onClick: () => void }) {
  return (
    <>
      {props.isVisible && (
        <div className={"kie-dmn-editor--info-node-panel"}>
          <Label onClick={props.onClick} className={"kie-dmn-editor--info-label"}>
            <InfoIcon style={{ width: "0.7em", height: "0.7em" }} />
          </Label>
        </div>
      )}
    </>
  );
}

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

// Hooks

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

//

const DEFAULT_NODE_FILL = "white";
const DEFAULT_NODE_STROKE_WIDTH = 1.5;
const DEFAULT_NODE_STROKE_COLOR = "black";

// This function makes sure that independent of strokeWidth, the size and position of the element is preserved. Much like `box-sizing: border-box`;
export function normalize<T extends NodeSvgProps>(_props: T) {
  const { strokeWidth: _strokeWidth, x: _x, y: _y, width: _width, height: _height, ...props } = _props;

  const strokeWidth = _strokeWidth ?? DEFAULT_NODE_STROKE_WIDTH;
  const halfStrokeWidth = strokeWidth / 2;

  const x = _x + halfStrokeWidth;
  const y = _y + halfStrokeWidth;
  const width = _width - strokeWidth;
  const height = _height - strokeWidth;

  return { strokeWidth, x, y, width, height, props };
}

export function InputDataNodeSvg(_props: NodeSvgProps) {
  const { strokeWidth, x, y, width, height, props } = normalize(_props);
  const rx =
    typeof height === "number"
      ? height / 2
      : (() => {
          throw new Error("Can't calculate rx based on a string height.");
        })();

  const ry =
    typeof width === "number"
      ? width / 2
      : (() => {
          throw new Error("Can't calculate ry based on a string width.");
        })();

  return (
    <g>
      <rect
        {...props}
        x={x}
        y={y}
        width={width}
        height={height}
        fill={DEFAULT_NODE_FILL}
        stroke={DEFAULT_NODE_STROKE_COLOR}
        strokeLinejoin={"round"}
        strokeWidth={strokeWidth}
        rx={rx}
        ry={ry}
      />
    </g>
  );
}

export function DecisionNodeSvg(_props: NodeSvgProps) {
  return (
    <g>
      <rect
        {...normalize(_props)}
        fill={DEFAULT_NODE_FILL}
        stroke={DEFAULT_NODE_STROKE_COLOR}
        strokeLinejoin={"round"}
      />
    </g>
  );
}

export function BkmNodeSvg(_props: NodeSvgProps) {
  const { strokeWidth, x, y, width, height, props } = normalize(_props);
  const bevel = 25;
  return (
    <g>
      <polygon
        {...props}
        points={`${bevel},0 0,${bevel} 0,${height} ${width - bevel},${height} ${width},${height - bevel}, ${width},0`}
        fill={DEFAULT_NODE_FILL}
        stroke={DEFAULT_NODE_STROKE_COLOR}
        strokeWidth={strokeWidth}
        strokeLinejoin={"round"}
        transform={`translate(${x},${y})`}
      />
    </g>
  );
}

export function KnowledgeSourceNodeSvg(_props: NodeSvgProps) {
  const { strokeWidth, x, y, width, height, props } = normalize(_props);
  const quarterX = width / 4;
  const halfX = width / 2;
  const amplitude = 20;
  return (
    <g>
      <path
        {...props}
        d={`M0,${height - amplitude / 2} L0,0 M0,0 L${width},0 M${width},0 L${width},${height - amplitude / 2} Z`}
        stroke={DEFAULT_NODE_STROKE_COLOR}
        strokeWidth={strokeWidth}
        fill={DEFAULT_NODE_FILL}
        strokeLinejoin={"round"}
        transform={`translate(${x},${y})`}
      />
      <path
        d={`M0,0 Q${quarterX},${amplitude} ${halfX},0 T${width},0`}
        stroke={DEFAULT_NODE_STROKE_COLOR}
        fill={DEFAULT_NODE_FILL}
        strokeWidth={strokeWidth}
        strokeLinejoin={"round"}
        transform={`translate(${x},${y - amplitude / 2 + height})`}
      />
    </g>
  );
}

export function DecisionServiceNodeSvg(__props: NodeSvgProps & { dividerLineY?: number }) {
  const { strokeWidth, x, y, width, height, props: _props } = normalize(__props);
  const { dividerLineY, ...props } = _props;
  const cornerRadius = 40;
  return (
    <g>
      <rect
        {...props}
        x={x}
        y={y}
        width={width}
        height={height}
        fill={DEFAULT_NODE_FILL}
        stroke={DEFAULT_NODE_STROKE_COLOR}
        strokeLinejoin={"round"}
        strokeWidth={strokeWidth}
        rx={cornerRadius}
        ry={cornerRadius}
      />
      <path
        d={`M0,0 L${width},0`}
        strokeLinejoin={"round"}
        strokeWidth={strokeWidth}
        stroke={DEFAULT_NODE_STROKE_COLOR}
        transform={`translate(${x + strokeWidth / 2},${y + (dividerLineY ? dividerLineY : height / 2)})`}
      />
    </g>
  );
}

export function TextAnnotationNodeSvg(_props: NodeSvgProps & { showPlaceholder?: boolean }) {
  const { strokeWidth, x, y, width, height, props } = normalize(_props);
  return (
    <g>
      <path
        {...props}
        x={x}
        y={y}
        d={`M20,0 L0,0 M0,0 L0,${height} M0,${height} L20,${height}`}
        stroke={DEFAULT_NODE_STROKE_COLOR}
        strokeWidth={strokeWidth}
        strokeLinejoin={"round"}
        transform={`translate(${x},${y})`}
      />
      {props.showPlaceholder && (
        <text x={"20%"} y={"65%"} style={{ fontSize: "5em", fontWeight: "bold" }}>
          Text
        </text>
      )}
    </g>
  );
}

export function GroupNodeSvg(_props: NodeSvgProps & { strokeDasharray?: string }) {
  const { strokeWidth, x, y, width, height, props } = normalize(_props);
  const strokeDasharray = props.strokeDasharray ?? "5,5";
  const cornerRadius = 40;
  return (
    <g>
      <rect
        {...props}
        x={x}
        y={y}
        width={width}
        height={height}
        fill={DEFAULT_NODE_FILL}
        stroke={DEFAULT_NODE_STROKE_COLOR}
        strokeLinejoin={"round"}
        strokeWidth={strokeWidth}
        strokeDasharray={strokeDasharray}
        rx={cornerRadius}
        ry={cornerRadius}
      />
    </g>
  );
}

export type NodeSvgProps = RF.Dimensions & RF.XYPosition & { strokeWidth?: number };
